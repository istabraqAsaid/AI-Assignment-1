import java.util.ArrayList;

public class State implements Comparable<State> {
    int cost;
    ArrayList<Integer> arrangement;
    int eval;

    public State(int cost, ArrayList<Integer> arrangement) {
        this.cost = cost;
        this.arrangement = arrangement;
    }

    public State(int cost, ArrayList<Integer> arrangement, int eval) {
        this.cost = cost;
        this.arrangement = arrangement;
        this.eval = eval;
    }

    @Override
    public int compareTo(State other) {
        return Integer.compare(this.cost, other.cost);
    }
}