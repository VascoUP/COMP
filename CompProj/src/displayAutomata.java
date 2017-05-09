import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import automata.AutomataState;
import automata.AutomataTable;

public class displayAutomata {
	public static void display(AutomataTable table){
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();
		Graph graph = new SingleGraph("eNFA");
		
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();
			
			graph.addNode(key.toString());
			
			for (Entry<String, Set<AutomataState>> entries : value.entrySet()) {
				String entry = entries.getKey();
				
				for (AutomataState inputResult : entries.getValue()) {
					graph.addNode(inputResult.toString());
					graph.addEdge(entry, key.toString(), inputResult.toString());
				}
			}
		}
		
		graph.display();
	}
}
