package automata;

import java.util.HashSet;
import java.util.Set;

import automata.AutomataNode;
import automata.AutomataType;

public class AutomataTransition {
    private AutomataNode src;
    private AutomataNode dst;

    private Set<Character> tokens;
    private EmptyToken empty;

    public AutomataTransition(AutomataType type, AutomataNode src, AutomataNode dst) {
        this.src = src;
        this.dst = dst;
        tokens = new HashSet<Character>();
        empty = new EmptyToken(type);
    }

    
    public AutomataNode getSrc() {
        return src;
    }

    public AutomataNode getDst() {
        return dst;
    }

    public Set<Character> getTokens() {
        return tokens;
    }


    public void addToken() {
    	empty.setUsed();
    }
    
    public void addToken(char c) {
        tokens.add(c);
    }
    
    public boolean validInput() {
    	return empty.getUsed();
    }
    
    public boolean validInput(char c) {
    	return tokens.contains(c);
    }
}