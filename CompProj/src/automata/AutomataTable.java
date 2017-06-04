package automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * This class creates an automata table
 *
 */
public class AutomataTable {
	private HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar;

	private AutomataType type;

	private int stateID = 1;

	/**
	 * AutomataTable's constructor
	 * @param type Represents the automata's type
	 */
	public AutomataTable(AutomataType type) {
		this.type = type;
		stateGrammar = new HashMap<>();
	}

	/**
	 * Gets the state's grammar
	 * @return The state's grammar
	 */
	public HashMap<AutomataState, HashMap<String, Set<AutomataState>>> getStateGrammar() {
		return stateGrammar;
	}

	/**
	 * Gets the state grammar's size
	 * @return The state grammar's size
	 */
	public int getAutomataSize() {
		return stateGrammar.size();
	}

	/**
	 * Gets the AutomataState corresponding to the identifier passed
	 * @param id Identifier that will be used to discover the AutomataState
	 * @return The AutomataState corresponding to the identifier passed
	 */
	public AutomataState getStateByID(int id) {
		for (AutomataState state : stateGrammar.keySet())
			if (state.getID() == id)
				return state;
		return null;
	}

	/**
	 * Gets the accepted states
	 * @return The accepted states
	 */
	public AutomataState[] getAcceptStates() {
		ArrayList<AutomataState> acceptStates = new ArrayList<>();
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> entries : stateGrammar.entrySet())
			if (entries.getKey().getAccept())
				acceptStates.add(entries.getKey());
		return acceptStates.toArray(new AutomataState[acceptStates.size()]);
	}

	/**
	 * Gets the start state from the automata's table
	 * @return The start state from the automata's table
	 */
	public AutomataState getStartState() {
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> entries : stateGrammar.entrySet())
			if (entries.getKey().getStart())
				return entries.getKey();
		return null;
	}

	/**
	 * Gets the state's identifier
	 * @return The state's identifier
	 */
	public int getStateID() {
		return stateID;
	}

	/**
	 * Gets the transitions from a state
	 * @param input Input string
	 * @param state Automata's state
	 * @return The transitions from a state
	 */
	public Set<AutomataState> getStateInputTransitions(String input, AutomataState state) {
		HashMap<String, Set<AutomataState>> hash = stateGrammar.get(state);
		if (hash == null) 
			return null;
		return hash.get(input);
	}

	/**
	 * Gets the state's transitions
	 * @param state Automata's state
	 * @return The state's transitions
	 */
	public Set<AutomataState> getStateTransitions(AutomataState state) {
	    HashMap<String, Set<AutomataState>> inputTransitions = stateGrammar.get(state);
	    Set<AutomataState> transitions = new HashSet<>();
	    for(Set<AutomataState> iT : inputTransitions.values())
	        transitions.addAll(iT);
	    return transitions;
    }

	/**
	 * Gets the E closure's states
	 * @param state Automata's states
	 * @return The E closure's states
	 */
	public SortedSet<AutomataState> getEClosure(AutomataState state) {
		List<AutomataState> states = new ArrayList<>();
		states.add(state);
		return getEClosure(states);
	}
	
	/**
	 * Gets the E closure's states
	 * @param states Collection of automata's states
	 * @return The E closure's states
	 */
	public SortedSet<AutomataState> getEClosure(Collection<AutomataState> states) {
		Queue<AutomataState> queueStates = new PriorityQueue<>();
		queueStates.addAll(states);
		
		SortedSet<AutomataState> processedStates = new TreeSet<>();
		while (!queueStates.isEmpty()) {
			AutomataState state = queueStates.remove();
			if( processedStates.contains(state) )
				continue;
			processedStates.add(state);
			Set<AutomataState> emptyTransitions = getStateInputTransitions(AutomataGrammar.emptyToken, state);
			if( emptyTransitions == null )
				continue;
			queueStates.addAll(emptyTransitions);
		}
		return processedStates;
	}

	/**
	 * Remove a state from the state's grammar
	 * @param id State's identifier
	 */
	public void removeState(int id) {
		AutomataState state = getStateByID(id);
		stateGrammar.remove(state);
	}
	
	/**
	 * Adds a state to the state's grammar
	 * @param start Star's variable
	 * @param accept Accept's variable
	 * @return The state added identifier
	 */
	public int addState(boolean start, boolean accept) {
		int id = stateID;
		stateGrammar.put(new AutomataState(stateID++, start, accept), 
						new HashMap<>());
		return id;
	}

	/**
	 * Adds a state to the state's grammar
	 * @param state New state to add to the state's grammar
	 * @return The state added identifier
	 */
	public int addState(AutomataState state) {
		int id = stateID;
		state.setID(stateID++);
		stateGrammar.put(state, new HashMap<>());
		return id;
	}

	/**
	 * Verifies if it's possible to add a transaction
	 * @param fromState Source state
	 * @param toState Destination state
	 * @return true if it's possible, false otherwise
	 */
	private boolean verifyAddTransition(AutomataState fromState, AutomataState toState) {
        HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(fromState);
        Set<AutomataState> states = transitions.get(AutomataGrammar.anyInput);
        return states != null && states.contains(toState);
    }

	/**
	 * Verifies if it's possible to add a transaction to a state
	 * @param fromState Source state
	 * @param toState Destination state
	 * @param input Input string
	 * @return true if it's possible, false otherwise
	 */
	private boolean stateAddTransition(AutomataState fromState, AutomataState toState, String input) {
	    if(verifyAddTransition(fromState, toState))
	        return false;

        HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(fromState);
        Set<AutomataState> inputT = transitions.get(input);
        if (inputT == null) {
            inputT = new HashSet<>();
            inputT.add(toState);
            transitions.put(input, inputT);
        } else
            inputT.add(toState);
        return true;
    }

	/**
	 * Verifies if it's possible to copy the state's transition
	 * @param state Automata's state
	 * @param copy HashMap with the state's transitions
	 * @return true if it's possible, false otherwise
	 */
	public boolean stateCopyTransitions(AutomataState state, HashMap<String, Set<AutomataState>> copy) {
		if (state == null)
			return false;

		HashMap<String, Set<AutomataState>> currTransitions = stateGrammar.get(state);
		if (currTransitions == null)
			return false;

		for (Entry<String, Set<AutomataState>> entries : copy.entrySet()) {
			Set<AutomataState> inputT = currTransitions.get(entries.getKey());
			if (inputT == null) {
				inputT = new HashSet<>();
				inputT.addAll(entries.getValue());
				currTransitions.put(entries.getKey(), inputT);
			} else {
				inputT.addAll(entries.getValue());
			}
		}

		stateGrammar.put(state, currTransitions);

		return true;
	}

	/**
	 * Sets the state's transitions
	 * @param state Automata's state
	 * @param input Input string
	 * @param resultingState Resulting automata's state
	 * @return true if it possible, false otherwise
	 */
	public boolean stateSetTransition(AutomataState state, String input, AutomataState resultingState) {
		return stateAddTransition(state, resultingState, input);
	}

	/**
	 * Sets an empty transition
	 * @param state Automata's state
	 * @param resultingState Resulting automata's state
	 * @return true if it's possible, false otherwise
	 */
	public boolean stateSetEmptyTransition(AutomataState state, AutomataState resultingState) {
        return type == AutomataType.E_NFA && stateSetTransition(state, AutomataGrammar.emptyToken, resultingState);
    }

	/**
	 * Sets the transitions of the string' array
	 * @param state Automata's state
	 * @param resultingState Resulting automata's state
	 * @param arr Array of string
	 * @return true if it's possible, false otherwise
	 */
	public boolean stateSetArrTransition(AutomataState state, AutomataState resultingState, String[] arr) {
		HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(state);
		for( String str : arr )
		    if( !stateSetTransition(state, str, resultingState) )
		        break;
		return true;
	}
	
	/**
	 * Sets all transitions
	 * @param state Automata's state
	 * @param resultingState Resulting automata's state
	 * @return true if it's possible, false otherwise
	 */
	public boolean stateSetAllTransitions(AutomataState state, AutomataState resultingState) {
        HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(state);
        transitions.clear();
        return stateSetTransition(state, AutomataGrammar.anyInput, resultingState);
	}

	/**
	 * Copies the state's transitions
	 * @param id State's identifier
	 * @param copy HashMap used to copy
	 * @return tru if it's possible, false otherwise
	 */
	public boolean stateCopyTransitions(int id, HashMap<String, Set<AutomataState>> copy) {
		AutomataState state = getStateByID(id);
		return stateCopyTransitions(state, copy);
	}

	/**
	 * Sets the state's transitions
	 * @param id State's identifier
	 * @param input Input string
	 * @param dstID Destinations' identifier
	 * @return true if it's possible, false otherwise
	 */
	public boolean stateSetTransition(int id, String input, int dstID) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetTransition(fState, input, sState);
	}

	/**
	 * Sets the empty transition
	 * @param id State's identifier
	 * @param dstID Destination's identifier
	 * @return true if it's possible, false otherwise
	 */
	public boolean stateSetEmptyTransition(int id, int dstID) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetEmptyTransition(fState, sState);
	}

	/**
	 * Sets all the transactions
	 * @param id State's identifier
	 * @param dstID Destination's identifier
	 * @return true if it's possible, false otherwise
	 */
	public boolean stateSetAllTransitions(int id, int dstID) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetAllTransitions(fState, sState);
	}

	/**
	 * Sets the array with the transactions
	 * @param id State's identifier
	 * @param dstID Destination's identifier
	 * @param arr Array with strings
	 * @return true if it's possible, false otherwise
	 */
    public boolean stateSetArrTransition(int id, int dstID, String[] arr) {
        AutomataState fState = getStateByID(id);
        AutomataState sState = getStateByID(dstID);
        return stateSetArrTransition(fState, sState, arr);
    }
}
