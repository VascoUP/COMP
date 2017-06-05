package automata;

import java.util.*;
import java.util.List;

/**
 * 
 * This class creates the DFA's minimizer
 *
 */
public class DFAMinimizer {
    private static int setTransitionsID = 1;
    private AutomataTable resultingAutomata;

    /**
     * DFAMinimizer's constructor
     * @param dfa DFA's table
     */
    public DFAMinimizer(AutomataTable dfa) {
        resultingAutomata = minimize(dfa);
    }

    /**
     * Gets the DFA's table
     * @return The DFA's table
     */
    public AutomataTable getDFA() {
        return resultingAutomata;
    }

    /**
 	 *
     * Creates a set of transactions
     *
     */
    private class SetTransitations {
        private int id;
        private SetTransitations[] transitions;
        private Set<AutomataState> states;

        /**
         * SetTransaction's constructor
         * @param id State's identifier
         */
        private SetTransitations(int id) {
            this.id = id;
            states = new HashSet<>();
            transitions = new SetTransitations[256];
            Arrays.fill(transitions, null);
        }

        /**
         * Adds a state to the automata state's set
         * @param state New automata's state to be added
         */
        private void addState(AutomataState state) {
            states.add(state);
        }

        /**
         * Removes a automata's state from the automata state's set
         * @param state Automata's state that will be removed
         */
        private void removeState(AutomataState state) {
            states.remove(state);
        }

        /**
         * Verifies if the set contains a certain automata's state
         * @param state Automata's state that will be analyzed
         * @return true if the set contains the automata's state, false otherwise
         */
        private boolean containsState(AutomataState state) {
            return states.contains(state);
        }

        /**
         * Converts the set of automata's states into a string
         * @return The string created
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for(AutomataState state : states) {
                builder.append(state);
                builder.append(" ");
            }
            return builder.toString();
        }
    }

    /**
     * Minimizes the DFA's table
     * @param dfa DFA's table
     * @return The DFA's table minimized
     */
    private AutomataTable minimize(AutomataTable dfa) {
        List<SetTransitations> sets = initialSets(dfa);
        calculateMinimizedDfa(dfa, sets);
        return buildMinimizedDfa(sets);
    }

    /**
     * Gets the initial SetTransitations
     * @param dfa DFA's table
     * @return The initial SetTransitions
     */
    private List<SetTransitations> initialSets(AutomataTable dfa) {
        List<SetTransitations> sets = new ArrayList<>();
        SetTransitations acceptStates = new SetTransitations(setTransitionsID++);
        SetTransitations otherStates = new SetTransitations(setTransitionsID++);
        for(AutomataState state : dfa.getStateGrammar().keySet()) {
            if(state.getAccept()) acceptStates.addState(state);
            else otherStates.addState(state);
        }
        sets.add(acceptStates);
        if(otherStates.states.size() > 0)
            sets.add(otherStates);
        return sets;
    }

    /**
     * Calculates the DFA's minimized
     * @param dfa DFA's table
     * @param sets List of SetTransitations
     */
    private void calculateMinimizedDfa(AutomataTable dfa, List<SetTransitations> sets) {
        boolean isDiff;
        do { isDiff = setsIterate(dfa, sets);
        } while(isDiff);
    }

    /**
     * Builds the minimized DFA
     * @param sets List with the SetTransitations
     * @return The minimized DFA
     */
    private AutomataTable buildMinimizedDfa(List<SetTransitations> sets) {
        Map<AutomataState, SetTransitations> stateSet = new HashMap<>();
        AutomataTable newDfa = new AutomataTable(AutomataType.DFA);
        for(SetTransitations set : sets) {
            if(isInitialSet(set)) {
                AutomataState state = setToState(newDfa, set);
                stateSet.put(state, set);
                break;
            }
        }
        for(SetTransitations set : sets) {
            if(!isInitialSet(set)) {
                AutomataState state = setToState(newDfa, set);
                stateSet.put(state, set);
            }
        }
        return newDfa(newDfa, stateSet);
    }

