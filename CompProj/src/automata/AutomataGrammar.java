package automata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class AutomataGrammar {
	private static final URL grammarFile = AutomataTable.class.getClassLoader().getResource("assets/Grammar");
	
	public static final String emptyToken = "?";
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
}
