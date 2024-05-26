import java.util.*;

public class SeatingArrangementSolver {

    // Define your dislike matrix here
    static String[] names = {"Ahmed", "Salem", "Ayman", "Hani", "Kamal", "Samir", "Hakam", "Fuad", "Ibrahim", "Khalid"};

    // Define the dislike matrix here
    static int[][] dislikeMatrix = {
            {0, 68, 55, 30, 82, 48, 33, 10, 76, 43},
            {68, 0, 90, 11, 76, 20, 55, 17, 62, 99},
            {55, 90, 0, 70, 63, 96, 51, 90, 88, 64},
            {30, 11, 70, 0, 91, 86, 78, 99, 53, 92},
            {82, 76, 63, 91, 0, 43, 88, 53, 42, 75},
            {48, 20, 96, 86, 43, 0, 63, 97, 37, 26},
            {33, 55, 51, 78, 88, 63, 0, 92, 87, 81},
            {10, 17, 90, 99, 53, 97, 92, 0, 81, 78},
            {76, 62, 88, 53, 42, 37, 87, 81, 0, 45},
            {43, 99, 64, 92, 75, 26, 81, 78, 45, 0}
    };

    // Define your heuristic function h(n) using the dislike matrix
    static int heuristic(ArrayList<Integer> arrangement) {
        int sumDislike = 0;
        for (int i = 0; i < arrangement.size(); i++) {
            int left = arrangement.get((i - 1 + arrangement.size()) % arrangement.size());
            int right = arrangement.get((i + 1) % arrangement.size());
            sumDislike += dislikeMatrix[arrangement.get(i)][left];
            sumDislike += dislikeMatrix[arrangement.get(i)][right];
        }
        return sumDislike;
    }

    // Define the non-linear dislike cost function f(x)
    public static int nonLinearDislikeCost(int dislikePercentage) {
        return (int) Math.pow(dislikePercentage, 2);
    }


    // Check if an element exists in a list
    static boolean contains(ArrayList<Integer> arr, int target) {
        return arr.stream().anyMatch(num -> num == target);
    }


    // Calculate the cost of a seating arrangement
    static int calculateCost(ArrayList<Integer> arrangement) {
        int cost = 0;
        int n = arrangement.size();

        for (int i = 0; i < n; i++) {
            int left = arrangement.get((i - 1 + n) % n);
            int right = arrangement.get((i + 1) % n);
            cost += nonLinearDislikeCost(dislikeMatrix[arrangement.get(i)][left]);
            cost += nonLinearDislikeCost(dislikeMatrix[arrangement.get(i)][right]);
        }

        return cost;
    }

    // Implement Uniform Cost Search (UCS)
    static ArrayList<Integer> uniformCostSearch() {
        // Priority queue to store states based on their cost
        PriorityQueue<State> pq = new PriorityQueue<>();

        // Set to keep track of explored states
        HashSet<ArrayList<Integer>> explored = new HashSet<>();

        // Initial state: empty arrangement
        ArrayList<Integer> initialArrangement = new ArrayList<>();
        int initialCost = Integer.MAX_VALUE;
        pq.add(new State(initialCost, initialArrangement));

        while (!pq.isEmpty()) {
            // Get the state with the lowest cost
            State currentState = pq.poll();
            int currentCost = currentState.cost;
            ArrayList<Integer> currentArrangement = currentState.arrangement;

            // Check if the state is the goal state (all persons seated)
            if (currentArrangement.size() == names.length) {
                return currentArrangement;
            }

            // Add current state to explored set
            explored.add(new ArrayList<>(currentArrangement));

            // Generate successor states
            for (int i = 0; i < names.length; i++) {
                if (!currentArrangement.contains(i)) {
                    ArrayList<Integer> successorArrangement = new ArrayList<>(currentArrangement);
                    successorArrangement.add(i);

                    // Calculate the cost of the successor state
                    int cost = calculateCost(successorArrangement);

                    // Add successor state to priority queue if not explored
                    if (!explored.contains(successorArrangement)) {
                        pq.add(new State(cost, successorArrangement));
                    }
                }
            }
        }

        // No solution found
        return null;
    }

