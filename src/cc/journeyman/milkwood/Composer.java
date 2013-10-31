package cc.journeyman.milkwood;

import java.util.Collection;
import java.util.Collections;

/**
 * Composes text output based on a rule tree.
 * 
 * @author simon
 * 
 */
public class Composer {
	/**
	 * Whether or not I am in debugging mode.
	 */
	private final boolean debug;

	/**
	 * 
	 * @param debug
	 *            Whether or not I am in debugging mode.
	 */
	public Composer(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Recursive, backtracking, output generator.
	 * 
	 * @param rules the rule set we're working to.
	 * @param length the number of tokens still to be output.
	 * @return if a successful path forward is found, that path, else null.
	 */
	public WordSequence compose(RuleTreeNode rules, int length) {
		WordStack preamble = composePreamble(rules);
		WordSequence result = new WordSequence();

		// composing the preamble will have ended with *ROOT* on top of the
		// stack;
		// get rid of it.
		preamble.pop();
		
		if (debug) {
			System.err.println( "Preamble: " + preamble);
		}

		result.addAll(preamble);
		
		WordStack body = this.compose(preamble, rules, length);
		Collections.reverse(body);
		result.addAll(body);
		
		return result;
	}

	/**
	 * Recursively attempt to find sequences in the ruleset to append to what's
	 * been composed so far.
	 * 
	 * @param glanceBack the last few words output.
	 * @param rules the rule set we're working to.
	 * @param length the number of tokens still to be output.
	 * @return if a successful path forward is found, that path, else null.
	 */
	private WordStack compose(WordStack glanceBack, RuleTreeNode rules,
			int length) {
		final WordStack result;
		
		if ( debug) {
			System.err.println( String.format( "%d: %s", length, glanceBack));
		}

		/* are we there yet? */
		if (length == 0) {
			result = new WordStack(); 
		} else {
			/*
			 * are there any rules in this ruleset which matches the current
			 * sliding window? if so, then recurse; if not, then fail.
			 */
			Collection<String> words = rules.match(glanceBack.duplicate());

			if (words.isEmpty()) {
				/* backtrack */
				result = null;
			} else {
				result = tryOptions(words, glanceBack, rules, length);
			}
		}
		return result;
	}
	
	/**
	 * Try each of these candidates in turn, attempting to recurse.
	 * @param candidates words which could potentially be added to the output.
	 * @param glanceBack the last few words output.
	 * @param allRules the rule set we're working to.
	 * @param length the number of tokens still to be output.
	 * @return if a successful path forward is found, that path, else null.
	 */
	private WordStack tryOptions(Collection<String> candidates,
			WordStack glanceBack, RuleTreeNode allRules, int length) {
		WordStack result = null;
		
		for ( String candidate : candidates) {
			result = compose( new WordStack(glanceBack, candidate), allRules, length - 1);
			if ( result != null) {
				/* by Jove, I think she's got it! */
				result.push(candidate);
				break;
			}
		}
		
		return result;
	}


	/**
	 * Random walk of the rule tree to extract (from the root) a legal sequence
	 * of words the length of our tuple.
	 * 
	 * @param rules
	 *            the rule tree (fragment) to walk.
	 * @return a sequence of words.
	 */
	private WordStack composePreamble(RuleTreeNode rules) {
		final WordStack result;
		final RuleTreeNode successor = rules.getRule();

		if (successor == null) {
			result = new WordStack();
		} else {
			result = this.composePreamble(successor);
			result.push(rules.getWord());
		}
		return result;
	}

}
