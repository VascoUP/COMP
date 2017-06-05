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

/**
 * 
 * This class creates the Java program
 *
 */
public class JavaProgram implements ProgramMaker {
	private static final URL fileURL = JavaProgram.class.getResource("../programs/");
	private static final String fileName = "RegIdent.java";
	private AutomataTable table;
	
	/**
	 * JavaProgram's constructor
	 * @param table Automata's table
	 */
	public JavaProgram(AutomataTable table) {
		this.table = table;
	}

	/**
	 * Creates the program's code
	 * @return The program's code
	 */
	@Override
	public String code() {
		StringBuilder builder = new StringBuilder();
		writeJavaFile(builder);
		return new String(builder);
	}

	/**
	 * Writes the program's code into the respective file
	 */
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

	/**
	 * Writes the program's code
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeJavaFile(StringBuilder text){
		writeJavaImports(text);
		text.append("public class RegIdent {\n");
		writeJavaValidator(text);
		writeJavaMain(text);
		text.append("}");
	}

	/**
	 * Write the program's main function
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeJavaMain(StringBuilder text){
		text.append("\n\n\tpublic static void main(String[] args) {\n");
		text.append("\t\tString string = args[0];\n");
		
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();

		text.append("\t\tint[][] edges = {");
		boolean addComma = false;
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();

			if(addComma)
				text.append(",");
			else
				addComma = true;
			text.append("\n\t\t\t{");
			
			if(value.get("anyInput") != null) {
				for( int j = 0; j < 256 ; j++) {
					text.append(key.getID()-1);
					if(j + 1 != 256)
						text.append(", ");
				}
			} else{
				for( int j = 0; j < 256 ; j++) {
					Set<AutomataState> dst = value.get(String.valueOf((char)j));
					if(dst == null || dst.size() != 1) {
						text.append("-1");
					} else {
						AutomataState st = dst.iterator().next();
						text.append(st.getID()-1);
					}
					
					if(j + 1 != 256) {
						text.append(", ");
					}
				}
			}

			text.append("}");
		}
		text.append("\n\t\t};\n");
		
		AutomataState acceptStates[] = table.getAcceptStates();
		text.append("\n\t\tint[] finalEdges = {");
		addComma = false;
		for(AutomataState state : acceptStates) {
			if(addComma)
				text.append(", ");
			else
				addComma=true;
			text.append(state.getID()-1);
		}
		text.append("};\n");
		
		text.append("\t\tint i;\n");
		text.append("\t\tint result = validator(string, edges);\n");
		text.append("\t\tfor(i = 0; i < finalEdges.length; i++) {\n");
		text.append("\t\t\tif(result == finalEdges[i]) {\n");
		text.append("\t\t\t\tSystem.out.println(\"String matches the regular expression\\n\");\n");
		text.append("\t\t\t\treturn;\n");
		text.append("\t\t\t}\n");
		text.append("\t\t}\n");
		text.append("\t\tSystem.out.println(\"String doesn\'t match the regular expression\\n\");\n");
		text.append("\t}\n");
	}

	/**
	 * Writes the program's imports
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeJavaImports(StringBuilder text) {}

	/**
	 * Writes the validation function
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeJavaValidator(StringBuilder text){
		text.append("\tprivate static int validator(String str, int[][] edges) {\n");
		text.append("\t\tint currentState = ").append(table.getStartState().getID() - 1).append(";\n");
		text.append("\t\tfor(int i = 0; i < str.length(); i++) {\n");
		text.append("\t\t\tif(edges[currentState][(int)str.charAt(i)] != -1)\n");
		text.append("\t\t\t\tcurrentState = edges[currentState][(int)str.charAt(i)];\n");
		text.append("\t\t\telse\n");
		text.append("\t\t\t\treturn -1;\n");
		text.append("\t\t}\n");
		text.append("\t\treturn currentState;\n");
		text.append("\t}");
	}
	

}
