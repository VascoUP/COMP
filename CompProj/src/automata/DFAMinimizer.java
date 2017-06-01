package automata;


import java.awt.*;
import java.util.*;
import java.util.List;

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

        // need to renumber automata id's
    }

    private void minimize(AutomataTable dfa) {
        boolean[][] D = new boolean[dfa.getAutomataSize()][dfa.getAutomataSize()];
        List<List<HashSet<Point>>> S = new ArrayList<>();

        for (AutomataState state : dfa.getStateGrammar().keySet()) {
            List<HashSet<Point>> innerList = new ArrayList<>();
            for (AutomataState ignored : dfa.getStateGrammar().keySet()) {
                Arrays.fill(D[state.getID()], false);
                innerList.add(new HashSet<>());
            }
            S.add(innerList);
        }

        for (AutomataState state : dfa.getStateGrammar().keySet())
            for (AutomataState state2 : dfa.getStateGrammar().keySet())
                if (state.getAccept() != state2.getAccept())
                    D[state.getID()][state2.getID()] = true;

        for (AutomataState state : dfa.getStateGrammar().keySet())
            for (AutomataState state2 : dfa.getStateGrammar().keySet()) {
                if (D[state.getID()][state2.getID()])
                    continue;

                boolean distinguished = false;
                HashMap<String, Set<AutomataState>> transitions = dfa.getStateGrammar().get(state);
                for (Map.Entry<String, Set<AutomataState>> entry : transitions.entrySet()) {
                    Set<AutomataState> Ms = entry.getValue();
                    Set<AutomataState> Ns = dfa.getStateGrammar().get(state2).get(entry.getKey());
                    if (Ms == null || Ms.size() != 1 || Ns == null || Ns.size() != 1)
                        continue;

                    int m = entry.getValue().iterator().next().getID();
                    int n = Ns.iterator().next().getID();
                    if (D[m][n] || D[n][m]) {
                        dist(D, S, state.getID(), state2.getID());
                        distinguished = true;
                        break;
                    }
                }

                if (!distinguished) {
                    for (Map.Entry<String, Set<AutomataState>> entry : transitions.entrySet()) {
                        Set<AutomataState> Ms = entry.getValue();
                        Set<AutomataState> Ns = dfa.getStateGrammar().get(state2).get(entry.getKey());
                        if (Ms == null || Ms.size() != 1 || Ns == null || Ns.size() != 1)
                            continue;

                        int m = entry.getValue().iterator().next().getID();
                        int n = Ns.iterator().next().getID();

                        if (m < n && !(state.getID() == m && state2.getID() == n))
                            S.get(m).get(n).add(new Point(state.getID(), state2.getID()));
                        else if (m > n && !(state.getID() == n && state2.getID() == m))
                            S.get(n).get(m).add(new Point(state.getID(), state2.getID()));
                    }
                }
            }

        //mergeStates();
    }

    private void dist(boolean[][] D, List<List<HashSet<Point>>> S, int i, int j) {
        _dist(D, S, new Point(i, j), new HashSet<>());
    }

    private void _dist(boolean[][] D, List<List<HashSet<Point>>> S, Point point, HashSet<Point> visited) {
        if (visited.contains(point))
            return;

        int i = point.x, j = point.y;
        D[i][j] = true;
        visited.add(point);
        for (Point pair : S.get(i).get(j))
            _dist(D, S, pair, visited);
    }
}
