package automata;

public class AutomataState implements Comparable<AutomataState> {
	private int id;
	private boolean start;
	private boolean accept;
	
	public AutomataState(int id, boolean start, boolean accept) {
		this.id = id;
		this.start = start;
		this.accept = accept;
	}
	
	/*public AutomataState copyAutomataState(AutomataState state, int nID) {
		return new AutomataState(nID, state.getStart(), state.getAccept());
	}*/
	
	public int getID() {
		return id;
	}
	
	public boolean getStart() {
		return start;
	}
	
	public void setStart(boolean start) {
		this.start = start;
	}
	
	public boolean getAccept() {
		return accept;
	}
	
	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	@Override
	public int compareTo(AutomataState other) {
		return Integer.valueOf(id).compareTo(Integer.valueOf(other.getID()));
	}
	
	@Override
	public int hashCode() {
	    return ((Integer)id).hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other.getClass() == this.getClass() && ((AutomataState)other).getID() == id;
	}
}
