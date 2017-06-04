package automata;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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

		for (Entry<	AutomataState, 
					HashMap<String, Set<AutomataState>>> entries : 
				second.getStateGrammar().entrySet()) {
			
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
	 * OR operation between tables
	 * @param first First automata's table
	 * @param second Second's automata's table
	 * @return The new automata's table
	 */
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
		//AutomataState startState = table.getStartState();

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
	 * @param first First automata's table
	 * @param n First value
	 * @param m Second value
	 * @return The new automata's table
	 */
	public static AutomataTable nToM(AutomataTable first, int n, int m) {
		return first;
	}


	/**
	 * Range operation
	 * @param first First automata's table
	 * @param n First value
	 * @return The new automata's table
	 */
	public static AutomataTable nToMax(AutomataTable first, int n) {
		return first;
	}


	/**
	 * Range operation
	 * @param first First automata's table
	 * @param max Max value
	 * @param m Second value
	 * @return The new automata's table
	 */
	public static AutomataTable maxToM(AutomataTable first, int m) {
		return first;
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
