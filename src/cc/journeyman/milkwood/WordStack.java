package cc.journeyman.milkwood;

import java.util.Stack;

/**
 * Sliding window which rules may match.
 * 
 * @author simon
 * 
 */
public class WordStack extends Stack<String> {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new, empty, wordstack.
	 */
	public WordStack() {
		super();
	}
	
	/**
	 * create a new window from this window, having this new word as its
	 * terminal and ommitting the current first word. That is, the new window
	 * should be as long as the old, with each word shuffled up one place.
	 * 
	 * @param prototype the window to copy from.
	 * @param terminal the new terminal word.
	 */
	public WordStack(WordStack prototype, String terminal) {
		this();

		WordStack copy = prototype.duplicate();
		copy.pop();
		this.populate( copy, terminal);
	}

	private void populate(WordStack copy, String terminal) {
		if ( copy.isEmpty()) {
			this.push(terminal);
		} else {
			String token = copy.pop();
			this.populate(copy, terminal);
			this.push( token);
		}
	}

	/**
	 * A wrapper round clone which hides all the ugly casting.
	 * 
	 * @return a duplicate copy of myself.
	 */
	public WordStack duplicate() {
		return (WordStack) this.clone();
	}

}
