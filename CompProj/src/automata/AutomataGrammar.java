package automata;

/**
 * 
 * This class creates a grammar to the automata
 *
 */
public class AutomataGrammar {
	public enum StringGroup {LOWERCASE, UPPERCASE, NUMBER, EPSILON, NONEXISTING};
	
	private static final int lowercaseMin = 97;
	private static final int lowercaseMax = 122;
	private static final int uppercaseMin = 65;
	private static final int uppercaseMax = 90;
	private static final int numberMin = 48;
	private static final int numberMax = 57;

	static final String emptyToken = "epsilon";
	static final String anyInput = "anyInput";

	/**
	 * Gets the StringGroup of a string
	 * @param entry String used to found the StringGroup
	 * @return The StringGroup of the string
	 */
	private static StringGroup getGroup(String entry) {
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
	
	/**
	 * Gets the input range from an initial string to an ending string
	 * @param initRange Initial string
	 * @param endRange Ending string
	 * @return The input range from the initial string to the ending string
	 */
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
