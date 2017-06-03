package codemaking;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

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

	private void writeCFile(StringBuilder text) {
		writeCIncludes(text);
		writeCValidate(text);
		writeCMain(text);
	}

	private void writeCIncludes(StringBuilder text) {
		text.append("#include <iostream>\n")
		.append("#include <stdio.h>\n")
		.append("#include <string>\n")
		.append("#include <vector>\n");
	}

	public void writeCValidate(StringBuilder text){
		text.append("\nbool validate(string exp, vector<vector<int>>edges){\n");
		text.append("\tint curr_state = edges[0][0];\n");
		text.append("\tfor(int i=0; i<exp.size();i++){\n");
		text.append("\t\tint caracter = (int)exp[i];\n");
		text.append("\t\tif (edges[curr_state][caracter] != -1)\n");
		text.append("\t\t\tcurr_state = edges[curr_state][caracter];\n");
		text.append("\t\telse\n");
		text.append("\t\t\treturn false;\n");
		text.append("\t{\n");
		text.append("\treturn true;\n");
		text.append("}");
	}

	private void writeCMain(StringBuilder text) {
		text.append("\nint main() {\n");
		text.append("\n\tstring str;\n");
		text.append("\n\twhile(str != \"quit\") {\n");
		text.append("\t\tscanf('%s', str);\n");
		text.append("\n\t\tif(validate(str))\n");
		text.append("\t\t\tprintf('%s', 'DFA match');\n");
		text.append("\t\telse\n");
		text.append("\t\t\tprintf('%s', 'DFA doesn't match');\n");
		text.append("\t}\n");
		text.append("\n\treturn 0;\n");
		text.append("}\n");
	}
}
