
import automata.AutomataGrammar;
import automata.AutomataTable;
import automata.AutomataTableOperations;
import automata.AutomataType;

/*import automata.AutomataExpression;
import automata.AutomataNode;
import automata.AutomataTransition;
import automata.AutomataType;*/

public class GenerateENFA {
	private static final String EXPRESSION_NAME = "Expression";
	private static final String TERMINAL_NAME = "Terminal";
	private static final String PARENTHESIS_NAME = "ParenthesisExp";

	
	public static AutomataTable enfa(SimpleNode root) {
		return start(root);
	}

	public static AutomataTable start(SimpleNode root) {
		return expression((SimpleNode) root.children[0]);
	}

	
	
	/*
	 * ======================
	 * ===== EXPRESSION =====
	 * ======================
	 */
	public static AutomataTable expression(SimpleNode node) {
		//System.out.println("expression: Node " + node.toString());

		AutomataTable fExp = expressionType((SimpleNode) node.children[0]);
		AutomataTable exp = null;

		if (node.children.length > 1) {
			node = (SimpleNode) node.children[1];
			if (node.toString() == EXPRESSION_NAME) {
				AutomataTable sExp = expression(node);
				exp = AutomataTableOperations.join(fExp, sExp);
			} else
				exp = pipeExpression(node, fExp);
		}

		return exp == null ? fExp : exp;
	}

	
	
	/*
	 * ======================
	 * == PIPES EXPRESSION ==
	 * ======================
	 */
	
	public static AutomataTable pipeExpression(SimpleNode node, AutomataTable fExp) {
		//System.out.println("pipeExpression: Node " + node.toString());

		AutomataTable sExp = expression((SimpleNode) node.children[0]);

		return AutomataTableOperations.or(fExp, sExp);
	}

	
	
	/*
	 * ======================
	 * == EXPRESSIONS TYPE ==
	 * ======================
	 */
	public static AutomataTable expressionType(SimpleNode node) {
		//System.out.println("expressionType: Node " + node.toString());

		/*
		 * ======================================= 
		 * = DONE 	-> TERMINALS 				 = 
		 * = 		-> PARENTHESIS EXPRESSIONS 	 =
		 * = ADD 	-> RANGE EXPRESSIONS 		 =
		 * =======================================
		 */
		AutomataTable table = parseExpressionType(node);

		if (node.children.length > 1)
			table = qualifierType((SimpleNode) node.children[1], table);

		return table;
	}
	
	public static AutomataTable parseExpressionType(SimpleNode node) {
		AutomataTable table;
		
		if( node.children[0].toString() == TERMINAL_NAME )
			table = tableTerminal(node);
		else if( node.children[0].toString() == PARENTHESIS_NAME )
			table = expression((SimpleNode) ((SimpleNode) node.children[0]).children[0]);
		else
			table = rangeExpression((SimpleNode) node.children[0]);
		
		return table;
	}
	
	
	public static AutomataTable rangeExpression(SimpleNode node) {
		AutomataTable table = new AutomataTable(AutomataType.E_NFA);
		int fromID = table.addState(true, false);
		int toID = table.addState(false, true);
		for( Node childNode : node.children )
			range((SimpleNode)childNode, table, fromID, toID);			
		return table;
	}
	
	public static void range(SimpleNode node, AutomataTable table, int fromState, int toState) {
		SimpleNode firstInput = (SimpleNode) ((SimpleNode) node.children[0]).children[0];
		if( node.children.length > 1 ) {
			SimpleNode secondInput= (SimpleNode) ((SimpleNode) node.children[1]).children[0];
			String[] rangeInputs = 
					AutomataGrammar.getRangeInput(firstInput.getTerminal(), secondInput.getTerminal());
			for( String input : rangeInputs )
				table.stateSetTransition(fromState, input, toState);
		} else
			table.stateSetTransition(fromState, firstInput.getTerminal(), toState);
	}
	
	/*
	 * ======================
	 * ====== TERMINAL ======
	 * ======================
	 */
	public static AutomataTable tableTerminal(SimpleNode node) {
		AutomataTable table = new AutomataTable(AutomataType.E_NFA);
		terminal((SimpleNode) node.children[0], 
					table, 
					table.addState(true, false));
		return table;
	}

	public static void terminal(SimpleNode node, AutomataTable table, int connectID) {
		//System.out.println("terminal: Node " + node.toString());
		if (node.children.length != 0) {
			node = (SimpleNode) node.children[0]; // T1
			node = (SimpleNode) node.children[0]; // Letter or Number
		}

		AutomataTableOperations.terminal(table, node.getTerminal(), connectID);

		//System.out.println("	terminal: Node transition " + node.getTerminal());
	}
	
	
	
	/*
	 * ======================
	 * ===== QUALIFIERS =====
	 * ======================
	 */

	public static AutomataTable qualifierType(SimpleNode node, AutomataTable table) {
		AutomataTable result;

		if (node.children != null && node.children.length != 0) {
			node = (SimpleNode) node.children[0];
			node = (SimpleNode) node.children[0];
			return complexQualifierType(node, table);
		}

		if (node.getTerminal() == "*")
			result = AutomataTableOperations.zeroOrMore(table);
		else if (node.getTerminal() == "+")
			result = AutomataTableOperations.oneOrMore(table);
		else
			result = AutomataTableOperations.zeroOrOne(table);

		return result;
	}

	public static AutomataTable complexQualifierType(SimpleNode node, AutomataTable table) {
		return table;
	}

}