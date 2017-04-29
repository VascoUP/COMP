package automata;

import java.util.HashMap;

public class AutomataTable {	
	private HashMap<AutomataState, AutomataState[]> stateGrammar;
	
	private AutomataType type;
	
	private int stateID = 1;
	
	
	public AutomataTable(AutomataType type) {
		this.type = type;
		stateGrammar = new HashMap<>();
	}
	

	
	public HashMap<AutomataState, AutomataState[]> getStateGrammar() {
		return stateGrammar;
	}

	
	public int addState( boolean start, boolean accept ) {
		int id = stateID;
		stateGrammar.put(new AutomataState(stateID++, start, accept), 
							new AutomataState[AutomataGrammar.grammar.size()]);
		return id;
	}
	
	public AutomataState getStateByID(int id) {
		for( AutomataState state : stateGrammar.keySet() )
			if( state.getID() == id )
				return state;
		return null;
	}
	
	
	public boolean stateSetTransition( AutomataState state, String input, AutomataState resultingState ) {
		int index = AutomataGrammar.grammar.indexOf(input);
		if( !stateGrammar.containsKey(resultingState) ||
			!stateGrammar.containsKey(state) || 
			index == -1)
			return false;
		
		AutomataState[] transitions = stateGrammar.get(state);
		transitions[index] = resultingState;
		
		return true;
	}
	
	public boolean stateSetEmptyTransition( AutomataState state, AutomataState resultingState ) {
		if( !stateGrammar.containsKey(resultingState) || 
			!stateGrammar.containsKey(state) || 
			type != AutomataType.E_NFA )
			return false;
		stateSetTransition(state, AutomataGrammar.emptyToken, resultingState);
		return true;
	}
	
	public boolean stateSetAllTransitions( AutomataState state, AutomataState resultingState ) {
		if( !stateGrammar.containsKey(resultingState) ||
			!stateGrammar.containsKey(state) )
			return false;
		
		AutomataState[] transitions = stateGrammar.get(state);
		for( int i = 0; i < transitions.length; i++ ) 
			if( AutomataGrammar.grammar.get(i) != AutomataGrammar.emptyToken )
				transitions[i] = resultingState;
		
		return true;
	}
	

	public boolean stateSetTransition( int id, String input, int dstID ) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetTransition(fState, input, sState);
	}
	
	public boolean stateSetEmptyTransition( int id, int dstID ) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetEmptyTransition(fState, sState);
	}
	
	public boolean stateSetAllTransitions( int id, int dstID ) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetAllTransitions(fState, sState);
	}
	
	
	public static void main(String args[]) {
		AutomataTable table = new AutomataTable(AutomataType.E_NFA);
		AutomataState state = new AutomataState(1, true, true);
		table.addState(true, true);
		table.stateSetAllTransitions(1, 1);
		
		HashMap<AutomataState, AutomataState[]> stateGrammar = table.getStateGrammar();			
		AutomataState[] transitions = stateGrammar.get(state);
		
		int index = AutomataGrammar.grammar.indexOf("H");
		System.out.println(index == -1 ? 
								"input a not found" : (
								transitions[index] == null ? 
										"null" : 
										transitions[index].getID()));
		index = AutomataGrammar.grammar.indexOf("6");
		System.out.println(index == -1 ? 
								"input a not found" : (
								transitions[index] == null ? 
										"null" : 
										transitions[index].getID()));
		
		/*
		table.stateSetTransition(state, "-", state);
		
		stateGrammar = table.getStateGrammar();			
		transitions = stateGrammar.get(state);
		
		index = table.grammar.indexOf("-");
		System.out.println(index == -1 ? "input - not found" : transitions[index]);
		*/
	}
}
