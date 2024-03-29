package automata;

import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * This class creates the operations for the automata's table
 *
 */
public class AutomataTableOperations {
	/**
	 * Joins the different tables
	 * @param first First automata's table
	 * @param second Second automata's table
	 * @param joinState Array with the joined states
	 * @return The automata's table created
	 */
	private static AutomataTable joinTables(AutomataTable first, AutomataTable second, AutomataState[] joinState) {
		AutomataState secondStartState = second.getStartState();

		secondStartState.setStart(false);
		int secontStartID = first.addState(secondStartState);
		for (AutomataState state : joinState) {
			state.setAccept(false);
			first.stateSetEmptyTransition(state.getID(), secontStartID);
		}

		for (Entry<	AutomataState, HashMap<String, Set<AutomataState>>> entries :
				second.getStateGrammar().entrySet()) {
			if (secondStartState != entries.getKey()) {
				entries.getKey().setStart(false);
				first.addState(entries.getKey());
			}
		}

		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> entries :
				second.getStateGrammar().entrySet()) {
			first.stateCopyTransitions(entries.getKey(), entries.getValue());
		}

		return first;
	}

	/**
	 * Joins different tables
	 * @param first First automata's table
	 * @param second Second automata's table
	 * @return The automata's tables created
	 */
	public static AutomataTable join(AutomataTable first, AutomataTable second) {
		AutomataState[] firstAcceptStates = first.getAcceptStates();
		return joinTables(first, second, firstAcceptStates);
	}
	
	/**
	 * OR operation
	 * @param tables List with automatas' tables
	 * @return The new automata's table
	 */
	public static AutomataTable or(List<AutomataTable> tables) {
		if( tables.size() == 1 )
			return tables.get(0);
		
		AutomataTable nTable = new AutomataTable(AutomataType.E_NFA);

		AutomataState firstState = new AutomataState(1, true, false);
		nTable.addState(firstState);
		AutomataState[] joinStates = new AutomataState[1];
		joinStates[0] = firstState;

		for( AutomataTable t : tables )
			nTable = joinTables(nTable, t, joinStates);			

		AutomataTable endTable = new AutomataTable(AutomataType.E_NFA);
		endTable.addState(true, true);
		nTable = join(nTable, endTable);

		return nTable;
	}

	/**
	 * Zero or More operation
	 * @param table Automata's table
	 * @return The new automata's table
	 */
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

	/**
	 * One or More operation
	 * @param table Automata's table
	 * @return The new automata's table
	 */
	public static AutomataTable oneOrMore(AutomataTable table) {
		AutomataState acceptState = table.getAcceptStates()[0];
		table.stateSetEmptyTransition(acceptState.getID(), 1);
		acceptState.setAccept(false);

		int endID = table.addState(false, true);
		table.stateSetEmptyTransition(acceptState.getID(), endID);

		return table;
	}

	/**
	 * Zero or One operation
	 * @param table Automata's table
	 * @return The new automata's table
	 */
	public static AutomataTable zeroOrOne(AutomataTable table) {
		AutomataTable nTable = new AutomataTable(AutomataType.E_NFA);
		AutomataState acceptState = table.getAcceptStates()[0];

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

	/**
	 * Range operation
	 * @param table First automata's table
	 * @param n First value
	 * @param m Second value
	 * @return The new automata's table
	 */
	public static AutomataTable nToM(AutomataTable table, int n, int m) {
		if(n > m)
			return null;
        AutomataTable result;
        Set<Integer> finalStates = new HashSet<>();
        int index;

		if(n > 0) {
            result = new AutomataTable(table);
            for (int i = 1; i < n; i++) {
                result = join(result, new AutomataTable(table));
            }
            index = n;
        } else {
            result = new AutomataTable(table);
            finalStates.add(result.getStartState().getID());
            index = 1;
        }

        for (; index < m; index++) {
            finalStates.addAll(result.getAcceptStatesID());
            result = join(result, new AutomataTable(table));
        }
        for (int finalStateID : finalStates)
            result.getStateByID(finalStateID).setAccept(true);
        return result;
	}


	/**
	 * Range operation
	 * @param table First automata's table
	 * @param n First value
	 * @return The new automata's table
	 */
	public static AutomataTable nToMax(AutomataTable table, int n) {
        AutomataTable result = nToM(table, n, n);
        if(result == null) return null;
        table = zeroOrMore(table);
		return join(result, table);
	}


	/**
	 * Range operation
	 * @param table First automata's table
	 * @param m Max value
	 * @return The new automata's table
	 */
	public static AutomataTable toM(AutomataTable table, int m) {
	    return nToM(table, 0, m);
	}

	/**
	 * Terminal operation
	 * @param table Automata's table
	 * @param input Input string
	 * @param fromState Source state
	 * @return The destination state's identifier
	 */
	public static int terminal(AutomataTable table, String input, int fromState) {
		int toState = table.addState(false, true);
		if( input.equals(".") )
			table.stateSetAllTransitions(fromState, toState);
		else
			table.stateSetTransition(fromState, input, toState);
		return toState;
	}
}
