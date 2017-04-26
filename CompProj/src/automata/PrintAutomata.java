package automata;

public class PrintAutomata {
	public static String getString( AutomataNode node ) {
		StringBuilder builder = new StringBuilder();
		AutomataTransition[] transitions = node.getTransitions();
		builder.append(node.getID());
		builder.append(getChildString(transitions, " "));
		return new String(builder);
	}
	
	private static String getChildString( AutomataTransition[] transitions, String init ) {
		StringBuilder builder = new StringBuilder();
		for( AutomataTransition tr : transitions )
			builder.append(getString(tr, init + " "));
		return new String(builder);
		
	}
	
	private static String getString( AutomataTransition transition, String init ) {
		StringBuilder builder = new StringBuilder();
		AutomataNode node = transition.getDst();
		AutomataTransition[] transitions = node.getTransitions();
		
		builder.append("\n");
		builder.append(init);
		builder.append(" ");
		builder.append(getNodeInfo(transition));
		builder.append(getChildString(transitions, init + " "));
		
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
}
