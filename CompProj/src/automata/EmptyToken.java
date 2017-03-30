package automata;

public class EmptyToken {
	private boolean used;
	public boolean e_nfa;
	
	public EmptyToken(AutomataType type) {
		this.e_nfa = (type != AutomataType.E_NFA);
		this.used = false;
	}
	
	public void setUsed() throws EmptyStringError {
		if(e_nfa)
			throw new EmptyStringError("Finite automatas cannot support the empty string");
		used = true;
	}
	
	public boolean getUsed() {
		return used;
	}
}
