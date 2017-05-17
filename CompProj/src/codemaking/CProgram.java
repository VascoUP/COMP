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
			f2 = new FileWriter(fnew,false);
			f2.write(code);
			f2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	private void writeCPPFile(StringBuilder text) {
		writeCPPIncludes(text);

	}

	private void writeCPPIncludes(StringBuilder text) {
		text.append("#include <iostream>\n")
		.append("#include <map>\n")
		.append("#include <set>\n")
		.append("\nusing namespace std;\n");
	}


}
