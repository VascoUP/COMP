package automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Translate {
	public static AutomataTable getDFA(AutomataTable enfa) {
		AutomataTable table = new AutomataTable(AutomataType.DFA);
		Queue<SortedSet<AutomataState>> processStates = new PriorityQueue<>();
		addQueue(table, enfa, enfa.getStartState(), processStates);
		SortedSet<AutomataState> setStates;
		
		while(!processStates.isEmpty()) {
			setStates = processStates.poll();
			//SortedSet<AutomataState> setStateTransitions = new TreeSet<>();
			
			AutomataState newTableState = setStates.last();
			setStates.remove(newTableState);
			
			HashMap<String, Set<AutomataState>> transitions = getSetStateTransitions(enfa, setStates);
			addInputEClosure(table, enfa, transitions, processStates);
		}
		
		return table;
	}
	
	private static void addInputEClosure(	
							AutomataTable table, 		
							AutomataTable enfa, 
							HashMap<String, Set<AutomataState>> transitions, 
							Queue<SortedSet<AutomataState>> processStates) {
		for( Entry<String, Set<AutomataState>> inputStateT : transitions.entrySet())
			addQueue(table, enfa, inputStateT.getValue(), processStates);
	}
	
	private static void addQueue(	AutomataTable table, 
			AutomataTable enfa, 
			AutomataState state,
			Queue<SortedSet<AutomataState>> processStates) {
		SortedSet<AutomataState> setStates = new TreeSet<>(enfa.getEClosure(state));
		addState(table, setStates, true, false);
		processStates.add(setStates);		
	}
	
	private static void addQueue(	AutomataTable table, 
							AutomataTable enfa, 
							Set<AutomataState> states,
							Queue<SortedSet<AutomataState>> processStates) {
		SortedSet<AutomataState> setStates = new TreeSet<>(enfa.getEClosure(states));
		addState(table, setStates, true, false);
		processStates.add(setStates);		
	}
	
	private static HashMap<String, Set<AutomataState>> getSetStateTransitions(
							AutomataTable enfa, 
							SortedSet<AutomataState> setStates) {
		
		HashMap<String, Set<AutomataState>> transitions = new HashMap<>();
		for( AutomataState state : setStates ) {
			HashMap<String, Set<AutomataState>> stateTransitions = enfa.getStateGrammar().get(state);

			for( Entry<String, Set<AutomataState>> inputStateT : stateTransitions.entrySet()) {
				Set<AutomataState> inputT = transitions.get(inputStateT);
				if (inputT == null) {
					inputT = new HashSet<>();
					inputT.addAll(inputStateT.getValue());
					transitions.put(inputStateT.getKey(), inputT);
				} else
					inputT.addAll(inputStateT.getValue());
			}
		}
		return transitions;
	}
	
	private static void addState(AutomataTable table, Set<AutomataState> setStates, boolean start, boolean accept) {
		int id = table.addState(true, false);
		AutomataState state = table.getStateByID(id);
		setStates.add(state);
	}
}
