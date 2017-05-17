package codemaking;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import automata.AutomataTable;
import automata.PrintAutomata;

public class JavaProgram implements ProgramMaker {
	private static final URL fileURL = JavaProgram.class.getResource("assets/");
	private static final String fileName = "RegIdent.java";
	private AutomataTable table;
	
	public JavaProgram(AutomataTable table) {
		this.table = table;
	}

	@Override
	public String code() {
		StringBuilder builder = new StringBuilder();
		builder.append("public class RegIdent {\n");
		builder.append("	public static void main(String[] args) {\n");
		builder.append("		System.out.println(\"Hello World!\");\n");
		builder.append("	}\n");
		builder.append("}\n");
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

}
