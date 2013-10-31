package cc.journeyman.milkwood;

import java.util.Collection;
import java.util.Stack;

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
	 * @param rules
	 * @param tupleLength
	 * @param length
	 * @return
	 */
	public WordSequence compose(RuleTreeNode rules, int tupleLength, int length) {
		Stack<String> preamble = composePreamble(rules);
		WordSequence result = new WordSequence();

		// composing the preamble will have ended with *ROOT* on top of the
		// stack;
		// get rid of it.
		preamble.pop();

		result.addAll(preamble);

		result.addAll(this.compose(preamble, rules, rules, tupleLength, length));
		return result;
	}

	/**
	 * Recursively attempt to find sequences in the ruleset to append to what's
	 * been composed so far.
	 * 
	 * @param glanceBack
	 * @param allRules
	 * @param currentRules
	 * @param tupleLength
	 * @param length
	 * @return
	 */
	private WordSequence compose(Stack<String> glanceBack,
			RuleTreeNode allRules, RuleTreeNode currentRules, int tupleLength,
			int length) {
		assert (glanceBack.size() == tupleLength) : "Shouldn't happen: bad tuple size";
		assert (allRules.getWord() == RuleTreeNode.ROOTMAGICTOKEN) : "Shoudn't happen: bad rule set";
		WordSequence result;

		try {
			@SuppressWarnings("unchecked")
			String here = currentRules.getWord((Stack<String>) glanceBack
					.clone());
			System.err.println(String.format("Trying token %s", here));

			result = new WordSequence();
			result.add(here);

			if (length != 0) {
				/* we're not done yet */
				Collection<String> options = allRules.getSuccessors();

				for (String next : options) {
					@SuppressWarnings("unchecked")
					WordSequence rest = this
							.tryOption((Stack<String>) glanceBack.clone(),
									allRules, currentRules.getRule(next),
									tupleLength, length - 1);

					if (rest != null) {
						/* we have a solution */
						result.addAll(rest);
						break;
					}
				}
			}
		} catch (NoSuchPathException ex) {
			if (debug) {
				System.err.println(String.format("No path %s: Backtracking...",
						glanceBack));
			}
			result = null;
		}

		return result;
	}

	/**
	 * Try composing with this ruleset
	 * 
	 * @param glanceBack
	 * @param allRules
	 *            all the rules there are.
	 * @param currentRules
	 *            the current node in the rule tree.
	 * @param tupleLength
	 *            the size of the glanceback window we're considering.
	 * @param length
	 * @return
	 */
	private WordSequence tryOption(Stack<String> glanceBack,
			RuleTreeNode allRules, RuleTreeNode currentRules, int tupleLength,
			int length) {
		final Stack<String> restack = this.restack(glanceBack,
				currentRules.getWord());
		restack.pop();
		return this.compose(restack, allRules, currentRules, tupleLength,
				length);
	}

	/**
	 * Return a new stack comprising all the items on the current stack, with
	 * this new string added at the bottom
	 * 
	 * @param stack
	 *            the stack to restack.
	 * @param bottom
	 *            the item to place on the bottom.
	 * @return the restacked stack.
	 */
	private Stack<String> restack(Stack<String> stack, String bottom) {
		final Stack<String> result;
		if (stack.isEmpty()) {
			result = new Stack<String>();
			result.push(bottom);
		} else {
			String top = stack.pop();
			result = restack(stack, bottom);
			result.push(top);
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
	private Stack<String> composePreamble(RuleTreeNode rules) {
		final Stack<String> result;
		final RuleTreeNode successor = rules.getRule();

		if (successor == null) {
			result = new Stack<String>();
		} else {
			result = this.composePreamble(successor);
			result.push(rules.getWord());
		}
		return result;
	}

}