    /**
     * Iterates through the list of SetTransitations
     * @param dfa DFA's table
     * @param sets List of SetTransitations
     * @return true if the list is organized, false otherwise
     */
    private boolean setsIterate(AutomataTable dfa, List<SetTransitations> sets) {
        Map<Integer, SetTransitations[]> elementTransitions = new HashMap<>();
        boolean changedSets = false;
        for(SetTransitations set : new ArrayList<>(sets)) {
            elementTransitions.clear();
            for(AutomataState state : set.states)
                getStateTransitions(dfa, sets, elementTransitions, state);
            if(!changedSets)
                changedSets = reorganizeSet(dfa, sets, set.id, elementTransitions);
            else
                reorganizeSet(dfa, sets, set.id, elementTransitions);
        }
        return changedSets;
    }

    /**
     * Reorganizes the list of SetTransitations
     * @param dfa DFA's table
     * @param sets List of SetTransitations
     * @param setID Set's identifier
     * @param elementTransitions Transition's from the element
     * @return true if the list is organized, false otherwise
     */
    private boolean reorganizeSet(AutomataTable dfa, List<SetTransitations> sets, int setID, Map<Integer, SetTransitations[]> elementTransitions) {
        SetTransitations[] setTransitations = null;
        SetTransitations set = getSetByID(sets, setID);
        boolean changedStatePositions = false;
        if( set == null ) return false;
        for(Map.Entry<Integer, SetTransitations[]> entry : elementTransitions.entrySet()) {
            if(setTransitations == null) {
                setTransitations = entry.getValue();
                set.transitions = setTransitations;
                continue;
            }

            if(equalSets(entry.getValue(), setTransitations))
                continue;

            changedStatePositions = true;
            AutomataState state = dfa.getStateByID(entry.getKey());
            set.removeState(state);
            repositionState(sets, state, entry.getValue());
        }
        return changedStatePositions;
    }

    /**
     * Reposes the state of transitation
     * @param sets List of SetTransitations
     * @param state Automata's state
     * @param transitations Element's transitations
     */
    private void repositionState(List<SetTransitations> sets, AutomataState state, SetTransitations[] transitations) {
        if(sets.size() < 1) return;
        SetTransitations set = sets.get(0);
        if(equalSets(set.transitions, transitations) && isFinalSet(set) == state.getAccept()) {
            set.addState(state);
            return;
        }
        setsAddSet(sets, state, transitations);
    }

    /**
     * Adds a SetTransitation to the list
     * @param sets List of SetTransitation
     * @param state Automata's state
     * @param transitations Element's transitions
     */
    private void setsAddSet(List<SetTransitations> sets, AutomataState state, SetTransitations[] transitations) {
        SetTransitations newSet = new SetTransitations(setTransitionsID++);
        newSet.addState(state);
        newSet.transitions = transitations;
        sets.add(newSet);
    }

    /**
     * Creates a new DFA's table with new transitions
     * @param newDfa DFA's where the transitions will be placed
     * @param stateSet Map with the automata's state transitions
     * @return The new DFA's table with new transitions
     */
    private AutomataTable newDfa(AutomataTable newDfa, Map<AutomataState, SetTransitations> stateSet) {
        for(AutomataState state : stateSet.keySet())
            newTransition(newDfa, stateSet, state);
        return newDfa;
    }

    /**
     * Creates a new transition
     * @param newDfa DFA's table
     * @param stateSet Map with the automata's state transitions
     * @param state Automata's state
     */
    private void newTransition(AutomataTable newDfa, Map<AutomataState, SetTransitations> stateSet, AutomataState state) {
        SetTransitations transitations = stateSet.get(state);
        for(int i = 0; i < transitations.transitions.length; i++) {
            if(transitations.transitions[i] == null)
                continue;
            AutomataState dst = null;
            for(Map.Entry<AutomataState, SetTransitations> entry : stateSet.entrySet()) {
                if(entry.getValue().id == transitations.transitions[i].id) {
                    dst = entry.getKey();
                    break;
                }
            }
            if(dst == null)
                throw new Error("Unable to find state");
            newDfa.stateSetTransition(state, String.valueOf((char) i), dst);
        }
    }

