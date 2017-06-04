import java.util.ArrayList;
import java.util.List;

import automata.*;

class GenerateENFA {
	private static final String TERMINAL_NAME = "Terminal";
	private static final String QUALIFIER_NAME = "Qualifier";
	private static final String EXPT_NAME = "ExpressionType";
	private static final String PARENTHESIS_NAME = "ParenthesisExp";

	static AutomataTable enfa(SimpleNode root) {
		return start(root);
	}

	private static AutomataTable start(SimpleNode root) {
		return expression((SimpleNode) root.children[0]);
	}

	
	
	/*
	 * ======================
	 * ===== EXPRESSION =====
	 * ======================
	 */
	private static AutomataTable expression(SimpleNode node) {
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
	private static AutomataTable expressionType(SimpleNode node) {
		AutomataTable fExp = parseExpressionType((SimpleNode) node.children[0]);
		AutomataTable exp = null;
		
		if (node.children.length > 1) {
			SimpleNode nNode = (SimpleNode) node.children[1];
			if( nNode.toString().equals(EXPT_NAME)) {
				AutomataTable sExp = expressionType(nNode);
				exp = AutomataTableOperations.join(fExp, sExp);
			} else if( nNode.toString().equals(QUALIFIER_NAME) ) {
				fExp = qualifierType(nNode, fExp);
				if(node.children.length > 2 && node.children[2].toString().equals(EXPT_NAME)) {
					nNode = (SimpleNode) node.children[2];
					AutomataTable sExp = expressionType(nNode);
					exp = AutomataTableOperations.join(fExp, sExp);
				}
			}
		}

		return exp == null ? fExp : exp;
	}

	private static AutomataTable parseExpressionType(SimpleNode node) {
		AutomataTable table;
		
		if( node.toString().equals(TERMINAL_NAME) )
			table = tableTerminal(node);
		else if( node.toString().equals(PARENTHESIS_NAME) )
			table = expression((SimpleNode) (node).children[0]);
		else
			table = rangeExpression(node);
		
		return table;
	}


	private static AutomataTable rangeExpression(SimpleNode node) {
		AutomataTable table = new AutomataTable(AutomataType.E_NFA);
		int fromID = table.addState(true, false);
		int toID = table.addState(false, true);
		for( Node childNode : node.children )
			range((SimpleNode)childNode, table, fromID, toID);			
		return table;
	}

	private static void range(SimpleNode node, AutomataTable table, int fromState, int toState) {
		SimpleNode firstInput = (SimpleNode) ((SimpleNode) node.children[0]).children[0];
		if( node.children.length > 1 ) {
			SimpleNode secondInput= (SimpleNode) ((SimpleNode) node.children[1]).children[0];
			String[] rangeInputs = 
					AutomataGrammar.getRangeInput(firstInput.getTerminal(), secondInput.getTerminal());
			table.stateSetArrTransition(fromState, toState, rangeInputs);
		} else
			table.stateSetTransition(fromState, firstInput.getTerminal(), toState);
	}
	
	/*
	 * ======================
	 * ====== TERMINAL ======
	 * ======================
	 */
	private static AutomataTable tableTerminal(SimpleNode node) {
		AutomataTable table = new AutomataTable(AutomataType.E_NFA);
		terminal(	node, 
					table, 
					table.addState(true, false));
		return table;
	}

	private static void terminal(SimpleNode node, AutomataTable table, int connectID) {
		if (node.children != null && node.children.length != 0) {
			node = (SimpleNode) node.children[0]; // T1
			node = (SimpleNode) node.children[0]; // Letter or Number
		}
		AutomataTableOperations.terminal(table, node.getTerminal(), connectID);

	}
	
	
	
	/*
	 * ======================
	 * ===== QUALIFIERS =====
	 * ======================
	 */

	private static AutomataTable qualifierType(SimpleNode node, AutomataTable table) {
		AutomataTable result;

		if (node.children != null && node.children.length != 0) {
			node = (SimpleNode) node.children[0];
			node = (SimpleNode) node.children[0];
			return complexQualifierType(node, table);
		}

		switch (node.getTerminal()) {
			case "*":
				result = AutomataTableOperations.zeroOrMore(table);
				break;
			case "+":
				result = AutomataTableOperations.oneOrMore(table);
				break;
			default:
				result = AutomataTableOperations.zeroOrOne(table);
				break;
		}

		return result;
	}

	private static AutomataTable complexQualifierType(SimpleNode node, AutomataTable table) {
		return table;
	}

}