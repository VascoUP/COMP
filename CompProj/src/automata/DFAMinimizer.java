package automata;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DFAMinimizer {
    public DFAMinimizer(AutomataTable dfa) {
        System.out.println("Before minimizing dfa");
        System.out.println(PrintAutomata.getString(dfa));
        minimize(dfa);
        System.out.println("After minimizing dfa");
        System.out.println(PrintAutomata.getString(dfa));
    }

    private class SetTransitations {
        private int id;
        private Set<Integer> sets;
        private Set<AutomataState> states;

        private SetTransitations(int id) {
            this.id = id;
            states = new HashSet<>();
            sets = new HashSet<>();
        }

        private void addState(AutomataState state) {
            states.add(state);
        }

        private void removeState(AutomataState state) {
            states.remove(state);
        }

        private void addSet(Integer id) {
            sets.add(id);
        }

        private boolean containsState(AutomataState state) {
            return states.contains(state);
        }
    }

    private void minimize(AutomataTable dfa) {
        // Separate DFA in 2 sets
        //  - Set 1 final states
        //  - Set 2 all other states
        List<SetTransitations> sets = initialSets(dfa);

        // Apply all transitions to the states
        //  - If ALL transitions land on the respective set then add it
        //  - Else check if there is any set that includes the sets to which it can transit
        //  - Do this until 2 iterations are equal
        boolean isDiff = false;
        do {
            isDiff = setsIterate(dfa, sets);
        } while(isDiff);
    }

    private List<SetTransitations> initialSets(AutomataTable dfa) {
        List<SetTransitations> sets = new ArrayList<>();
        SetTransitations acceptStates = new SetTransitations(1);
        SetTransitations otherStates = new SetTransitations(2);
        sets.add(acceptStates);
        sets.add(otherStates);
        for(AutomataState state : dfa.getStateGrammar().keySet()) {
            if(state.getAccept()) acceptStates.addState(state);
            else otherStates.addState(state);
        }
        return sets;
    }

    private boolean setsIterate(AutomataTable dfa, List<SetTransitations> sets) {
        return false;
    }
}