    /**
     * Converts the SetTransitations into a automata's state
     * @param dfa DFA's table
     * @param set SetTransitations
     * @return The SetTransitations converted into a automata's state
     */
    private AutomataState setToState(AutomataTable dfa, SetTransitations set) {
        boolean start = false, accept = false;
        if(isInitialSet(set)) start = true;
        if(isFinalSet(set)) accept = true;
        int id = dfa.addState(start, accept);
        return dfa.getStateByID(id);
    }

    /**
     * Verifies if the SetTransitations is the initial set
     * @param set SetTransitations
     * @return true if it's the initial set, false otherwise
     */
    private boolean isInitialSet(SetTransitations set) {
        for(AutomataState state : set.states)
            if(state.getStart())
                return true;
        return false;
    }

    /**
     * Verifies if the SetTransitations is the final set
     * @param set SetTransitations
     * @return true if it's the final set, false otherwise
     */
    private boolean isFinalSet(SetTransitations set) {
        for(AutomataState state : set.states)
            if(state.getAccept())
                return true;
        return false;
    }

    /**
     * Gets the state's transitions
     * @param dfa DFA's table
     * @param sets List of SetTransitations
     * @param elementTransitions Element's transitions
     * @param state Automata's state
     */
    private void getStateTransitions(AutomataTable dfa, List<SetTransitations> sets, Map<Integer, SetTransitations[]> elementTransitions, AutomataState state) {
        SetTransitations[] setTransitations = new SetTransitations[256];

        Set<AutomataState> states = dfa.getStateGrammar().get(state).get(AutomataGrammar.anyInput);
        if(states != null && states.size() == 1) {
            AutomataState dst = states.iterator().next();
            SetTransitations set;
            if((set = getStateSet(sets, dst)) != null)
                Arrays.fill(setTransitations, set);
            elementTransitions.put(state.getID(), setTransitations);
            return;
        }

        Arrays.fill(setTransitations, null);

        for( Map.Entry<String, Set<AutomataState>> entry : dfa.getStateGrammar().get(state).entrySet() ) {
            if(entry.getValue() == null || entry.getValue().size() != 1)
                continue;
            AutomataState dst = entry.getValue().iterator().next();
            SetTransitations set;
            if((set = getStateSet(sets, dst)) != null)
                setTransitations[entry.getKey().charAt(0)] = set;
        }

        elementTransitions.put(state.getID(), setTransitations);
    }

    /**
     * Gets the state's SetTransitations
     * @param sets List of SetTransitations
     * @param state Automata's state
     * @return The state's SetTransitations, null if the set doesn't contains the state
     */
    private SetTransitations getStateSet(List<SetTransitations> sets, AutomataState state) {
        for( SetTransitations set : sets )
            if(set.containsState(state))
                return set;
        return null;
    }

    /**
     * Gets the SetTransitations by identifier
     * @param sets List of SetTransitations
     * @param setID Set's identifier
     * @return The SetTransitations whose identifier is equal to the setID
     */
    private SetTransitations getSetByID(List<SetTransitations> sets, int setID) {
        for( SetTransitations set : sets )
            if(set.id == setID)
                return set;
        return null;
    }

    /**
     * Verifies if a the SetTranstation are equal
     * @param sets1 Array with SetTransitations
     * @param sets2 Array with SetTransitations
     * @return true if every element from the arrays are equal, false otherwise
     */
    private boolean equalSets(SetTransitations[] sets1, SetTransitations[] sets2) {
        if(sets1.length != sets2.length)
            return false;
        for(int i = 0; i < sets1.length; i++)
            if(sets1[i] != sets2[i])
                return false;
        return true;
    }
}
