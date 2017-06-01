import java.util.ArrayList;
import java.util.List;

import automata.AutomataGrammar;
import automata.AutomataTable;
import automata.AutomataTableOperations;
import automata.AutomataType;

public class GenerateENFA {
	private static final String TERMINAL_NAME = "Terminal";
	private static final String QUALIFIER_NAME = "Qualifier";
	private static final String EXPT_NAME = "ExpressionType";
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
		System.out.println("expression: Node " + node.toString());

		if( node.children.length < 1 )
			return null;
		
		AutomataTable segExp;
		List<AutomataTable> tables = new ArrayList<>();
		
		for( Node n : node.children ) {
			segExp = expressionType((SimpleNode) n);
			tables.add(segExp);
		}
		
		return AutomataTableOperations.or(tables);
	}

	
	/*
	 * ======================
	 * == EXPRESSIONS TYPE ==
	 * ======================
	 */
	public static AutomataTable expressionType(SimpleNode node) {
		System.out.println("expressionType: Node " + node.toString());
		AutomataTable fExp = parseExpressionType((SimpleNode) node.children[0]);
		AutomataTable exp = null;
		
		if (node.children.length > 1) {
			SimpleNode nNode = (SimpleNode) node.children[1];
			if( nNode.toString().equals(EXPT_NAME)) {
				AutomataTable sExp = expressionType(nNode);
				exp = AutomataTableOperations.join(fExp, sExp);
			} else if( nNode.toString().equals(QUALIFIER_NAME) ) {
				fExp = qualifierType(nNode, fExp);
				if( node.children.length > 2 ) {
					nNode = (SimpleNode) node.children[2];
					if( nNode.toString().equals(EXPT_NAME)) {
						AutomataTable sExp = expressionType(nNode);
						exp = AutomataTableOperations.join(fExp, sExp);
					}
				}
			}
		}

		return exp == null ? fExp : exp;
	}
	
	public static AutomataTable parseExpressionType(SimpleNode node) {
		System.out.println("parseExpressionType: Node " + node.toString());
		AutomataTable table;
		
		if( node.toString().equals(TERMINAL_NAME) )
			table = tableTerminal(node);
		else if( node.toString().equals(PARENTHESIS_NAME) )
			table = expression((SimpleNode) (node).children[0]);
		else
			table = rangeExpression(node);
		
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
			/*for( String input : rangeInputs )
				table.stateSetTransition(fromState, input, toState);*/
			table.stateSetArrTransition(fromState, toState, rangeInputs);
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
		terminal(	node, 
					table, 
					table.addState(true, false));
		return table;
	}

	public static void terminal(SimpleNode node, AutomataTable table, int connectID) {
		System.out.println("terminal: Node " + node.toString());
		if (node.children != null && node.children.length != 0) {
			node = (SimpleNode) node.children[0]; // T1
			node = (SimpleNode) node.children[0]; // Letter or Number
		}
		
		System.out.println("	terminal: Node transition " + node.getTerminal());
		AutomataTableOperations.terminal(table, node.getTerminal(), connectID);

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