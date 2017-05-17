package automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class DisplayAutomata {
	private static int edgeID = 1;
	
	public static void display(AutomataTable table){
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();
		Graph graph = new SingleGraph("eNFA");
		
		Set<String> states = new HashSet<>();
		List<Edge> edges = new ArrayList<>();
		
		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();
			
			addNode(key, states, graph);
			
			for (Entry<String, Set<AutomataState>> entries : value.entrySet()) {
				String entry = entries.getKey();
				
				for (AutomataState inputResult : entries.getValue()) {
					addNode(inputResult, states, graph);
					addEdge(entry, key, inputResult, edges,graph);
				}
			}
		}
		
		graph.display();
	}
	
	private static String stateToString(AutomataState state) {
		return "q" + state.getID();
	}
	
	private static void addNode(AutomataState state, Set<String> addedStates, Graph graph) {
		String str = stateToString(state);
		if( addedStates.contains(str) )
			return ;
		addedStates.add(str);
		org.graphstream.graph.Node n = graph.addNode(str);
		n.addAttribute("ui.label", str);
	}
	
	private static void addEdge(String entry, AutomataState src, AutomataState dst, List<Edge> addedEdges, Graph graph) {
		for( Edge e : addedEdges ) {
			if( e.getSourceNode().getId().equals(stateToString(src)) && 
				e.getTargetNode().getId().equals(stateToString(dst)) ) {
				String label = e.getAttribute("ui.label");
				label += ","+entry;
				e.setAttribute("ui.label", label);
				return ;
			}
		}
		
		Edge e = graph.addEdge(edgeID++ + "", stateToString(src), stateToString(dst), true);
		e.addAttribute("ui.label", entry);
		addedEdges.add(e);
	}
}
