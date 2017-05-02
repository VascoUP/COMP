package automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class AutomataTable {
	private HashMap<AutomataState, HashMap<Integer, List<AutomataState>>> stateGrammar;

	private AutomataType type;

	private int stateID = 1;

	public AutomataTable(AutomataType type) {
		this.type = type;
		stateGrammar = new HashMap<>();
	}

	public HashMap<AutomataState, HashMap<Integer, List<AutomataState>>> getStateGrammar() {
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
		for (Entry<AutomataState, HashMap<Integer, List<AutomataState>>> entries : stateGrammar.entrySet())
			if (entries.getKey().getAccept())
				acceptStates.add(entries.getKey());
		return acceptStates.toArray(new AutomataState[acceptStates.size()]);
	}

	public AutomataState getStartState() {
		for (Entry<AutomataState, HashMap<Integer, List<AutomataState>>> entries : stateGrammar.entrySet())
			if (entries.getKey().getStart())
				return entries.getKey();
		return null;
	}

	public int getStateID() {
		return stateID;
	}

	public List<AutomataState> getStateInputTransitions(String input, AutomataState state) {
		HashMap<Integer, List<AutomataState>> hash = stateGrammar.get(state);
		if (hash == null)
			return null;
		int index = AutomataGrammar.grammar.indexOf(AutomataGrammar.emptyToken);
		return hash.get(index);
	}

	public int addState(boolean start, boolean accept) {
		int id = stateID;
		System.out.println(stateID);
		HashMap<Integer, List<AutomataState>> hash = stateGrammar.put(new AutomataState(stateID++, start, accept), new HashMap<Integer, List<AutomataState>>());
		System.out.println(hash == null ? "null" : "not null");
		System.out.println(stateID);
		return id;
	}

	public int addState(AutomataState state) {
		int id = stateID;
		state.setID(stateID++);
		stateGrammar.put(state, new HashMap<Integer, List<AutomataState>>());
		return id;
	}

	public boolean stateCopyTransitions(AutomataState state, HashMap<Integer, List<AutomataState>> copy) {
		if (state == null)
			return false;

		HashMap<Integer, List<AutomataState>> currTransitions = stateGrammar.get(state);
		if (currTransitions == null)
			return false;

		for (Entry<Integer, List<AutomataState>> entries : copy.entrySet()) {
			List<AutomataState> inputT = currTransitions.get(entries.getKey());
			if (inputT == null)
				inputT = new ArrayList<>();

			inputT.addAll(entries.getValue());
			currTransitions.put(entries.getKey(), inputT);
		}

		stateGrammar.put(state, currTransitions);

		return true;
	}

	public boolean stateSetTransition(AutomataState state, String input, AutomataState resultingState) {
		int index = AutomataGrammar.grammar.indexOf(input);
		if (!stateGrammar.containsKey(resultingState) || !stateGrammar.containsKey(state) || index == -1)
			return false;

		HashMap<Integer, List<AutomataState>> transitions = stateGrammar.get(state);

		List<AutomataState> inputT = transitions.get(index);
		if (inputT == null)
			inputT = new ArrayList<>();

		if( !inputT.contains(resultingState) )
			inputT.add(resultingState);
		transitions.put(index, inputT);

		return true;
	}

	public boolean stateSetEmptyTransition(AutomataState state, AutomataState resultingState) {
		if (!stateGrammar.containsKey(resultingState) || !stateGrammar.containsKey(state) || type != AutomataType.E_NFA)
			return false;
		stateSetTransition(state, AutomataGrammar.emptyToken, resultingState);
		return true;
	}

	public boolean stateSetAllTransitions(AutomataState state, AutomataState resultingState) {
		if (!stateGrammar.containsKey(resultingState) || !stateGrammar.containsKey(state))
			return false;

		HashMap<Integer, List<AutomataState>> transitions = stateGrammar.get(state);
		for (int i = 0; i < transitions.size(); i++)
			if (AutomataGrammar.grammar.get(i) != AutomataGrammar.emptyToken)
				transitions.get(i).add(resultingState);

		return true;
	}

	public boolean stateCopyTransitions(int id, HashMap<Integer, List<AutomataState>> copy) {
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
