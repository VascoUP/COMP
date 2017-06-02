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

	private void writeJavaFile(StringBuilder text){
		text.append("public class RegIdent {\n");
		writeJavaImports(text);
		writeJavaComments(text);
		writeJavavalidator(text);
		writeJavaMain(text);
		text.append("}");
	}

	private void writeJavaMain(StringBuilder text){
		text.append("\n\tpublic static void main(String[] args) {\n");
		text.append("\n\t\tString string;\n");
		text.append("\t\tScanner input = new Scanner(System.in);\n");
		text.append("\n\t\twhile(true) {\n");
		text.append("\t\t\ttry {\n");
		text.append("\t\t\t\tstring = input.nextLine();\n");
		text.append("\t\t\t} catch (NoSuchElementException e) {\n");
		text.append("\t\t\t\tbreak;\n");
		text.append("\t\t\t}\n");
		text.append("\n\t\t\tif(validator(string))\n");
		text.append("\t\t\t\tSystem.out.println(\"DFA matches!\");\n");
		text.append("\t\t\telse\n");
		text.append("\t\t\t\tSystem.out.println(\"DFA does not match!\");\n");
		text.append("\t\t}\n");
		text.append("\n\t\tinput.close();\n");
		text.append("\t}\n");
	}

	private void writeJavaComments(StringBuilder text) {
		text.append("\n\t/*\n\n");
		text.append(toString());
		text.append("\n\t*/\n");
	}

	private void writeJavaImports(StringBuilder text) {
		text.append("import java.util.HashMap;\n");
		text.append("import java.util.HashSet;\n");
		text.append("import java.util.Map;\n");
		text.append("import java.util.NoSuchElementException;\n");
		text.append("import java.util.Scanner;\n");
		text.append("import java.util.Set;\n");
	}

	public void writeJavavalidator(StringBuilder text){
		AutomataState state;
		AutomataState[] acceptStates = table.getAcceptStates();
		text.append("public static boolean validator(String str) {\n");
		/*text.append("\tint state =").append(Integer.table.getStartState()).append(", nextState;\n");*/
		text.append("\tboolean finalStatePresent = false;\n");
		text.append("\tfor(Character c: str.toCharArray()) {\n");
		/*text.append("\t\tnextState = ").append(table.getStateInputTransitions(c, state).next()).append(";\n");*/
		text.append("\t\tif(nextState == null || nextState == -1)\n");
		text.append("\t\t\treturn false;\n");
		text.append("\t}");

		text.append("\tfor(int fState: ){\n");
		text.append("\t\tif(nextState == fState)\n");
		text.append("\t\t\tfinalStatePresent = true;\n");
		text.append("\t}");
		text.append("\tif(finalStatePresent)\n");
		text.append("\t\treturn true;\n");
		text.append("\telse\n");
		text.append("\t\treturn false;\n");
		text.append("}\n");
	}

}
