import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import automata.AutomataState;
import automata.AutomataTable;

public class DisplayAutomata {
	public static void display(AutomataTable table){
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();
		Graph graph = new SingleGraph("eNFA");
		
		SortedSet<String> addedStates = new TreeSet<>();
		
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();
			
			addNode(key, addedStates, graph);
			
			for (Entry<String, Set<AutomataState>> entries : value.entrySet()) {
				String entry = entries.getKey();
				
				for (AutomataState inputResult : entries.getValue()) {
					addNode(inputResult, addedStates, graph);
					graph.addEdge(entry, stateToString(key), stateToString(inputResult));
				}
			}
		}
		
		graph.display();
	}
	
	private static String stateToString(AutomataState state) {
		return "q" + state.getID();
	}
	
	private static void addNode(AutomataState state, SortedSet<String> addedStates, Graph graph) {
		String str = stateToString(state);
		if( addedStates.contains(str) )
			return ;
		addedStates.add(str);
		graph.addNode(str);
	}
}
