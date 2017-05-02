package automata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class AutomataGrammar {
	private static final URL grammarFile = AutomataTable.class.getClassLoader().getResource("assets/Grammar");
	
	public static enum StringGroup {LOWERCASE, UPPERCASE, NUMBER, EPSILON, NONEXISTING};

	public static final String emptyToken = "epsilon";
	public static final ArrayList<String> grammar = getGrammarFromFile();

	private static final ArrayList<String> getGrammarFromFile() {
		ArrayList<String> nList = new ArrayList<>();

		try {
			FileReader fileReader = new FileReader(grammarFile.getFile());
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null)
				nList.add(line);
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		nList.add(emptyToken);
		return nList;
	}
	
	public static StringGroup getGroup(String entry) {
		if( entry.equals(emptyToken) )
			return StringGroup.EPSILON;
		
		int index = grammar.indexOf(entry);
		if( index >= 0 && index < 26 )
			return StringGroup.LOWERCASE;
		else if( index >= 26 && index < 52 )
			return StringGroup.UPPERCASE;
		else if( index >= 52 )
			return StringGroup.NUMBER;
		
		return StringGroup.NONEXISTING;
	}
	
	public static String[] getRangeInput(String initRange, String endRange) {
		int indexInit = grammar.indexOf(initRange);
		int indexEnd = grammar.indexOf(endRange);
		if( getGroup(initRange) != getGroup(endRange) || indexInit >= indexEnd )
			return null;
		String[] arr = new String[indexEnd - indexInit + 1];
		for( int i = indexInit, arrIndex = 0; i <= indexEnd; i++, arrIndex++ )
			arr[arrIndex] = grammar.get(i);
		return arr;
	}
	
	
	
	
	
	
	
	
	
	
	
}
