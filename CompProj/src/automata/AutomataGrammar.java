package automata;


public class AutomataGrammar {
	//private static final URL grammarFile = AutomataTable.class.getClassLoader().getResource("assets/Grammar");
	
	public static enum StringGroup {LOWERCASE, UPPERCASE, NUMBER, EPSILON, NONEXISTING};
	
	public static final int lowercaseMin = 97;
	public static final int lowercaseMax = 122;
	public static final int uppercaseMin = 65;
	public static final int uppercaseMax = 90;
	public static final int numberMin = 48;
	public static final int numberMax = 57;

	public static final String emptyToken = "epsilon";
	//public static final ArrayList<String> grammar = getGrammarFromFile();

	/*private static final ArrayList<String> getGrammarFromFile() {
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
	}*/
	
	public static StringGroup getGroup(String entry) {
		if( entry.equals(emptyToken) )
			return StringGroup.EPSILON;
		
		char c = entry.charAt(0);
        System.out.printf("%c = %d\n", c, (int)c);
		
		if( (int)c >= lowercaseMin && (int)c <= lowercaseMax )
			return StringGroup.LOWERCASE;
		else if( (int)c >= uppercaseMin && (int)c <= uppercaseMax )
			return StringGroup.UPPERCASE;
		else if( (int)c >= numberMin && (int)c <= numberMax )
			return StringGroup.NUMBER;
		
		return StringGroup.NONEXISTING;
	}
	
	public static String[] getRangeInput(String initRange, String endRange) {
		int indexInit = (int) initRange.charAt(0);
		int indexEnd = (int) endRange.charAt(0);
		if( getGroup(initRange) != getGroup(endRange) || indexInit >= indexEnd )
			return null;
		String[] arr = new String[indexEnd - indexInit + 1];
		for( int i = indexInit, arrIndex = 0; i <= indexEnd; i++, arrIndex++ )
			arr[arrIndex] = String.valueOf(Character.toChars(i));
		return arr;
	}
	
	
	
	
	
	
	
	
	
	
	
}
