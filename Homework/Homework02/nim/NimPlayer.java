/**********************************
 * Author: Moriah Tolliver
 * Last Edit Made: 2/28/2019
 **********************************/

package nim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Artificial Intelligence responsible for playing the game of Nim!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class NimPlayer {
    
    private final int MAX_REMOVAL;
    
    NimPlayer (int MAX_REMOVAL) {
        this.MAX_REMOVAL = MAX_REMOVAL;
    }
    
    /**
     * Get children resulting from all possible actions mapped with their minimax scores
     * @param remaining int to get children from
     * @return Map<GameTreeNode, Integer> of children mapped to their minimax scores
     */
    public Map<GameTreeNode, Integer> getChildren( int remaining ) {
        Map<GameTreeNode, Integer> visited = new HashMap<>();
    	Map<GameTreeNode, Integer> children = new HashMap<>();
        
        for ( int action = 1; action <= MAX_REMOVAL && remaining-action >= 0; action++ ) {
        	GameTreeNode child = new GameTreeNode( remaining-action, action, false );
        	child.score = alphaBetaMinimax( child, Integer.MIN_VALUE, Integer.MAX_VALUE, false, visited );
        	children.put( child, child.score );
        }
        
        return children;
    }
    
    /**
     * Returns best action for player to take given a class of nodes to analyze the actions of
     * @param nodes to look for best action from
     * @return int number of stones to take
     */
    public int getBestAction( Map<GameTreeNode, Integer> nodes ) {
        GameTreeNode maxNode = new GameTreeNode(1, 1, false);

    	for ( Map.Entry<GameTreeNode, Integer> node : nodes.entrySet() ) {
        	
        	maxNode = ( Math.max(maxNode.score, node.getValue()) == node.getValue() ) ? node.getKey() : maxNode;
        }
    	
    	return maxNode.action;
    }
    
    /**
     * 
     * @param   remaining   Integer representing the amount of stones left in the pile
     * @return  An int action representing the number of stones to remove in the range
     *          of [1, MAX_REMOVAL]
     */
    public int choose (int remaining) {
        Map<GameTreeNode, Integer> children = getChildren( remaining );
        return getBestAction( children );
    }
    
    /**
     * Constructs the minimax game tree by the tenets of alpha-beta pruning with
     * memoization for repeated states.
     * @param   node    The root of the current game sub-tree
     * @param   alpha   Smallest minimax score possible
     * @param   beta    Largest minimax score possible
     * @param   isMax   Boolean representing whether the given node is a max (true) or min (false) node
     * @param   visited Map of GameTreeNodes to their minimax scores to avoid repeating large subtrees
     * @return  Minimax score of the given node + [Side effect] constructs the game tree originating
     *          from the given node
     */
    private int alphaBetaMinimax (GameTreeNode node, int alpha, int beta, boolean isMax, Map<GameTreeNode, Integer> visited) {
    	if ( visited.containsKey(node) ) { //return score of previously calculated node
    		return visited.get(node);
    	}
    	
    	if ( node.remaining == 0 ) {
    		return (isMax) ? 0 : 1;
    	}
    	    	
    	if (isMax) {
    		int v = Integer.MIN_VALUE;
    		for ( int action = 1; action <= MAX_REMOVAL && node.remaining-action >= 0; action++ ) {
    			GameTreeNode child = new GameTreeNode(node.remaining - action, action, false);
				v = Math.max( v , alphaBetaMinimax(child, alpha, beta, false, visited) );
				alpha = Math.max(alpha, v);
				if ( beta <= alpha ) {
					break;
				}
    		}
    		visited.put(node, v);
    		return v;
    	} else {
    		int v = Integer.MAX_VALUE;
    		for ( int action = 1; action <= MAX_REMOVAL && node.remaining-action >= 0; action++ ) {
    			GameTreeNode child = new GameTreeNode(node.remaining - action, action, true);
				v = Math.min( v , alphaBetaMinimax(child, alpha, beta, true, visited) );
				beta = Math.min(beta, v);
				if ( beta <= alpha ) {
					break;
				}
    		}
    		visited.put(node, v);
    		return v;
    	}
    	
    }

}

/**
 * GameTreeNode to manage the Nim game tree.
 */
class GameTreeNode {
    
    int remaining, action, score;
    boolean isMax;
    ArrayList<GameTreeNode> children;
    
    /**
     * Constructs a new GameTreeNode with the given number of stones
     * remaining in the pile, and the action that led to it. We also
     * initialize an empty ArrayList of children that can be added-to
     * during search, and a placeholder score of -1 to be updated during
     * search.
     * 
     * @param   remaining   The Nim game state represented by this node: the #
     *          of stones remaining in the pile
     * @param   action  The action (# of stones removed) that led to this node
     * @param   isMax   Boolean as to whether or not this is a maxnode
     */
    GameTreeNode (int remaining, int action, boolean isMax) {
        this.remaining = remaining;
        this.action = action;
        this.isMax = isMax;
        children = new ArrayList<>();
        score = -1;
    }
    
    @Override
    public boolean equals (Object other) {
        return other instanceof GameTreeNode 
            ? remaining == ((GameTreeNode) other).remaining && 
              isMax == ((GameTreeNode) other).isMax &&
              action == ((GameTreeNode) other).action
            : false;
    }
    
    @Override
    public int hashCode () {
        return remaining + ((isMax) ? 1 : 0);
    }
    
    public String toString () {
    	return "[rem: " + remaining + ", action: " + action + ", isMax: " + isMax + "]";
    }
    
}
