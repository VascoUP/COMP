package automata;


import java.util.*;

public class DFAMinimizer {
    DFAMinimizer(AutomataTable dfa) {
        removeUnreachableStates(dfa);
        //minimize(dfa);
    }

    private void removeUnreachableStates(AutomataTable dfa) {
        boolean[] arr = new boolean[dfa.getAutomataSize()];
        Queue<Integer> process = new PriorityQueue<>();
        process.add(1);

        while(!process.isEmpty()) {
            int i = process.poll();
            AutomataState s = dfa.getStateByID(i);
            HashMap<String, Set<AutomataState>> transitions = dfa.getStateGrammar().get(s);
            for(Map.Entry<String, Set<AutomataState>> entry : transitions.entrySet())
                for(AutomataState dst : entry.getValue())
                    process.add(dst.getID());
            arr[i] = true;
        }

        for(int i = 0; i < arr.length; i++)
            if( !arr[i] )
                dfa.removeState(i);
    }
}
