//**********************************************************
// Authors: Ayla Khoshaba, Cooper LaRhette, Moriah Tolliver
//**********************************************************

// package pathfinder.informed;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {

    /**
     * Returns cost required to go from the initial state of this problem maze to the input MazeState
     * @param state a MazeState representing which state to calculate cost of transition to
     * @param problem to evaluate state cost in
     * @return int cost of going from initial state to input state
     */
    public static int getTotalCost (MazeState state, MazeProblem problem) {
        return (state.equals(problem.INITIAL_STATE)) ? 0 : (problem.isMudTile(state)) ? problem.manhattanDistance(problem.INITIAL_STATE , state) + 2 : problem.manhattanDistance(problem.INITIAL_STATE , state);
    }

    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     *
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> solve(MazeProblem problem) {
        /// TODO: Initialize frontier--The frontier holds SearchTreeNodes!
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<>(0 , new SearchTreeNodeComparator());
        ArrayList<String> result = new ArrayList<>();
        // Map<String, MazeState> transitions;
        // SearchTreeNode currentNode;
        //
        // // TODO: Add new SearchTreeNode representing the problem's initial state to the
        // // frontier. Since this is the initial state, the node's action and parent will
        // // be null
        // frontier.add( new SearchTreeNode( problem.INITIAL_STATE, null, null, getTotalCost(problem.INITIAL_STATE, problem) ));
        //
        // // TODO: Loop: as long as the frontier is not empty...
        //
        // while (!frontier.isEmpty()) {
        //
        //
        //     // TODO: Get the next node to expand by the ordering of breadth-first search
        //     currentNode = frontier.peek();
        //
        //     // TODO: If that node's state is the goal (see problem's isGoal method),
        //     // you're done! Return the solution
        //     // [Hint] Use a helper method to collect the solution from the current node!
        //     if (problem.isGoal(currentNode.state)) {
        //         return buildPath(currentNode);
        //     }
        //
        //     // TODO: Otherwise, must generate children to keep searching. So, use the
        //     // problem's getTransitions method from the currently expanded node's state...
        //     transitions = problem.getTransitions(currentNode.state);
        //
        //     // TODO: ...and *for each* of those transition states...
        //     // [Hint] Look up how to iterate through <key, value> pairs in a Map -- an
        //     // example of this is already done in the MazeProblem's getTransitions method
        //     for (Map.Entry<String, MazeState> action : transitions.entrySet()) {
        //         // TODO: ...add a new SearchTreeNode to the frontier with the appropriate
        //         // action, state, and parent
        //         frontier.add(new SearchTreeNode(action.getValue(), action.getKey(), currentNode, getTotalCost(action.getValue(), problem) ));
        //     }
        //     frontier.remove();
        // }

        // Should never get here, but just return null to make the compiler happy
        return null;
    }

    public static ArrayList<String> buildPath(SearchTreeNode root) {
        Stack<String> temp = new Stack<>();
        ArrayList<String> result = new ArrayList<>();
        while (root.parent != null) {
            temp.add(root.action);
            root = root.parent;
        }
        while (!temp.isEmpty()) {
            result.add(temp.pop());
        }
        return result;
    }

    public static void main(String args[]) {
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.....X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        System.out.println(Pathfinder.solve(prob));
    }
}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode{

    MazeState state;
    String action;
    SearchTreeNode parent;
    int cost;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     *
     * @param state  The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode(MazeState state, String action, SearchTreeNode parent, int cost) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.cost = cost;
    }
}

class SearchTreeNodeComparator implements Comparator<SearchTreeNode> {
    // Overriding compare()method of Comparator
    // for ascending order of cost
    public int compare(SearchTreeNode s1, SearchTreeNode s2) {
        if (s1.cost < s2.cost) {
            return -1;
        } else if (s1.cost > s2.cost) {
            return 1;
        }
        return 0;
    }
}
