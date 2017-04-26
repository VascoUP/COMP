package automata;

import java.util.ArrayList;
import java.util.Iterator;

public class AutomataNode {
	private static int nodeIDs = 1;
	
	private int id = nodeIDs++;
    private ArrayList<AutomataTransition> transitions;
    
    public AutomataNode() {
    	transitions = new ArrayList<AutomataTransition>();
    }
    
    public void addTransition(AutomataTransition transition) {
    	if( transition.getSrc() != this )
    		return ;
    	transitions.add(transition);
    }
    
    public AutomataNode[] getConnectedNodes() {
    	Iterator<AutomataTransition> transitions_iter = transitions.iterator();
    	AutomataNode[] result = new AutomataNode[transitions.size()];
    	int result_index = 0;    	
    	while(transitions_iter.hasNext())
    		result[result_index] = transitions_iter.next().getDst();
    	return result;
    }
    
    public int getID() {
    	return id;
    }
    
    public AutomataTransition[] getTransitions() {
    	AutomataTransition[] t = new AutomataTransition[transitions.size()];
    	return transitions.toArray(t);
    }
}