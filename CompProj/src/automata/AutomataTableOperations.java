package automata;

import java.util.Map.Entry;

public class AutomataTableOperations {
	public static AutomataTable join(AutomataTable first, AutomataTable second) {
		AutomataState[] firstAcceptStates = first.getAcceptStates();
		AutomataState secondStartState = second.getStartState();
		
		int secontStartID = first.addState(false, secondStartState.getAccept());
		for( AutomataState state : firstAcceptStates ) {
			state.setAccept(false);
			first.stateSetEmptyTransition(state.getID(), secontStartID);
		}
		
		for(Entry<AutomataState, AutomataState[]> entries : second.getStateGrammar().entrySet()) {
			int sID;
			if( secondStartState != entries.getKey() )
				sID = first.addState(false, entries.getKey().getAccept());
			else 
				sID = secontStartID;
			first.stateCopyTransitions(sID, entries.getValue());
		}
		
		return first;
	}
	
	public static AutomataTable or(AutomataTable first, AutomataTable second) {
		return first;
	}
	
	public static AutomataTable zeroOrMore(AutomataTable first) {
		return first;
	}
	
	public static AutomataTable oneOrMore(AutomataTable first) {
		return first;
	}
	
	public static AutomataTable zeroOrOne(AutomataTable first) {
		return first;
	}
	
	public static AutomataTable nToM(AutomataTable first, int n, int m) {
		return first;
	}
	
	public static AutomataTable nToMax(AutomataTable first, int n) {
		return first;
	}
	
	public static AutomataTable maxToM(AutomataTable first, int m) {
		return first;
	}
	
	public static void terminal(AutomataTable table, String input, int fromState) {
		int toState = table.addState(false, true);
		table.stateSetTransition(fromState, input, toState);
	}
}
