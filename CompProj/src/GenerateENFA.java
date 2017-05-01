
import automata.AutomataTable;
import automata.AutomataTableOperations;
import automata.AutomataType;
import automata.PrintAutomata;

/*import automata.AutomataExpression;
import automata.AutomataNode;
import automata.AutomataTransition;
import automata.AutomataType;*/

public class GenerateENFA {
	// private static final String START_NAME = "Start";
	private static final String EXPRESSION_NAME = "Expression";
	/*
	 * private static final String EXPRESSION_TYPE_NAME = "ExpressionType";
	 * private static final String PIPE_EXPRESSION_NAME = "PipeExpression";
	 */

	public static AutomataTable enfa(SimpleNode root) {
		return start(root);
	}

	public static AutomataTable start(SimpleNode root) {
		return expression((SimpleNode) root.children[0]);
	}

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

	public static AutomataTable pipeExpression(SimpleNode node, AutomataTable fExp) {
		//System.out.println("pipeExpression: Node " + node.toString());

		AutomataTable sExp = expression((SimpleNode) node.children[0]);

		return AutomataTableOperations.or(fExp, sExp);
	}

	public static AutomataTable expressionType(SimpleNode node) {
		//System.out.println("expressionType: Node " + node.toString());

		/*
		 * ======================================= 
		 * = DONE 	-> TERMINALS 				 = 
		 * = 		-> PARENTHESIS EXPRESSIONS 	 =
		 * = ADD 	-> RANGE EXPRESSIONS 		 =
		 * =======================================
		 */
		AutomataTable table;
		
		if( node.children[0].toString() == "Terminal" ) {
			table = new AutomataTable(AutomataType.E_NFA);
			int id = table.addState(true, false);
			terminal((SimpleNode) node.children[0], table, id);
		}
		else if( node.children[0].toString() == "ParenthesisExp" ) {
			SimpleNode nextNode = (SimpleNode) node.children[0];
			table = expression((SimpleNode) nextNode.children[0]);
		} else
			table = null;

		if (node.children.length > 1) {
			// Has qualifier
			//System.out.println("	expressionType: Qualifier terminal ");
			table = qualifierType((SimpleNode) node.children[1], table);
		} else {
			// Do not have qualifier (only terminal)
			//System.out.println("	expressionType: Non qualifier terminal ");
		}

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