package automata;

import java.util.*;
import java.util.List;

public class DFAMinimizer {
    private static int setTransitionsID = 1;
    private AutomataTable resultingAutomata;

    public DFAMinimizer(AutomataTable dfa) {
        resultingAutomata = minimize(dfa);
    }

    public AutomataTable getDFA() {
        return resultingAutomata;
    }

    private class SetTransitations {
        private int id;
        private SetTransitations[] transitions;
        private Set<AutomataState> states;

        private SetTransitations(int id) {
            this.id = id;
            states = new HashSet<>();
            transitions = new SetTransitations[256];
            Arrays.fill(transitions, null);
        }

        private void addState(AutomataState state) {
            states.add(state);
        }

        private void removeState(AutomataState state) {
            states.remove(state);
        }

        private boolean containsState(AutomataState state) {
            return states.contains(state);
        }

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

    private AutomataTable minimize(AutomataTable dfa) {
        List<SetTransitations> sets = initialSets(dfa);
        calculateMinimizedDfa(dfa, sets);
        return buildMinimizedDfa(sets);
        //return dfa;
    }


    private List<SetTransitations> initialSets(AutomataTable dfa) {
        List<SetTransitations> sets = new ArrayList<>();
        SetTransitations acceptStates = new SetTransitations(setTransitionsID++);
        SetTransitations otherStates = new SetTransitations(setTransitionsID++);
        sets.add(acceptStates);
        sets.add(otherStates);
        for(AutomataState state : dfa.getStateGrammar().keySet()) {
            if(state.getAccept()) acceptStates.addState(state);
            else otherStates.addState(state);
        }
        return sets;
    }

    private void calculateMinimizedDfa(AutomataTable dfa, List<SetTransitations> sets) {
        boolean isDiff;
        do { isDiff = setsIterate(dfa, sets);
        } while(isDiff);
    }

    private AutomataTable buildMinimizedDfa(List<SetTransitations> sets) {
        Map<AutomataState, SetTransitations> stateSet = new HashMap<>();
        AutomataTable newDfa = new AutomataTable(AutomataType.DFA);
        for(SetTransitations set : sets) {
            if(isInitialSet(set)) {
                AutomataState state = setToState(newDfa, set);
                System.out.println("New first state " + state);
                stateSet.put(state, set);
                break;
            }
        }
        for(SetTransitations set : sets) {
            if(!isInitialSet(set)) {
                AutomataState state = setToState(newDfa, set);
                System.out.println("New state " + state);
                stateSet.put(state, set);
            }
        }
        return newDfa(newDfa, stateSet);
    }


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

    private void repositionState(List<SetTransitations> sets, AutomataState state, SetTransitations[] transitations) {
        for(SetTransitations set : sets) {
            if(equalSets(set.transitions, transitations)) {
                set.addState(state);
                return;
            }
        }
        setsAddSet(sets, state, transitations);
    }

    private void setsAddSet(List<SetTransitations> sets, AutomataState state, SetTransitations[] transitations) {
        SetTransitations newSet = new SetTransitations(setTransitionsID++);
        newSet.addState(state);
        newSet.transitions = transitations;
        sets.add(newSet);
    }


    private AutomataTable newDfa(AutomataTable newDfa, Map<AutomataState, SetTransitations> stateSet) {
        for(AutomataState state : stateSet.keySet())
            newTransition(newDfa, stateSet, state);
        return newDfa;
    }

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
            if(dst == null) {
                throw new Error("Unable to find state");
            }
            System.out.println("Add new path " + String.valueOf((char) i) + " src: " + state + " dst: " + dst);
            newDfa.stateSetTransition(state, String.valueOf((char) i), dst);
        }
    }

    private AutomataState setToState(AutomataTable dfa, SetTransitations set) {
        boolean start = false, accept = false;
        if(isInitialSet(set)) start = true;
        if(isFinalSet(set)) accept = true;
        int id = dfa.addState(start, accept);
        return dfa.getStateByID(id);
    }

    private boolean isInitialSet(SetTransitations set) {
        for(AutomataState state : set.states)
            if(state.getStart())
                return true;
        return false;
    }

    private boolean isFinalSet(SetTransitations set) {
        for(AutomataState state : set.states)
            if(state.getAccept())
                return true;
        return false;
    }


    private void getStateTransitions(AutomataTable dfa, List<SetTransitations> sets, Map<Integer, SetTransitations[]> elementTransitions, AutomataState state) {
        SetTransitations[] setTransitations = new SetTransitations[256];
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

    private SetTransitations getStateSet(List<SetTransitations> sets, AutomataState state) {
        for( SetTransitations set : sets )
            if(set.containsState(state))
                return set;
        return null;
    }

    private SetTransitations getSetByID(List<SetTransitations> sets, int setID) {
        for( SetTransitations set : sets )
            if(set.id == setID)
                return set;
        return null;
    }


    private boolean equalSets(SetTransitations[] sets1, SetTransitations[] sets2) {
        if(sets1.length != sets2.length) return false;
        for(int i = 0; i < sets1.length; i++)
            if(sets1[i] != sets2[i])
                return false;
        return true;
    }
}
