package codemaking;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import automata.AutomataState;
import automata.AutomataTable;

public class JavaProgram implements ProgramMaker {
	private static final URL fileURL = JavaProgram.class.getResource("../programs/");
	private static final String fileName = "RegIdent.java";
	private AutomataTable table;
	
	public JavaProgram(AutomataTable table) {
		this.table = table;
	}

	@Override
	public String code() {
		StringBuilder builder = new StringBuilder();
		writeJavaFile(builder);
		return new String(builder);
	}

	@Override
	public void toFile() {
		String code = code();
		
		File fnew = new File(fileURL.getPath() + fileName);
		FileWriter f2;

		try {
		    f2 = new FileWriter(fnew,false);
		    f2.write(code);
		    f2.close();
		} catch (IOException e) {
	        e.printStackTrace();
		}  
	}

	private void writeJavaFile(StringBuilder text){
		writeJavaImports(text);
		text.append("public class RegIdent {\n");
		writeJavaValidator(text);
		writeJavaMain(text);
		text.append("}");
	}

	private void writeJavaMain(StringBuilder text){
		text.append("\n\n\tpublic static void main(String[] args) {\n");
		text.append("\t\tString string = args[0];\n");
		
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();

		int hashSize = stateGrammar.size();
		
		text.append("\t\tArrayList<ArrayList<Integer>> edges= new ArrayList<ArrayList<Integer>>();\n");
		text.append(hashSize);

		boolean mapCreated = false;
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();

			if(!mapCreated)
				text.append("\t\tArrayList<Integer> map = new ArrayList<Integer>();\n");
			else
				text.append("\t\tmap.clear();\n");
			
			if(value.get("anyInput") != null){
				text.append("\t\tmap.add(");
				text.append(key.getID());
				text.append(");\n");
			}
			else{
				int j;
				for( j = 0; j < 256 ; j++) {
					Set<AutomataState> dst = value.get(String.valueOf((char)j));
					if(dst == null || dst.size() != 1) {
						text.append("\t\tmap.add(");
						text.append(-1);
						text.append(");\n");
					} else {
						AutomataState st = dst.iterator().next();
						text.append("\t\tmap.add(");
						text.append(st.getID());
						text.append(");\n");
					}
				}
			}

			text.append("\t\tedges.add(map);\n");
		}
		
		text.append("\t\tif(validator(string, edges))\n");
		text.append("\t\t\tSystem.out.println(\"DFA matches!\");\n");
		text.append("\t\telse\n");
		text.append("\t\t\tSystem.out.println(\"DFA does not match!\");\n");
		text.append("\t}\n");
	}

	
	private void writeJavaImports(StringBuilder text) {
		text.append("import java.util.ArrayList;\n");
	}

	public void writeJavaValidator(StringBuilder text){
		text.append("\tpublic static boolean validator(String str, ArrayList<ArrayList<Integer>> edges) {\n");
		text.append("\t\tint currentState = ").append(table.getStartState().getID()).append(";\n");
		text.append("\t\tint character = (int)str.charAt(0);\n");
		text.append("\t\tfor(int i = 0; i < str.length(); i++) {\n");
		text.append("\t\t\tif(edges.get(currentState).get((int)str.charAt(i)) != -1)\n");
		text.append("\t\t\t\tcurrentState = edges.get(currentState).get((int)str.charAt(i));\n");
		text.append("\t\t\telse\n");
		text.append("\t\t\t\treturn false;\n");
		text.append("\t\t}\n");
		text.append("\t\treturn true;\n");
		text.append("\t}");
	}
	
	

}
