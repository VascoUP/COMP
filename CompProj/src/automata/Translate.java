package automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Translate {
	public static AutomataTable getDFA(AutomataTable enfa) {
		AutomataTable table = new AutomataTable(AutomataType.DFA);
		List<List<AutomataState>> processStates = new ArrayList<>();
		int index = 0;

		List<AutomataState> setStates = new ArrayList<>(enfa.getEClosure(enfa.getStartState()));

		addState(table, setStates, true, isFinal(setStates));
		processStates.add(setStates);

		while (index < processStates.size()) {
			setStates = new ArrayList<>(processStates.get(index++));

			AutomataState newTableState = setStates.get(setStates.size() - 1);
			setStates.remove(setStates.size() - 1);

			HashMap<String, Set<AutomataState>> transitions = getSetStateTransitions(enfa, setStates);

			addInputEClosure(table, enfa, newTableState, transitions, processStates);
		}

		return table;
	}

	private static void addInputEClosure(AutomataTable table, AutomataTable enfa, AutomataState srcState,
			HashMap<String, Set<AutomataState>> transitions, List<List<AutomataState>> processStates) {
		for (Entry<String, Set<AutomataState>> inputStateT : transitions.entrySet())
			addQueue(table, enfa, srcState, inputStateT.getKey(), enfa.getEClosure(inputStateT.getValue()),
					processStates);
	}

	private static void addQueue(AutomataTable table, AutomataTable enfa, AutomataState srcState, String input,
			Set<AutomataState> eclosureStates, List<List<AutomataState>> processStates) {
		List<AutomataState> setStates = new ArrayList<>(eclosureStates);

		int existingID = existingState(processStates, setStates);
		if (existingID == -1) {
			AutomataState dstState = addState(table, setStates, false, isFinal(setStates));
			table.stateSetTransition(srcState, input, dstState);
			processStates.add(setStates);
		} else
			table.stateSetTransition(srcState.getID(), input, existingID);
	}

	private static HashMap<String, Set<AutomataState>> getSetStateTransitions(
			AutomataTable enfa,
			List<AutomataState> setStates) {

		HashMap<String, Set<AutomataState>> transitions = new HashMap<>();
		for (AutomataState state : setStates) {
			HashMap<String, Set<AutomataState>> stateTransitions = enfa.getStateGrammar().get(state);
			for (Entry<String, Set<AutomataState>> inputStateT : stateTransitions.entrySet()) {
				if (inputStateT.getKey() == AutomataGrammar.emptyToken)
					continue;
				Set<AutomataState> inputT = transitions.get(inputStateT.getKey());

				System.out.println("From: " + state.getID() + " with: " + inputStateT.getKey());
				System.out.println("To: ");
				printStates(inputStateT.getValue());
				
				if (inputT == null) {
					inputT = new HashSet<>();
					inputT.addAll(inputStateT.getValue());
					transitions.put(inputStateT.getKey(), inputT);
				} else {
					inputT.addAll(inputStateT.getValue());
					transitions.put(inputStateT.getKey(), inputT);
				}

				System.out.println("After add: ");
				printStates(inputStateT.getValue());
				printStates(transitions.get(inputStateT.getKey()));
			}
		}

		System.out.println("From: ");
		printStates(setStates);
		for (Entry<String, Set<AutomataState>> inputStateT : transitions.entrySet()) {
			System.out.println("Transitions with: " + inputStateT.getKey());
			printStates(inputStateT.getValue());
		}
		return transitions;
	}

	private static void printStates(Collection<AutomataState> states) {
		for(AutomataState state : states)
			System.out.println("	" + state.getID());
	}
	
	private static AutomataState addState(AutomataTable table, List<AutomataState> setStates, boolean start,
			boolean accept) {
		int id = table.addState(start, accept);
		AutomataState state = table.getStateByID(id);
		setStates.add(state);
		return state;
	}

	private static boolean isFinal(List<AutomataState> setStates) {
		for (AutomataState state : setStates)
			if (state.getAccept())
				return true;
		return false;
	}

	private static int existingState(List<List<AutomataState>> processStates, List<AutomataState> newState) {
		for (List<AutomataState> tableState : processStates) {
			if (tableState.size() - 1 != newState.size())
				continue;
			for (int i = 0; i < newState.size(); i++) {
				if (!tableState.get(i).equals(newState.get(i)))
					break;
				else if (i == newState.size() - 1)
					return tableState.get(i + 1).getID();
			}
		}
		return -1;
	}
}
