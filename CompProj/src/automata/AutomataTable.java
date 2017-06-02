package automata;

import java.util.ArrayList;
import java.util.Arrays;
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

public class AutomataTable {
	private HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar;

	private AutomataType type;

	private int stateID = 1;

	public AutomataTable(AutomataType type) {
		this.type = type;
		stateGrammar = new HashMap<>();
	}

	public HashMap<AutomataState, HashMap<String, Set<AutomataState>>> getStateGrammar() {
		return stateGrammar;
	}

	public int getAutomataSize() {
		return stateGrammar.size();
	}

	public AutomataState getStateByID(int id) {
		for (AutomataState state : stateGrammar.keySet())
			if (state.getID() == id)
				return state;
		return null;
	}

	public AutomataState[] getAcceptStates() {
		ArrayList<AutomataState> acceptStates = new ArrayList<>();
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> entries : stateGrammar.entrySet())
			if (entries.getKey().getAccept())
				acceptStates.add(entries.getKey());
		return acceptStates.toArray(new AutomataState[acceptStates.size()]);
	}

	public AutomataState getStartState() {
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> entries : stateGrammar.entrySet())
			if (entries.getKey().getStart())
				return entries.getKey();
		return null;
	}

	public int getStateID() {
		return stateID;
	}

	public Set<AutomataState> getStateInputTransitions(String input, AutomataState state) {
		HashMap<String, Set<AutomataState>> hash = stateGrammar.get(state);
		if (hash == null) 
			return null;
		return hash.get(input);
	}

	public Set<AutomataState> getStateTransitions(AutomataState state) {
	    HashMap<String, Set<AutomataState>> inputTransitions = stateGrammar.get(state);
	    Set<AutomataState> transitions = new HashSet<>();
	    for(Set<AutomataState> iT : inputTransitions.values())
	        transitions.addAll(iT);
	    return transitions;
    }

	public SortedSet<AutomataState> getEClosure(AutomataState state) {
		List<AutomataState> states = new ArrayList<>();
		states.add(state);
		return getEClosure(states);
	}
	
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


	public void removeState(int id) {
		AutomataState state = getStateByID(id);
		stateGrammar.remove(state);
	}
	
	public int addState(boolean start, boolean accept) {
		int id = stateID;
		stateGrammar.put(new AutomataState(stateID++, start, accept), 
						new HashMap<>());
		return id;
	}

	public int addState(AutomataState state) {
		int id = stateID;
		state.setID(stateID++);
		stateGrammar.put(state, new HashMap<>());
		return id;
	}


	private boolean verifyAddTransition(AutomataState fromState, AutomataState toState) {
        HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(fromState);
        Set<AutomataState> states = transitions.get(AutomataGrammar.anyInput);
        return states != null && states.contains(toState);
    }

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
			} else
				inputT.addAll(entries.getValue());
		}

		stateGrammar.put(state, currTransitions);

		return true;
	}

	public boolean stateSetTransition(AutomataState state, String input, AutomataState resultingState) {
		return stateAddTransition(state, resultingState, input);
	}

	public boolean stateSetEmptyTransition(AutomataState state, AutomataState resultingState) {
        return type == AutomataType.E_NFA && stateSetTransition(state, AutomataGrammar.emptyToken, resultingState);
    }

	public boolean stateSetArrTransition(AutomataState state, AutomataState resultingState, String[] arr) {
		HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(state);
		for( String str : arr )
		    if( !stateSetTransition(state, str, resultingState) )
		        break;
		return true;
	}
	
	public boolean stateSetAllTransitions(AutomataState state, AutomataState resultingState) {
        HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(state);
        transitions.clear();
        return stateSetTransition(state, AutomataGrammar.anyInput, resultingState);
	}

	public boolean stateCopyTransitions(int id, HashMap<String, Set<AutomataState>> copy) {
		AutomataState state = getStateByID(id);
		return stateCopyTransitions(state, copy);
	}

	public boolean stateSetTransition(int id, String input, int dstID) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetTransition(fState, input, sState);
	}

	public boolean stateSetEmptyTransition(int id, int dstID) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetEmptyTransition(fState, sState);
	}

	public boolean stateSetAllTransitions(int id, int dstID) {
		AutomataState fState = getStateByID(id);
		AutomataState sState = getStateByID(dstID);
		return stateSetAllTransitions(fState, sState);
	}

    public boolean stateSetArrTransition(int id, int dstID, String[] arr) {
        AutomataState fState = getStateByID(id);
        AutomataState sState = getStateByID(dstID);
        return stateSetArrTransition(fState, sState, arr);
    }
}
