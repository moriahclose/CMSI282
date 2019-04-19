package huffman;

import java.util.*;
import java.io.*;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
	    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    private Map<Character, String> encodingMap;
    
    private char NULL_CHAR = '\0';
    
    // ------------------------------------------------------------
    // HELPER METHODS TO DELETE
    // ------------------------------------------------------------
    public void printTrie(HuffNode root) {
    	if ( root.isLeaf() ) {
        	System.out.println("<" + root.character + "," + root.count + ">");
    	} else {
    		printTrie(root.left);
        	System.out.println("<" + root.character + "," + root.count + ">");
        	printTrie(root.right);
    	}
    }
    
    public HuffNode getRoot() {
    	return trieRoot;
    }
    
    public void printMap() {
    	for(Map.Entry<Character, String> m : encodingMap.entrySet()) {
    		System.out.println("[" + m.getKey() + "," + m.getValue() + "]");
    	}
    }
    
    public Map<Character, String> getEncodingMap() {
    	return encodingMap;
    }
        
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    Huffman (String corpus) {
        trieRoot = new HuffNode(NULL_CHAR, corpus.length());
        encodingMap = new HashMap<Character, String>();
        
        setTrie(corpus);
        setEncodingMap(getRoot(), "");
    }
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Creates HashMap of character frequencies
     * @param corpus String
     * @return Map with chars as keys and frequencies as values
     */
    public HashMap<Character, Integer> charFrequencies(String corpus) {
    	HashMap<Character, Integer> frequencies = new HashMap<>();
    	
    	for (int i = 0; i < corpus.length(); i++) {
    		 char currentChar = corpus.charAt(i);
    		 if ( frequencies.containsKey( currentChar ) ) {
    			 frequencies.replace(currentChar, frequencies.get(currentChar)+1 );
    		 } else {
    			 frequencies.put(currentChar, 1);
    		 }
    	}
    	return frequencies;
    }
    
    /**
     * Creates Huffman Trie and changes root of Huffman object
     * @param String corpus to construct trie from
     */
    public void setTrie(String corpus) {
    	HashMap<Character, Integer> frequencies = this.charFrequencies(corpus);
    	PriorityQueue<HuffNode> pq = new PriorityQueue<>();
    	
    	for ( Map.Entry<Character, Integer> frequency : frequencies.entrySet() ) {
    		pq.add( new HuffNode(frequency.getKey(), frequency.getValue()) );
    	}

    	while( pq.size() > 1 ) {
    		HuffNode childOne = pq.poll();
    		HuffNode childTwo = pq.poll();
    		HuffNode parent = new HuffNode(NULL_CHAR, childOne.count + childTwo.count);
    		parent.left = childOne;
    		parent.right = childTwo;
    		pq.add(parent);
    	}
    	
    	trieRoot = pq.poll();
    }
    
    /**
     * Sets encodingMap 
     * @param HuffNode subtree root to start recursion from
     */
    public void setEncodingMap(HuffNode root, String bitString) {
    	if( root.left.isLeaf() ) {
    		this.encodingMap.put(root.left.character, bitString + "0");
    	} else {
        	setEncodingMap(root.left, bitString + "0");
    	}
    	
    	if (root.right.isLeaf() ) {
    		this.encodingMap.put(root.right.character, bitString + "1");
    	} else {
        	setEncodingMap(root.right, bitString + "1");
    	}
    	    	
    }
    
    /**
     * Creates bit string of message using encoding map without padded 0s
     * @param corpus String to get bitString of
     * @return String representing bits needed to represent corpus
     */
    public String getBitString(String corpus) {
    	String bitString = "";
    	for ( int i = 0; i < corpus.length(); i++ ) {
    		bitString += encodingMap.get( corpus.charAt(i) );
    	}
    	return bitString;
    }
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as 3 components: (1) the
     *         first byte contains the number of characters in the message,
     *         (2) the bitstring containing the message itself, (3) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
    	ByteArrayOutputStream compressed = new ByteArrayOutputStream();
    	compressed.write(message.length());
    	
    	String bitString = getBitString(message);
    	while (bitString.length() >= 8) { 								 // add each byte to the byte array
        	int parsed = Integer.parseInt(bitString.substring(0, 8), 2); // get unsigned int representation of byte
    		compressed.write( (byte)parsed );							 // add signed byte representation to array
    		bitString = bitString.substring(8);
    	}
    	
       	int padLength = 8 - bitString.length();
       	int parsed = Integer.parseInt(bitString, 2);
    	
    	compressed.write( (byte)parsed<<padLength );
    	return compressed.toByteArray();
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Returns string of bits for compressed byte[]
     * @param byte[] compressed message
     * @return String representation of bytes in array
     */
    public String compressedString( byte[] compressed ) {
    	String compressedStr = "";
    	
    	for ( int i = 1; i < compressed.length; i++) {
    		String addOn = "";
    		if (compressed[i] < 0) {
    			String twosComplement = Integer.toBinaryString(compressed[i]);
    			addOn = twosComplement.substring( twosComplement.length() - 8, twosComplement.length());
    		} else {
    			addOn = Integer.toString(compressed[i], 2);
    		}
    		
    		while (addOn.length() < 8) {
    			addOn = "0" + addOn; 
    		}
    		compressedStr += addOn;
    	}
    	
    	return compressedStr;
    }
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as 3 components: (1) the
     *        first byte contains the number of characters in the message,
     *        (2) the bitstring containing the message itself, (3) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
    	String decompressed = "";
    	String format = "";
    	int numChars = compressedMsg[0];

    	for (int i = 0; i < numChars; i++ ) {
    		format += "%s";
    	}
    	
    	
    	System.out.println("Has " + numChars + " chars");
    	System.out.println(compressedString(compressedMsg));
    	return decompressed;
    }
    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents (in the case of a leaf, otherwise
     * the null character \0), and a count field that holds the number of times
     * the node's character (or those in its subtrees) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode left, right;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return left == null && right == null;
        }
        
        public int compareTo (HuffNode other) {
            return this.count - other.count;
        }
        
    }

}
