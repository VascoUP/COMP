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

		for (Entry<AutomataState, 
					HashMap<String, List<AutomataState>>> entries : second.getStateGrammar()
				.entrySet()) {
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

		AutomataState firstState = new AutomataState(1, true, false);
		nTable.addState(firstState);
		AutomataState[] joinStates = new AutomataState[1];
		joinStates[0] = firstState;

		nTable = joinTables(nTable, first, joinStates);
		nTable = joinTables(nTable, second, joinStates);

		AutomataTable endTable = new AutomataTable(AutomataType.E_NFA);
		endTable.addState(true, true);
		nTable = join(nTable, endTable);

		return nTable;
	}

	
	public static AutomataTable zeroOrMore(AutomataTable table) {
		AutomataTable nTable = new AutomataTable(AutomataType.E_NFA);
		AutomataState acceptState = table.getAcceptStates()[0];
		AutomataState startState = table.getStartState();

		AutomataState firstState = new AutomataState(1, true, false);
		int firsID = nTable.addState(true, false);
		AutomataState[] joinStates = new AutomataState[1];
		joinStates[0] = firstState;

		nTable = joinTables(nTable, table, joinStates);
		nTable.stateSetEmptyTransition(acceptState.getID(), startState.getID());
		
		int endID = nTable.addState(false, true);
		nTable.stateSetEmptyTransition(acceptState.getID(), endID);
		nTable.stateSetEmptyTransition(firsID, endID);

		acceptState.setAccept(false);
		
		return nTable;
	}

	public static AutomataTable oneOrMore(AutomataTable table) {
		AutomataState acceptState = table.getAcceptStates()[0];
		table.stateSetEmptyTransition(acceptState.getID(), 1);
		acceptState.setAccept(false);

		int endID = table.addState(false, true);
		table.stateSetEmptyTransition(acceptState.getID(), endID);

		return table;
	}

	public static AutomataTable zeroOrOne(AutomataTable table) {
		AutomataTable nTable = new AutomataTable(AutomataType.E_NFA);
		AutomataState acceptState = table.getAcceptStates()[0];
		AutomataState startState = table.getStartState();

		AutomataState firstState = new AutomataState(1, true, false);
		nTable.addState(firstState);
		AutomataState[] joinStates = new AutomataState[1];
		joinStates[0] = firstState;

		nTable = joinTables(nTable, table, joinStates);

		int endID = table.addState(false, true);
		nTable.stateSetEmptyTransition(acceptState.getID(), endID);
		nTable.stateSetEmptyTransition(firstState.getID(), endID);

		return nTable;
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

	
	public static int terminal(AutomataTable table, String input, int fromState) {
		int toState = table.addState(false, true);
		table.stateSetTransition(fromState, input, toState);
		return toState;
	}
}
