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
import automata.PrintAutomata;

public class CProgram implements ProgramMaker {
	private static final URL fileURL = JavaProgram.class.getResource("assets/");
	private static final String fileName = "regIdent.c";
	private AutomataTable table;
	
	public CProgram(AutomataTable table) {
		this.table = table;
	}

	@Override
	public String code() {
		StringBuilder builder = new StringBuilder();
		writeCFile(builder);
		return new String(builder);
	}

	@Override
	public void toFile() {
		String code = code();
		System.out.println(code);
		System.out.println(PrintAutomata.getString(table));
		
		File fnew = new File(fileURL.getPath() + fileName);
		FileWriter f2;

		try {
			f2 = new FileWriter(fnew, false);
			f2.write(code);
			f2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	private void writeCFile(StringBuilder text) {
		writeCIncludes(text);
		writeCValidate(text);
		writeCMain(text);
	}

	private void writeCIncludes(StringBuilder text) {
		text.append("#include <stdio.h>\n");
	}

	private void writeCValidate(StringBuilder text){
		text.append("\nint validate(char *exp, int edges[][], int edgesSize){\n");
		text.append("\tint currentState = 0;\n");
		text.append("\tint i;\n");
		text.append("\tint character = (int)exp[0];\n");
		text.append("\tint size = sizeof(exp);\n");
		text.append("\tfor(i = 0 ; i < size; i++){\n");
		text.append("\t\tif(edges[currentState][(int)exp[i]] != -1){\n");
		text.append("\t\t\tcurrentState = edges[currentState][(int)exp[i]];\n");
		text.append("\t\t}\n");
		text.append("\t\telse\n");
		text.append("\t\t\treturn 0;\n");
		text.append("\t}\n");
		text.append("\treturn 1;\n");
		text.append("}");
	}

	private void writeCMain(StringBuilder text) {
		text.append("\nint main(int argc, char* argv[]) {\n");
		text.append("\t\tif(argc != 2){\n");
		text.append("\t\t\tprintf(\"%s\", \"Wrong number of arguments\n\");\n");
		text.append("\t\t\treturn -1;\n");
		text.append("\t\t}\n");
		
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();

		int hashSize = stateGrammar.size();
		
		text.append("\t\tint edges[");
		text.append(hashSize);
		text.append("][256] = {");

		int var = 0;
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();

			if(var != 0)
				text.append(",\n\t\t\t{");
			else
				text.append("\n\t\t\t{");
			
			if(value.get("anyInput") != null) {
				text.append(key.getID());
				text.append(", ");
			}
			else{
				int j;
				for( j = 0; j < 256 ; j++) {
					Set<AutomataState> dst = value.get(String.valueOf((char)j));
					if(dst == null || dst.size() != 1) {
						text.append("-1");
					} else {
						AutomataState st = dst.iterator().next();
						text.append(st.getID());
					}
					if(j + 1 != 256) {
						text.append(", ");
					}
				}
			}
			
			text.append("}");
			
			var++;
		}


		text.append("\t\t};");
				
		text.append("\n\tif(validate(argv[1], edges) == 1)");
		text.append("\t\tprintf(\"%s\", \"DFA match\n\");\n");
		text.append("\telse\n");
		text.append("\t\t\tprintf(\"%s\", \"DFA doesn't match\n\");\n");
		text.append("\n\treturn 0;\n");
		text.append("}\n");
	}
}
