package automata;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * This class prints the automata
 *
 */
public class PrintAutomata {
	/**
	 * Gets the automata's table representative string
	 * @param table Automata's table to be printed
	 * @return The automata's table representative string
	 */
	public static String getString(AutomataTable table) {
		StringBuilder builder = new StringBuilder();
		HashMap<AutomataState, HashMap<String, Set<AutomataState>>> stateGrammar = table.getStateGrammar();

		for (Entry<AutomataState, HashMap<String, Set<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, Set<AutomataState>> value = state.getValue();

			if (key.getStart())
				builder.append("->");
			else
				builder.append("  ");
			if (key.getAccept())
				builder.append("*");
			else
				builder.append(" ");
			builder.append(key.getID());
			builder.append(" |");

			for (Entry<String, Set<AutomataState>> entries : value.entrySet()) {
				for (AutomataState inputResult : entries.getValue()) {
					builder.append(entries.getKey());
					builder.append("-");
					builder.append(inputResult.getID());
					builder.append(" |");
				}
			}
			builder.append("\n");
		}

		return new String(builder);
	}
}
