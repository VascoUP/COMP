package automata;

import java.util.ArrayList;

public class PrintAutomata {
	public static String getString( AutomataNode node ) {
		StringBuilder builder = new StringBuilder();
		AutomataTransition[] transitions = node.getTransitions();

		ArrayList<AutomataNode> nodeList = new ArrayList<>();
		nodeList.add(node);
		
		builder.append(node.getID());
		builder.append(getChildString(transitions, nodeList, " "));
		return new String(builder);
	}
	
	private static String getChildString( AutomataTransition[] transitions, ArrayList<AutomataNode> nodeList, String init ) {
		StringBuilder builder = new StringBuilder();
		for( AutomataTransition tr : transitions )
			builder.append(getString(tr, nodeList, init + " "));
		return new String(builder);
		
	}
	
	private static String getString( AutomataTransition transition, ArrayList<AutomataNode> nodeList, String init ) {
		StringBuilder builder = new StringBuilder();
		AutomataNode node = transition.getDst();
		AutomataTransition[] transitions = node.getTransitions();
		
		builder.append("\n");
		builder.append(init);
		builder.append(" ");
		builder.append(getNodeInfo(transition));

		if( !nodeList.contains(node) ) {
			ArrayList<AutomataNode> newList = cloneList(nodeList);
			newList.add(node);
			builder.append(getChildString(transitions, newList, init + " "));
		}
		
		return new String(builder);
	}
	
	private static String getNodeInfo( AutomataTransition transition ) {
		StringBuilder builder = new StringBuilder();
		AutomataNode node = transition.getDst();
		builder.append(transition);
		builder.append(" ");
		builder.append(node.getID());
		return new String(builder);
	}

	private static ArrayList<AutomataNode> cloneList(ArrayList<AutomataNode> list) {
		ArrayList<AutomataNode> clone = new ArrayList<>(list.size());
	    for (AutomataNode item : list) clone.add(item);
	    return clone;
	}
}
