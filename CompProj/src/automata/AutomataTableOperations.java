package automata;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class AutomataTableOperations {
	private static AutomataTable joinTables(AutomataTable first, AutomataTable second, AutomataState[] joinState) {
		AutomataState secondStartState = second.getStartState();

		secondStartState.setStart(false);
		int secontStartID = first.addState(secondStartState);
		for (AutomataState state : joinState) {
			state.setAccept(false);
			first.stateSetEmptyTransition(state.getID(), secontStartID);
		}

		for (	Entry<AutomataState, HashMap<Integer, List<AutomataState>>> entries : 
				second.getStateGrammar().entrySet()) {
			System.out.println("Adding " + entries.getKey());
			int sID;
			if (secondStartState != entries.getKey()) {
				entries.getKey().setStart(false);
				sID = first.addState(entries.getKey());
			} else
				sID = secontStartID;
			first.stateCopyTransitions(sID, entries.getValue());
		}

		return first;
	}

	public static AutomataTable join(AutomataTable first, AutomataTable second) {
		AutomataState[] firstAcceptStates = first.getAcceptStates();
		return joinTables(first, second, firstAcceptStates);
	}

	public static AutomataTable or(AutomataTable first, AutomataTable second) {
		AutomataTable nTable = new AutomataTable(AutomataType.E_NFA);
		AutomataState[] firstAcceptStates = first.getAcceptStates();
		AutomataState firstStartState = first.getStartState();
		AutomataState[] secondAcceptStates = second.getAcceptStates();
		AutomataState secondStartState = second.getStartState();

		AutomataState firstState = new AutomataState(1, true, false);
		nTable.addState(firstState);
		AutomataState[] joinStates = new AutomataState[1];
		joinStates[0] = firstState;

		nTable = joinTables(nTable, first, joinStates);
		nTable = joinTables(nTable, second, joinStates);

		AutomataTable endTable = new AutomataTable(AutomataType.E_NFA);
		endTable.addState(true, true);
		nTable = join(nTable, endTable);

		// int secontStartID = first.addState(false, false);

		return nTable;
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
