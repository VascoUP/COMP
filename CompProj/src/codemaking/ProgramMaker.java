package codemaking;
/**
 * 
 * This interface creates the different programs
 *
 */
public interface ProgramMaker {
	/**
	 * Creates the program's code
	 * @return The program's code
	 */
	String code();
	/**
	 * Writes the program's code into the respective file
	 */
	void toFile();
}
