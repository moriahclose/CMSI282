/******************************************
 * Authors: Moriah Tolliver, Justin Wiggins
 ******************************************/
package lcs;

import java.util.*;

public class LCS {
    
    /**
     * memoCheck is used to verify the state of your tabulation after
     * performing bottom-up and top-down DP. Make sure to set it after
     * calling either one of topDownLCS or bottomUpLCS to pass the tests!
     */
    public static int[][] memoCheck;
    
    // -----------------------------------------------
    // Shared Helper Methods
    // -----------------------------------------------
    
    /**
     * Returns the last character in a given string
     * @param s String to get last character from
     * @return char representing last character in string 
     */
    public static char lastChar(String s) {
    	return s.charAt(s.length()-1);
    }
    
    /**
     * Returns given string without its last character
     * @param s String to remove last character from
     * @return String without last character of input string
     */
    public static String removeLastChar(String s) {
    	return s.substring(0, s.length()-1);
    }
    
    /**
     * Returns a set of solutions to the given LCS problem
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @param memo 2D array representing the memoized table that has been constructed using bottom-up or top-down dynamic programming
     * @return HashSet of String solutions to given LCS problem
     */
    public static HashSet<String> collectSolution( String rStr, String cStr, int[][] memo ) {
    	if ( rStr.length() == 0 || cStr.length() == 0 ) {
    		return new HashSet<String>( Arrays.asList( "" ) );
    	} 
    	
    	HashSet<String> result = new HashSet<>();
    	if ( lastChar(rStr) == lastChar(cStr) ) {
    		for( String substr : collectSolution( removeLastChar(rStr), removeLastChar(cStr), memo ) ) {
    			result.add( substr + lastChar(rStr) );
    		}
    	} else {
    		
    		if ( memo[rStr.length()-1][cStr.length()] >= memo[rStr.length()][cStr.length()-1] ) {
        		result.addAll( collectSolution(rStr, removeLastChar(cStr), memo) );
        	} 
        	
        	if ( memo[rStr.length()][cStr.length()-1] >= memo[rStr.length()-1][cStr.length()] ) {
        		result.addAll( collectSolution(removeLastChar(rStr), cStr, memo) );
        	}
    	}
    	
    	return result;
    }
    
    // -----------------------------------------------
    // Bottom-Up LCS
    // -----------------------------------------------
    
    /**
     * Bottom-up dynamic programming approach to the LCS problem, which
     * solves larger and larger subproblems iterative using a tabular
     * memoization structure.
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's columns
     * @return The longest common subsequence between rStr and cStr +
     *         [Side Effect] sets memoCheck to refer to table
     */
    public static Set<String> bottomUpLCS (String rStr, String cStr) {
    	memoCheck = bottomUpTableFill(rStr, cStr);
    	return collectSolution( rStr, cStr, memoCheck);
    }
    
    /**
     * Fills the memoization table using bottom-up dynamic programming
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return 2D array containing the memoized values
     */
    public static int[][] bottomUpTableFill( String rStr, String cStr ) {
    	int[][] memoTable = new int[rStr.length()+1][cStr.length()+1];
    	String gutteredString1 = "0" + rStr;
    	String gutteredString2 = "0" + cStr;
    	
    	for ( int row = 1; row < memoTable.length; row++ ) {
    		for ( int col = 1; col < memoTable[0].length; col++ ) {
    			if ( gutteredString1.charAt(row) == gutteredString2.charAt(col) ) {
    				memoTable[row][col] = 1 + memoTable[row-1][col-1];
    			} else {
    				memoTable[row][col] = Math.max(memoTable[row-1][col], memoTable[row][col-1]);
    			}
    		}
    	}
    	return memoTable;
    }
    
    // -----------------------------------------------
    // Top-Down LCS
    // -----------------------------------------------
    
    /**
     * Top-down dynamic programming approach to the LCS problem, which
     * solves smaller and smaller subproblems recursively using a tabular
     * memoization structure.
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     *         [Side Effect] sets memoCheck to refer to table  
     */
    public static Set<String> topDownLCS (String rStr, String cStr) {
    	memoCheck = topDownTableFill(rStr, cStr, new int[rStr.length()+1][cStr.length()+1]);
    	return collectSolution( rStr, cStr, memoCheck);
    }
    
    // [!] TODO: Add any top-down specific helpers here!
    
    /**
     * Fills the memoization table using top-down dynamic programming
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return 2D array containing the memoized values
     */
    public static int[][] topDownTableFill( String s1, String s2, int[][] inputTable) {
    	int[][] memoTable = inputTable;
    	
    	if ( s1.length() == 0 || s2.length() == 0 ) {
    		memoTable[s1.length()][s2.length()] = 0;
    	} else if ( memoTable[s1.length()][s2.length()] != 0 ) {
    		//do nothing because this square has already been calculated
    	} else if ( lastChar(s1) == lastChar(s2) ) {
    		memoTable[s1.length()][s2.length()] = 1 + topDownTableFill( removeLastChar(s1), removeLastChar(s2), memoTable)[s1.length()-1][s2.length()-1];
    	} else {
    		memoTable[s1.length()][s2.length()] = Math.max( topDownTableFill( removeLastChar(s1), s2, memoTable)[s1.length()-1][s2.length()], topDownTableFill( s1, removeLastChar(s2), memoTable)[s1.length()][s2.length()-1]);
    	}
    	
    	return memoTable;
    }
    
}
