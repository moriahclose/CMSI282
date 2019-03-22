package lcs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    
    // [!] TODO: Add your shared helper methods here!
    

    // -----------------------------------------------
    // Bottom-Up LCS
    // -----------------------------------------------
    
    /**
     * Bottom-up dynamic programming approach to the LCS problem, which
     * solves larger and larger subproblems iterative using a tabular
     * memoization structure.
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     *         [Side Effect] sets memoCheck to refer to table
     */
    public static Set<String> bottomUpLCS (String rStr, String cStr) {
//        throw new UnsupportedOperationException();
    	memoCheck = bottomUpTableFill(rStr, cStr);
    	return new HashSet<>(Arrays.asList(
                "A"
            ));
    }
    
    // [!] TODO: Add any bottom-up specific helpers here!
    public static int[][] bottomUpTableFill( String s1, String s2 ) {
    	int[][] memoTable = new int[s1.length()+1][s2.length()+1];
    	String gutteredString1 = "0" + s1;
    	String gutteredString2 = "0" + s2;
    	
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
    
    
    public static void print2DArray( int[][] a ) {
    	for ( int i = 0; i < a.length; i++) {
    		for  (int j = 0; j < a[0].length; j++ ) {
    			System.out.print(a[i][j] + " ");
    		}
    		System.out.println();
    	}
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
//        throw new UnsupportedOperationException();
    	memoCheck = topDownTableFill(rStr, cStr, new int[rStr.length()+1][cStr.length()+1]);
    	return new HashSet<>(Arrays.asList(
                "A"
            ));
    }
    
    // [!] TODO: Add any top-down specific helpers here!
    
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
    
    public static char lastChar(String s) {
    	return s.charAt(s.length()-1);
    }
    
    public static String removeLastChar(String s) {
    	return s.substring(0, s.length()-1);
    }
    
    public static void main(String args[]) {
//    	LCS l = new LCS();
//    	l.topDownTableFill("ABA", "BAA", new int[4][4]);
    }
    
}
