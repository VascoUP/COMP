package automata;

public class AutomataTableOperations {
	public static AutomataTable join(AutomataTable first, AutomataTable second) {
		return first;
	}
	
	public static AutomataTable or(AutomataTable first, AutomataTable second) {
		return first;
	}
	
	public static AutomataTable zeroOrMore(AutomataTable first) {
		return first;
	}
	
	public static AutomataTable oneOrMore(AutomataTable first) {
		return first;
	}
	
	public static AutomataTable zeroOrOne(AutomataTable first) {
		return first;
	}
	
	public static AutomataTable nToM(AutomataTable first, int n, int m) {
		return first;
	}
	
	public static AutomataTable nToMax(AutomataTable first, int n) {
		return first;
	}
	
	public static AutomataTable maxToM(AutomataTable first, int m) {
		return first;
	}
	
	public static void terminal(AutomataTable table, String input, int fromState) {
		int toState = table.addState(false, true);
		table.stateSetTransition(fromState, input, toState);
	}
}
