// package pathfinder.uninformed;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {

    /**
     * Given a SearchTreeNode, which specifies the goal node of the MazeProblem, returns a sequence of actions that leads from
     * the initial to a goal state.
     *
     * @param goalNode A SearchTreeNode that specifies the goal node.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> getSolution( SearchTreeNode goalNode ) {
        ArrayList<String> solution = new ArrayList<String>();
        SearchTreeNode current = goalNode;

        //get steps taken in reverse order
        while ( current.parent != null ) {
            solution.add( current.action );
            current = current.parent;
        }

        //reverse solution array list
        for ( int i = 0; i < solution.size()/2; i++ ) {
            String temp = solution.get( solution.size() - 1 - i);
            solution.set( solution.size() - 1 - i, solution.get(i) );
            solution.set( i , temp );
        }
        return solution;
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
    public static ArrayList<String> solve (MazeProblem problem) {
        // TODO: Initialize frontier -- what data structure should you use here for
        // breadth-first search? Recall: The frontier holds SearchTreeNodes!
        Queue<SearchTreeNode> frontier = new LinkedList<>();

        // TODO: Add new SearchTreeNode representing the problem's initial state to the
        // frontier. Since this is the initial state, the node's action and parent will
        // be null
        frontier.add( new SearchTreeNode( problem.INITIAL_STATE, null, null ) );

        // TODO: Loop: as long as the frontier is not empty...
        while( !frontier.isEmpty() ) {
            // TODO: Get the next node to expand by the ordering of breadth-first search
            SearchTreeNode currentNode = frontier.peek();
            // TODO: If that node's state is the goal (see problem's isGoal method),
            // you're done! Return the solution
            // [Hint] Use a helper method to collect the solution from the current node!
            if ( problem.isGoal( currentNode.state ) ) {
                return getSolution( frontier.remove() );
            }

            // TODO: Otherwise, must generate children to keep searching. So, use the
            // problem's getTransitions method from the currently expanded node's state...
            Map<String, MazeState> transitions = problem.getTransitions( currentNode.state );

            // TODO: ...and *for each* of those transition states...
            // [Hint] Look up how to iterate through <key, value> pairs in a Map -- an
            // example of this is already done in the MazeProblem's getTransitions method
            //
            for ( Map.Entry<String, MazeState> transition : transitions.entrySet() ) {

                // TODO: ...add a new SearchTreeNode to the frontier with the appropriate
                // action, state, and parent
                SearchTreeNode newNode = new SearchTreeNode( transition.getValue() , transition.getKey(), currentNode  );
                frontier.add( newNode );
            }
            frontier.remove();
        }

        // Should never get here, but just return null to make the compiler happy
        ArrayList<String> solution = new ArrayList<String>();
        solution.add("Hey");
        return solution;
    }

    public static void main( String[] args ) {
        String[] maze = {
            "XXXX",
            "X.IX",
            "XG.X",
            "XXXX"
        };
        MazeProblem prob = new MazeProblem(maze);

        ArrayList<String> solution = Pathfinder.solve(prob);
        System.out.println( "The solution is " + solution.toString() );

        String[] maze2 = {
            "XXXXXXX",
            "X.....X",
            "XIX.X.X",
            "XX.X..X",
            "XG....X",
            "XXXXXXX"
        };
        MazeProblem prob2 = new MazeProblem(maze2);
        solution = Pathfinder.solve(prob2);
        System.out.println( "The solution is " + solution.toString() );


        System.out.println("Testing getSolution()****************************" );
        SearchTreeNode root = new SearchTreeNode( new MazeState( 1 , 1), null, null );
        SearchTreeNode child1 = new SearchTreeNode( new MazeState( 2 , 1), "R", root );
        SearchTreeNode child2 = new SearchTreeNode( new MazeState( 2 , 2), "U", child1 );
        SearchTreeNode child3 = new SearchTreeNode( new MazeState( 2, 3), "U", child2 );
        SearchTreeNode child4 = new SearchTreeNode( new MazeState( 1, 3), "L", child3 );
        System.out.println( Pathfinder.getSolution(child4) );


        root = new SearchTreeNode( new MazeState( 1 , 1), null, null );
        child1 = new SearchTreeNode( new MazeState( 2 , 1), "U", root );
        child2 = new SearchTreeNode( new MazeState( 2 , 2), "L", child1 );
        child3 = new SearchTreeNode( new MazeState( 2, 3), "L", child2 );
        child4 = new SearchTreeNode( new MazeState( 1, 3), "D", child3 );
        System.out.println( Pathfinder.getSolution(child4) );

    }

}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode {

    MazeState state;
    String action;
    SearchTreeNode parent;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     *
     * @param state The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode (MazeState state, String action, SearchTreeNode parent) {
        this.state = state;
        this.action = action;
        this.parent = parent;
    }

}
