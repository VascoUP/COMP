package automata;

import java.util.HashMap;
import java.util.Map.Entry;

public class PrintAutomata {
	public static String getString(AutomataTable table) {
		StringBuilder builder = new StringBuilder();
		HashMap<AutomataState, AutomataState[]> stateGrammar = table.getStateGrammar();
		
		for( Entry<AutomataState, AutomataState[]> state : stateGrammar.entrySet() ) {
			AutomataState key = state.getKey();
			AutomataState[] value = state.getValue();
			
			if( key.getStart() )
				builder.append("->");
			else
				builder.append("  ");
			if( key.getAccept() )
				builder.append("*");
			else 
				builder.append(" ");
			builder.append(key.getID());
			builder.append(" |");
			
			for( int i = 0; i < value.length; i++ ) {
				if( value[i] != null ) {
					builder.append(AutomataGrammar.grammar.get(i));
					builder.append("-");
					builder.append(value[i].getID());
					builder.append(" |");
				}
			}
			builder.append("\n");
		}
		
		return new String(builder);
	}
}
