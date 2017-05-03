package automata;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class PrintAutomata {
	public static String getString(AutomataTable table) {
		StringBuilder builder = new StringBuilder();
		HashMap<AutomataState, HashMap<String, List<AutomataState>>> stateGrammar = table.getStateGrammar();

		for (Entry<AutomataState, HashMap<String, List<AutomataState>>> state : stateGrammar.entrySet()) {
			AutomataState key = state.getKey();
			HashMap<String, List<AutomataState>> value = state.getValue();

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

			for (Entry<String, List<AutomataState>> entries : value.entrySet()) {
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
