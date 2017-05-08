package automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
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


	public SortedSet<AutomataState> getEClosure(AutomataState state) {
		Set<AutomataState> states = new HashSet<>();
		states.add(state);
		return getEClosure(states);
	}
	
	public SortedSet<AutomataState> getEClosure(Set<AutomataState> states) {
		Set<AutomataState> setStates = new LinkedHashSet<>(states);
		SortedSet<AutomataState> processedStates = new TreeSet<>();
		for( AutomataState state : setStates ) {
			if( processedStates.contains(state) )
				continue;
			processedStates.add(state);
			setStates.addAll(getStateInputTransitions(AutomataGrammar.emptyToken, state));
		}
		return processedStates;
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
		if (!stateGrammar.containsKey(resultingState) || !stateGrammar.containsKey(state))
			return false;

		HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(state);

		Set<AutomataState> inputT = transitions.get(input);
		if (inputT == null) {
			inputT = new HashSet<>();
			inputT.add(resultingState);
			transitions.put(input, inputT);
		} else
			inputT.add(resultingState);

		return true;
	}

	public boolean stateSetEmptyTransition(AutomataState state, AutomataState resultingState) {
		if (!stateGrammar.containsKey(resultingState) || !stateGrammar.containsKey(state) || type != AutomataType.E_NFA)
			return false;
		stateSetTransition(state, AutomataGrammar.emptyToken, resultingState);
		return true;
	}

	public boolean stateSetArrTransition(AutomataState state, AutomataState resultingState, String[] arr) {
		if (!stateGrammar.containsKey(resultingState) || !stateGrammar.containsKey(state))
			return false;

		HashMap<String, Set<AutomataState>> transitions = stateGrammar.get(state);
		for( String str : arr ) {
			Set<AutomataState> list = transitions.get(str);
			if( list == null ) {
				list = new HashSet<>();
				list.add(resultingState);
				transitions.put(str, list);
			} else 
				list.add(resultingState);
		}

		return true;
	}
	
	public boolean stateSetAllTransitions(AutomataState state, AutomataState resultingState) {
		if (!stateGrammar.containsKey(resultingState) || !stateGrammar.containsKey(state))
			return false;

		ArrayList<String> arrList = new ArrayList<>();
		List<String> lowercase = Arrays.asList(AutomataGrammar.getRangeInput("a", "z"));
		List<String> uppercase = Arrays.asList(AutomataGrammar.getRangeInput("A", "Z"));
		List<String> numbers = Arrays.asList(AutomataGrammar.getRangeInput("0", "9"));
		arrList.addAll(lowercase);
		arrList.addAll(uppercase);
		arrList.addAll(numbers);
		
		return stateSetArrTransition(state, resultingState, arrList.toArray(new String[arrList.size()]));
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
}
