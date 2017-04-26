package automata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import automata.AutomataNode;
import automata.AutomataType;

public class AutomataTransition {
    private AutomataNode src;
    private AutomataNode dst;

    private Set<String> tokens;
    private EmptyToken empty;

    public AutomataTransition(AutomataType type, AutomataNode src, AutomataNode dst) {
        this.src = src;
        this.dst = dst;
        tokens = new HashSet<>();
        empty = new EmptyToken(type);
    }

    public AutomataTransition(AutomataType type) {
        empty = new EmptyToken(type);
    }
    
    public AutomataNode getSrc() {
        return src;
    }

    public AutomataNode getDst() {
        return dst;
    }

    public Set<String> getTokens() {
        return tokens;
    }


    public void addToken() {
    	empty.setUsed();
    }
    
    public void addToken(String c) {
        tokens.add(c);
    }
    
    public boolean validInput() {
    	return empty.getUsed();
    }
    
    public boolean validInput(char c) {
    	return tokens.contains(c);
    }

    
    public static AutomataTransition toNode(AutomataType type, AutomataNode src, AutomataNode dst) {
    	AutomataTransition t = new AutomataTransition(AutomataType.E_NFA, src, dst);
    	src.addTransition(t);
    	return t;
    }
    
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	if( empty.getUsed() )
    		builder.append("Empty string, ");
    	Iterator<String> iter = tokens.iterator();
    	while(iter.hasNext()) {
    		String str = iter.next();
    		builder.append(str);
    		builder.append(", ");
    	}
    	return new String(builder);
    }
}