package automata;

import java.util.ArrayList;
import java.util.Iterator;

public class AutomataNode {
    private ArrayList<AutomataTransition> transitions;
    
    public AutomataNode() {
    	transitions = new ArrayList<AutomataTransition>();
    }
    
    public AutomataNode[] getConnectedNodes() {
    	Iterator<AutomataTransition> transitions_iter = transitions.iterator();
    	AutomataNode[] result = new AutomataNode[transitions.size()];
    	int result_index = 0;    	
    	while(transitions_iter.hasNext())
    		result[result_index] = transitions_iter.next().getDst();
    	return result;
    }
}