    // Implement Greedy Search
    static ArrayList<Integer> greedySearch() {
        ArrayList<Integer> arrangement = new ArrayList<>();
        boolean[] chosen = new boolean[names.length];

        // Start with the first person
        arrangement.add(0);
        chosen[0] = true;

        // Greedily choose the next person to minimize dislike cost
        for (int i = 1; i < names.length; i++) {
            int currentPerson = arrangement.get(arrangement.size() - 1);
            int minDislikeCost = Integer.MAX_VALUE;
            int nextPerson = -1;

            for (int j = 0; j < names.length; j++) {
                if (!chosen[j]) {
                    int dislikeCost = nonLinearDislikeCost(dislikeMatrix[currentPerson][j]);
                    if (dislikeCost < minDislikeCost) {
                        minDislikeCost = dislikeCost;
                        nextPerson = j;
                    }
                }
            }

            arrangement.add(nextPerson);
            chosen[nextPerson] = true;
        }

        return arrangement;
    }

    // Implement A* Search
    static ArrayList<Integer> aStarSearch() {
        // Priority queue to store states based on their evaluation function value
        PriorityQueue<State> pq = new PriorityQueue<>();

        // Set to keep track of explored states
        HashSet<ArrayList<Integer>> explored = new HashSet<>();

        // Initial state: empty arrangement
        ArrayList<Integer> initialArrangement = new ArrayList<>();
        int initialCost = Integer.MAX_VALUE;
        int initialEval = heuristic(initialArrangement);
        pq.add(new State(initialCost, initialArrangement, initialEval));

        while (!pq.isEmpty()) {
            // Get the state with the lowest evaluation function value
            State currentState = pq.poll();
            int currentCost = currentState.cost;
            ArrayList<Integer> currentArrangement = currentState.arrangement;

            // Check if the state is the goal state (all persons seated)
            if (currentArrangement.size() == names.length) {
                return currentArrangement;
            }

            // Add current state to explored set
            explored.add(new ArrayList<>(currentArrangement));

            // Generate successor states
            for (int i = 0; i < names.length; i++) {
                if (!currentArrangement.contains(i)) {
                    ArrayList<Integer> successorArrangement = new ArrayList<>(currentArrangement);
                    successorArrangement.add(i);

                    // Calculate the cost of the successor state
                    int cost = calculateCost(successorArrangement);

                    // Calculate the evaluation function value of the successor state
                    int evalVal = cost + heuristic(successorArrangement);

                    // Add successor state to priority queue if not explored
                    if (!explored.contains(successorArrangement)) {
                        pq.add(new State(cost, successorArrangement, evalVal));
                    }
                }
            }
        }

        // No solution found
        return null;
    }

    // Main method to test the algorithms
    public static void main(String[] args) {
        System.out.println("\nUniform Cost Search:");
        ArrayList<Integer> ucsResult = uniformCostSearch();
        printResult("UCS", ucsResult);

        System.out.println("\nGreedy Search:");
        ArrayList<Integer> greedyResult = greedySearch();
        printResult("Greedy", greedyResult);

        System.out.println("\nA* Search:");
        ArrayList<Integer> aStarResult = aStarSearch();
        printResult("A*", aStarResult);
    }

    // Print the result of the algorithm
    static void printResult(String algorithm, ArrayList<Integer> arrangement) {
        if (arrangement == null) {
            System.out.println("No solution found for " + algorithm);
        } else {
            int cost = calculateCost(arrangement);
            System.out.print("Seating arrangement for " + algorithm + ": ");
            for (int i : arrangement) {
                System.out.print(names[i] + " ");
            }
            System.out.println();
            System.out.println("Cost for " + algorithm + " arrangement: " + cost);
        }
    }
}
