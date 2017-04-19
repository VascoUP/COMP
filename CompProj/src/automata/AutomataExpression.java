package automata;

public class AutomataExpression {
	private AutomataNode head;
	private AutomataNode tail;
	
	public AutomataExpression(AutomataNode head, AutomataNode tail) {
		this.head = head;
		this.tail = tail;
	}
	
	public AutomataNode getHead() {
		return head;
	}
	
	public AutomataNode getTail() {
		return tail;
	}
}
