import java.util.ArrayList;
import java.util.List;

import automata.*;

/**
 * 
 * This class generates the eNFA
 *
 */
public class GenerateENFA {
	private static final String TERMINAL_NAME = "Terminal";
	private static final String QUALIFIER_NAME = "Qualifier";
	private static final String EXPT_NAME = "ExpressionType";
	private static final String PARENTHESIS_NAME = "ParenthesisExp";

	/**
	 * Creates an automata table from the eNFA
	 * @param root eNFA's root
	 * @return The automata table created
	 */
	public static AutomataTable enfa(SimpleNode root) {
		return start(root);
	}

	/**
	 * Creates an automata table from the eNFA's root
	 * @param root eNFA's root
	 * @return The automata table created
	 */
	public static AutomataTable start(SimpleNode root) {
		return expression((SimpleNode) root.children[0]);
	}

	
	
	/*
	 * ======================
	 * ===== EXPRESSION =====
	 * ======================
	 */
	
	/**
	 * Creates an automata table using the node passed by the start() function
	 * @param node Node to be added to the automata table
	 * @return The automata table created
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
	/**
	 * Creates an automata table using the expressionType from the node selected
	 * @param node Node to be added to the automata table
	 * @return The automata table created
	 */
	public static AutomataTable expressionType(SimpleNode node) {
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
	
	/**
	 * Creates an automata table parsing the expressionType of the node selected
	 * @param node The node to be analyzed
	 * @return The automata table created
	 */
	public static AutomataTable parseExpressionType(SimpleNode node) {
		AutomataTable table;
		
		if( node.toString().equals(TERMINAL_NAME) )
			table = tableTerminal(node);
		else if( node.toString().equals(PARENTHESIS_NAME) )
			table = expression((SimpleNode) (node).children[0]);
		else
			table = rangeExpression(node);
		
		return table;
	}

	
	/**
	 * Creates an automata table for a range expression
	 * @param node Node to be added
	 * @return The automata table created
	 */
	public static AutomataTable rangeExpression(SimpleNode node) {
		AutomataTable table = new AutomataTable(AutomataType.E_NFA);
		int fromID = table.addState(true, false);
		int toID = table.addState(false, true);
		for( Node childNode : node.children )
			range((SimpleNode)childNode, table, fromID, toID);			
		return table;
	}

	/**
	 * Analyzes the range of a expression
	 * @param node Node to be analyzed
	 * @param table AutomataTable where the node will be added
	 * @param fromState Initial state
	 * @param toState Next state
	 */
	public static void range(SimpleNode node, AutomataTable table, int fromState, int toState) {
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
	
	/**
	 * Creates a automata table for a terminal
	 * @param node Node to be added
	 * @return The automata created
	 */
	public static AutomataTable tableTerminal(SimpleNode node) {
		AutomataTable table = new AutomataTable(AutomataType.E_NFA);
		terminal(	node, 
					table, 
					table.addState(true, false));
		return table;
	}

	/**
	 * Analyzes a terminal expression
	 * @param node Node that will be analyzed
	 * @param table Automata table where the node will be added
	 * @param connectID Connection's identifier
	 */
	public static void terminal(SimpleNode node, AutomataTable table, int connectID) {
		System.out.println("terminal: Node " + node.toString());
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

	/**
	 * Creates an automata table for a qualifier
	 * @param node No to be added
	 * @param table Automata table used to create the new table
	 * @return The automata table created
	 */
	public static AutomataTable qualifierType(SimpleNode node, AutomataTable table) {
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

	/**
	 * Creates an automata table for a complex qualifier
	 * @param node No to be added
	 * @param table Automata table used to create the new table
	 * @return The automata table created
	 */
	public static AutomataTable complexQualifierType(SimpleNode node, AutomataTable table) {
		return table;
	}

}