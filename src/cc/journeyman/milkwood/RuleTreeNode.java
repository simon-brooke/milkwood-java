/*
 * Proprietary unpublished source code property of 
 * Simon Brooke <simon@journeyman.cc>.
 * 
 * Copyright (c) 2013 Simon Brooke <simon@journeyman.cc>
 */
package cc.journeyman.milkwood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/**
 * Mapping a word to its successor words. This is probably highly 
 * inefficient of store, but for the present purposes my withers are unwrung. 
 * Not thread safe in this form because of access to the random number generator.
 * 
 * @author Simon Brooke <simon@journeyman.cc>
 */
public class RuleTreeNode {
    /**
     * The line separator on this platform.
     */
    public static final String NEWLINE = System.getProperty("line.separator", "\n");
    /**
     * A random number generator.
     */
    private static Random RANDOM = new Random();
    
    /**
     * The word at this node.
     */
    private final String word;

    /**
     * Potential successors of this node
     */
    private Map<String,RuleTreeNode> rules = new HashMap<String,RuleTreeNode>();

    /**
     * Create me wrapping this word.
     * @param word the word I represent.
     */
    public RuleTreeNode(String word) {
        this.word = word;
    }
    
    
    public String toString() {
    	StringBuffer buffy = new StringBuffer();
    	
    	this.printToBuffer( buffy, 0);
    	
    	
    	return buffy.toString();
    }
    
    
    private void printToBuffer(StringBuffer buffy, int indent) {
		for (int i = 0; i < indent; i++) {
			buffy.append( '\t');
		}
		buffy.append( this.getWord());
		
		
		if ( this.rules.isEmpty()) {
			buffy.append(NEWLINE);
		} else {
			buffy.append( " ==>").append(NEWLINE);
			for ( String successor : this.getSuccessors()) {
				rules.get(successor).printToBuffer(buffy, indent + 1);
			}
			buffy.append(NEWLINE);
		}
	}


	/**
     * 
     * @return my word.
     */
    public String getWord() {
        return word;
    }

    /**
     * 
     * @return a shuffled list of the words which could follow this one.
     */
    public Collection<String> getSuccessors() {
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(rules.keySet());
        Collections.shuffle(result, RANDOM);
        return result;
    }
    
    
    /**
     * Compile this sequence of tokens into rule nodes under me.
     * @param sequence the sequence of tokens to compile.
     */
    public void addSequence(Queue<String> sequence) {
        if (!sequence.isEmpty()) {
            String word = sequence.remove();
            RuleTreeNode successor = this.getRule(word);
            if (successor == null) {
                successor = new RuleTreeNode(word);
                this.rules.put(word, successor);
            }
            
            successor.addSequence(sequence);
        }
    }
        
    /** 
     * Choose a successor at random.
     * 
     * @return the successor chosen, or null if I have none.
     */
	protected RuleTreeNode getRule() {
		RuleTreeNode result = null;

		if (!rules.isEmpty()) {
			int target = RANDOM.nextInt(rules.keySet().size());

			for (String key : rules.keySet()) {
				/*
				 * NOTE: decrement after test.
				 */
				if (target-- == 0) {
					result = rules.get(key);
				}
			}
		}

		return result;
    }
    
    /**
     * 
     * @param token a token to seek.
     * @return the successor among my successors which has this token, if any.
     */
    protected RuleTreeNode getRule(String token) {
        return rules.get(token);
    }

    protected String getWord(Stack<String> path) throws NoSuchPathException {
        final String result;
        
        if ( path.isEmpty()) {
            result = this.getWord();
        } else {
            final RuleTreeNode successor = this.getRule(path.pop());
            
            if (successor == null) {
                throw new NoSuchPathException();
            } else {
                result = successor.getWord(path);
            }
        }
        
        return result;
    }
}
