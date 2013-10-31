/*
 * Proprietary unpublished source code property of 
 * Simon Brooke <simon@journeyman.cc>.
 * 
 * Copyright (c) 2013 Simon Brooke <simon@journeyman.cc>
 */
package cc.journeyman.milkwood;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

/**
 * 
 * @author Simon Brooke <simon@journeyman.cc>
 */
class TextGenerator {

	/**
	 * The magic token which is deemed to end sentences.
	 */
	public static final String PERIOD = ".";

	/**
	 * The average number of sentences in a paragraph.
	 */
	public static final int AVSENTENCESPERPARA = 5;
	/**
	 * A random number generator.
	 */
	private static Random RANDOM = new Random();
	/**
	 * Dictionary of first-words we know about; each first-word maps onto a
	 * tuple of tuples of word sequences beginning with that word, so 'I' might
	 * map onto [[I, CAME, COMMA],[I, SAW, COMMA],[I CONQUERED COMMA]].
	 */
	TupleDictionary dictionary = new TupleDictionary();

	public TextGenerator() {
	}


	public void generate(OutputStream out, int tupleLength, RuleTreeNode root,
			int length) throws IOException {
		WordSequence tokens = this.compose(root, tupleLength, length);

		if (tokens.contains(PERIOD)) {
			// TODO: eq = equal?
			tokens = this.truncateAtLastInstance(tokens, PERIOD);
		}

		this.generate(out, tokens);
	}

	/**
	 * Write this sequence of tokens on this stream, sorting out minor issues of
	 * orthography.
	 * 
	 * @param out
	 *            the stream.
	 * @param tokens
	 *            the tokens.
	 * @throws IOException
	 *             if it is impossible to write (e.g. file system full).
	 */
	private void generate(OutputStream out, WordSequence tokens)
			throws IOException {
		BufferedWriter dickens = new BufferedWriter(new OutputStreamWriter(out));
		boolean capitaliseNext = true;

		try {
			for (String token : tokens) {
				capitaliseNext = writeToken(dickens, capitaliseNext, token);
			}
		} finally {
			dickens.flush();
			dickens.close();
		}
	}

	/**
	 * Deal with end of paragraph, capital after full stop, and other minor
	 * orthographic conventions.
	 * 
	 * @param dickens
	 *            the scrivenor who writes for us.
	 * @param capitalise
	 *            whether or not the token should be capitalised
	 * @param token
	 *            the token to write;
	 * @returnvtrue if the next token to be written should be capitalised.
	 * @throws IOException
	 */
	private boolean writeToken(BufferedWriter dickens, boolean capitalise,
			String token) throws IOException {
		if (this.spaceBefore(token)) {
			dickens.write(" ");
		}
		if (capitalise) {
			dickens.write(token.substring(0, 1)
					.toUpperCase(Locale.getDefault()));
			dickens.write(token.substring(1));
		} else {
			dickens.write(token);
		}

		this.maybeParagraph(token, dickens);

		return (token.endsWith(PERIOD));
	}

	/**
	 * Return false if token is punctuation, else true. Wouldn't it be nice if
	 * Java provided Character.isPunctuation(char)? However, since it doesn't, I
	 * can give this slightly special semantics: return true only if this is
	 * punctuation which would not normally be preceded with a space.
	 * 
	 * @param ch
	 *            a character.
	 * @return true if the should be preceded by a space, else false.
	 */
	private boolean spaceBefore(String token) {
		final boolean result;

		if (token.length() == 1) {
			switch (token.charAt(0)) {
			case '.':
			case ',':
			case ':':
			case ';':
			case 's':
				/*
				 * an 's' on its own is probably evidence of a possessive with
				 * the apostrophe lost
				 */
			case 't':
				/*
				 * similar; probably 'doesn't' or 'shouldn't' or other cases of
				 * 'not' with an elided 'o'.
				 */
				result = false;
				break;
			default:
				result = true;
				break;
			}
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * If this token is an end-of-sentence token, then, on one chance in some,
	 * have the writer write two new lines. NOTE: The tokeniser is treating
	 * PERIOD ('.') as a word character, even though it has not been told to.
	 * Token.endsWith( PERIOD) is a hack to get round this problem. TODO:
	 * investigate and fix.
	 * 
	 * @param token
	 *            a token
	 * @param dickens
	 *            our scrivenor
	 * @throws IOException
	 *             if Mr Dickens has run out of ink
	 */
	private void maybeParagraph(String token, BufferedWriter dickens)
			throws IOException {
		if (token.endsWith(PERIOD) && RANDOM.nextInt(AVSENTENCESPERPARA) == 0) {
			dickens.write("\n\n");
		}
	}

	/**
	 * Recursive, backtracking, output generator.
	 * 
	 * @param rules
	 * @param tupleLength
	 * @param length
	 * @return
	 */
	private WordSequence compose(RuleTreeNode rules, int tupleLength, int length) {
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
			System.err.println( String.format("No path %s: Backtracking...", glanceBack));
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

	/**
	 * 
	 * @param tokens
	 *            a sequence of tokens
	 * @param marker
	 *            a marker to terminate after the last occurrance of.
	 * @return a copy of tokens, truncated at the last occurrance of the marker.
	 */
	private WordSequence truncateAtLastInstance(WordSequence tokens,
			String marker) {
		final WordSequence result = new WordSequence();

		if (!tokens.isEmpty()) {

			String token = tokens.remove();
			result.add(token);
			if (!(marker.equals(token) && !tokens.contains(marker))) {
				/*
				 * woah, double negatives. If the token we're looking at is the
				 * marker, and the remainder of the tokens does not include the
				 * marker, we're done. Otherwise, we continue. OK?
				 */
				result.addAll(this.truncateAtLastInstance(tokens, marker));
			}
		}

		return result;
	}
}
