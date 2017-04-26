import automata.AutomataExpression;
import automata.AutomataNode;
import automata.AutomataTransition;
import automata.AutomataType;

public class GenerateENFA {
	//private static final String START_NAME = "Start";
	private static final String EXPRESSION_NAME = "Expression";
	/*private static final String EXPRESSION_TYPE_NAME = "ExpressionType";
	private static final String PIPE_EXPRESSION_NAME = "PipeExpression";*/
	
    public static AutomataNode enfa( SimpleNode root ) {
        return start(root);
    }
    
    public static AutomataNode start( SimpleNode root) {
    	return expression((SimpleNode)root.children[0]).getHead();
    }
    
    public static AutomataExpression expression( SimpleNode node ) {
		System.out.println("expression: Node " + node.toString());
		
		AutomataExpression fExp = expressionType((SimpleNode)node.children[0]);
		AutomataExpression exp = null;
    	
    	if( node.children.length > 1 ) {
    		node = (SimpleNode)node.children[1];
    		if( node.toString() == EXPRESSION_NAME ) {
    			AutomataExpression sExp = expression(node);
    			AutomataTransition transition = 
    					AutomataTransition.toNode(AutomataType.E_NFA, fExp.getTail(), sExp.getHead());
    			transition.addToken();
    			exp = new AutomataExpression(fExp.getHead(), sExp.getTail());
    		} else
    			exp = pipeExpression(node, fExp);
    	}
    	
    	return exp == null ? fExp : exp;
    }
    
    public static AutomataExpression pipeExpression( SimpleNode node, AutomataExpression fExp ) {
		System.out.println("pipeExpression: Node " + node.toString());
		
    	AutomataNode nHead = new AutomataNode();
    	AutomataNode nTail = new AutomataNode();
    	
    	AutomataExpression sExp = expression((SimpleNode)node.children[0]);
    	
    	AutomataTransition fTHead = AutomataTransition.toNode(AutomataType.E_NFA, nHead, fExp.getHead());
    	AutomataTransition fTTail = AutomataTransition.toNode(AutomataType.E_NFA, fExp.getTail(), nTail);
    	AutomataTransition sTHead = AutomataTransition.toNode(AutomataType.E_NFA, nHead, sExp.getHead());
    	AutomataTransition sTTail = AutomataTransition.toNode(AutomataType.E_NFA, sExp.getTail(), nTail);
    	
    	fTHead.addToken();
    	fTTail.addToken();
    	sTHead.addToken();
    	sTTail.addToken();
    	
    	return new AutomataExpression(nHead, nTail);
    }
    
    public static AutomataExpression expressionType( SimpleNode node ) {
		System.out.println("expressionType: Node " + node.toString());
    	AutomataNode head = new AutomataNode();
    	
    	/*=======================================
    	 *=	DONE 	-> 	TERMINALS				=
    	 *=										=
    	 *=	ADD		->	RANGE EXPRESSIONS		=
    	 *=			->	PARENTHESIS EXPRESSIONS	=
    	 *=======================================
    	 */
    	AutomataTransition transition = terminal((SimpleNode)node.children[0], head);
    	AutomataExpression exp = new AutomataExpression(head, transition.getDst());
    	
    	if( node.children.length > 1 ) {
    		// Has qualifier
    		System.out.println("	expressionType: Qualifier terminal ");
    		exp = qualifierType((SimpleNode)node.children[1], exp);
    	} else {
    		// Do not have qualifier (only terminal)
    		System.out.println("	expressionType: Non qualifier terminal ");
    	}
    	
    	return exp;
    }
    
    public static AutomataExpression qualifierType( SimpleNode node, AutomataExpression expression ) {
    	AutomataNode head;
    	AutomataNode tail;
    	AutomataTransition transition;
    	
    	System.out.println(node.getTerminal());
    	System.out.println(node + "");
    	
    	if( node.getTerminal() == "*" ) {
    		head = new AutomataNode();
    		tail = new AutomataNode();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, head, expression.getHead());
    		transition.addToken();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, expression.getTail(), tail);
    		transition.addToken();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, expression.getTail(), expression.getHead());
    		transition.addToken();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, head, tail);
    		transition.addToken();
    	} else if( node.getTerminal() == "+" ){
    		head = new AutomataNode();
    		tail = new AutomataNode();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, head, expression.getHead());
    		transition.addToken();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, expression.getTail(), tail);
    		transition.addToken();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, expression.getTail(), expression.getHead());
    		transition.addToken();
    	} else if( node.getTerminal() == "?" ) {
    		head = expression.getHead();
    		tail = expression.getTail();
    		transition = 
    				AutomataTransition.toNode(AutomataType.E_NFA, expression.getHead(), expression.getTail());
    		transition.addToken();
    	} else {
    		head = expression.getHead();
    		tail = expression.getTail();
    	}
    	
    	return new AutomataExpression(head, tail);
    }
    
    public static AutomataTransition terminal( SimpleNode node, AutomataNode head ) {
		System.out.println("terminal: Node " + node.toString());
    	AutomataTransition transition = AutomataTransition.toNode(AutomataType.E_NFA, head, new AutomataNode());
    	if( node.children.length != 0 ) {
    		node = (SimpleNode) node.children[0]; //T1
    		node = (SimpleNode) node.children[0]; //Letter or Number
    	}
		transition.addToken(node.getTerminal());
		System.out.println("	terminal: Node transition " + node.getTerminal());
		return transition;
    }
    
    
}