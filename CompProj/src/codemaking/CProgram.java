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
 * Creates the C program
 *
 */
public class CProgram implements ProgramMaker {
	private static final URL fileURL = JavaProgram.class.getResource("../programs/");
	private static final String fileName = "regIdent.c";
	private AutomataTable table;
	
	/**
	 * CProgram's constructor
	 * @param table Automata's table
	 */
	public CProgram(AutomataTable table) {
		this.table = table;
	}

	/**
	 * Creates the program's code
	 * @return The program's code
	 */
	@Override
	public String code() {
		StringBuilder builder = new StringBuilder();
		writeCFile(builder);
		return new String(builder);
	}

	/**
	 * Writes the program's code into the respective file
	 */
	@Override
	public void toFile() {
		String code = code();

		System.out.println(fileURL.getPath());
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

	/**
	 * Writes the program's code
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeCFile(StringBuilder text) {
		writeCIncludes(text);
		writeCValidate(text);
		writeCMain(text);
	}
	
	/**
	 * Writes the program's includes
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeCIncludes(StringBuilder text) {
		text.append("#include <stdio.h>\n");
		text.append("#include <string.h>\n");
	}

	/**
	 * Writes the validation function
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeCValidate(StringBuilder text){
		text.append("\nint validate(char *exp, int edges[][], int edgesSize){\n");
		text.append("\tint currentState = 0;\n");
		text.append("\tint i;\n");
		text.append("\tint character = (int)exp[0];\n");
		text.append("\tint size = sizeof(exp);\n");
		text.append("\tfor(i = 0 ; i < size; i++){\n");
		text.append("\t\tif(edges[currentState][(int)exp[i]] != -1)\n");
		text.append("\t\t\tcurrentState = edges[currentState][(int)exp[i]];\n");
		text.append("\t\telse\n");
		text.append("\t\t\treturn -1;\n");
		text.append("\t}\n");
		text.append("\treturn currentState;\n");
		text.append("}");
	}

	/**
	 * Write the program's main function
	 * @param text StringBuilder where the code will be placed
	 */
	private void writeCMain(StringBuilder text) {
		text.append("\nint main(int argc, char* argv[]) {\n");
		text.append("\t\tif(argc != 2){\n");
		text.append("\t\t\tprintf(\"Wrong number of arguments\\n\");\n");
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


		text.append("\n\t\t};\n");
		
		text.append("\t\tif(validate(argv[1], edges) == 1)\n");
		text.append("\t\t\tprintf(\"DFA match\\n\");\n");
		text.append("\t\telse\n");
		text.append("\t\t\tprintf(\"DFA doesn't match\\n\");\n");
		text.append("\t\treturn 0;\n");
		text.append("}\n");
	}
}
