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
		builder.append("#include<stdio.h>\n\n");
		builder.append("int main(int argc, char* argv[]) {\n");
		builder.append("	printf(\"Hello World!\\n\");\n");
		builder.append("	return 0;\n");
		builder.append("}\n");
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

	private String writeCFile(StringBuilder text) {
		StringBuilder builder = new StringBuilder();
		
		writeCIncludes(builder);
		writeCValidate(builder);
		writeCMain(builder);
		
		return new String(builder);
	}

	private void writeCIncludes(StringBuilder text) {
		text.append("#include <stdio.h>\n");
		text.append("#include <string.h>\n");
	}

	public void writeCValidate(StringBuilder text){
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();

		int hashSize = stateGrammar.size();
		
		text.append("\nint validate(char *exp, int edges[");
		text.append(hashSize);
		text.append("][256]){\n");
		text.append("\tint currentState = edges[0][0];\n");
		text.append("\tint i;\n");
		text.append("\t\tint character = (int)exp[0];\n");
		text.append("\t\tint size = sizeof(exp);\n");
		text.append("\tfor(i = 0 ; i < size; i++){\n");
		text.append("\t\tif(edges[currentState][character] != -1){\n");
		text.append("\t\t\tcurrentState = edges[currentState][character];\n");
		text.append("\t\t\tcharacter = (int)exp[i];\n");
		text.append("\t\t}\n");
		text.append("\t\telse\n");
		text.append("\t\t\treturn 0;\n");
		text.append("\t}\n");
		text.append("\treturn 1;\n");
		text.append("}");
	}

	private void writeCMain(StringBuilder text) {
		text.append("\nint main(int argc, char* argv[]) {\n");
		text.append("\n\tif(argc != 2){\n");
		text.append("\n\t\tprintf(\"%s\", \"Wrong number of arguments\n\");\n");
		text.append("\t\t\treturn -1;\n");
		text.append("\t}\n");
		
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();

		int hashSize = stateGrammar.size();
		
		text.append("\tint edges[");
		text.append(hashSize);
		text.append("][256];\n");
		
		int var = 0;
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();
		
			text.append("\tint map[256] = {");
			
			if(key.equals("anyInput")){
				text.append(key.getID());
				text.append(" , ");
			}
			else{
				int j;
				for( j = 0; j < 256 ; j++){
					for (Entry<String, Set<AutomataState>> entries : value.entrySet()) {
						if(entries.getValue().isEmpty()){
							text.append(" -1 ");
							text.append(" , ");	
						}
						else{
							for (AutomataState inputResult : entries.getValue()) {
								text.append(inputResult.getID());
								text.append(" , ");	
							}
						}
					}
				}
			}
			
			text.append("}\n");
			text.append("memcpy(edges[");
			text.append(var);
			text.append("], map, strlen(map)+1;\n");
			
			var++;
		}
				
		text.append("\n\tif(validate(argv[1], edges) == 1)");
		text.append("\t\tprintf(\"%s\", \"DFA match\n\");\n");
		text.append("\telse\n");
		text.append("\t\t\tprintf(\"%s\", \"DFA doesn't match\n\");\n");
		text.append("\n\treturn 0;\n");
		text.append("}\n");
	}
}
