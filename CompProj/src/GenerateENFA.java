import automata.AutomataNode;

public class GenerateENFA {
	private static final String START_NAME = "Start";
	private static final String EXPRESSION_NAME = "Expression";
	private static final String EXPRESSION_TYPE_NAME = "ExpressionType";
	private static final String PIPE_EXPRESSION_NAME = "PipeExpression";
	
    public static AutomataNode enfa( SimpleNode root ) {
        return null;
    }
    
    public static AutomataNode start( SimpleNode root ) {
    	if( root.toString() != START_NAME )
    		return null;
    	return root.children.length > 0 ? expression((SimpleNode) root.children[0]) : null;
    }
    
    public static AutomataNode expression( SimpleNode node ) {
    	if( node.toString() != EXPRESSION_NAME )
    		return null;
    	AutomataNode enfa = null;
    	for( SimpleNode n : (SimpleNode[])node.children ) {
    		if( enfa == null ) enfa = expressionType(n);
    		else expressionType(n);
    	}
    	return enfa;
    }
    
    public static AutomataNode pipeExpression( SimpleNode node ) {
    	if( node.toString() != PIPE_EXPRESSION_NAME )
    		return null;
    	return node.children.length > 0 ? expression((SimpleNode) node.children[0]) : null;
    }
    
    public static AutomataNode expressionType( SimpleNode node ) {
    	if( node.toString() != EXPRESSION_TYPE_NAME )
    		return null;
    	/*for( SimpleNode n : (SimpleNode[]) node.children ) {
    		
    	}*/
    	return null;
    }
}