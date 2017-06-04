package automata;

/**
 * 
 * This class creates the different automata's states
 *
 */
public class AutomataState implements Comparable<AutomataState> {
	private int id;
	private boolean start;
	private boolean accept;

	/**
	 * AutomataState's constructor
	 * @param id State's identifier
	 * @param start If it is a start state
	 * @param accept If it is a acceptable state
	 */
	public AutomataState(int id, boolean start, boolean accept) {
		this.id = id;
		this.start = start;
		this.accept = accept;
	}

	/*
	 * public AutomataState copyAutomataState(AutomataState state, int nID) {
	 * return new AutomataState(nID, state.getStart(), state.getAccept()); }
	 */

	/**
	 * Gets the state's identifier
	 * @return The state's identifier
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the state's identifier
	 * @param id New state's identifier
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * Gets the start variable's value
	 * @return The start variable's value
	 */
	public boolean getStart() {
		return start;
	}

	/**
	 * Sets the start variable's value
	 * @param start The new value of the start's variable
	 */
	public void setStart(boolean start) {
		this.start = start;
	}

	/**
	 * Gets the accept variable's value
	 * @return The accept variable's value
	 */
	public boolean getAccept() {
		return accept;
	}

	/**
	 * Sets the accept variable's value
	 * @param start The new value of the accept's variable
	 */
	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	/**
	 * Compares the states of an automata
	 * @param other The other state that will be compared
	 * @return 0 if this state's identifier is equal to the other state's identifier; a value less than 0 if this state's identifier is numerically less than the other state's identifier; and a value greater than 0 if this state's identifier is numerically greater than the argument other state's identifier 
	 */
	@Override
	public int compareTo(AutomataState other) {
		return Integer.valueOf(id).compareTo(Integer.valueOf(other.getID()));
	}

	/**
	 * Gets the hash code for the state's identifier
	 * @return The hash code for the state's identifier
	 */
	@Override
	public int hashCode() {
		return ((Integer) id).hashCode();
	}

	/**
	 * Verifies if the state's identifier is equal to the other state's identifier
	 * @param other The other automata state
	 * @return true if the identifiers are equal, false otherwise
	 */
	@Override
	public boolean equals(Object other) {
		return other.getClass() == this.getClass() && ((AutomataState) other).getID() == id;
	}
}
