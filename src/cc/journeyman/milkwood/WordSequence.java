/*
 * Proprietary unpublished source code property of 
 * Simon Brooke <simon@journeyman.cc>.
 * 
 * Copyright (c) 2013 Simon Brooke <simon@journeyman.cc>
 */
package cc.journeyman.milkwood;

import java.util.LinkedList;
import java.util.Queue;

/**
 * An ordered sequence of words. Of course it implements Queue since it is a
 * LinkedList and LinkedList implements Queue, but I want to make it explicitly
 * clear that this is a queue and can be used as such.
 * 
 * @author Simon Brooke <simon@journeyman.cc>
 */
class WordSequence extends LinkedList<String> implements Queue<String> {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param tokens
	 *            a sequence of tokens
	 * @param marker
	 *            a marker to terminate after the last occurrance of.
	 * @return a copy of tokens, truncated at the last occurrance of the marker.
	 */
	public WordSequence truncateAtLastInstance(String marker) {
		final WordSequence result = new WordSequence();

		for (String token : this) {
			if (token.endsWith(marker) && !this.contains(marker)) {
				/*
				 * If the token we're looking at ends with the marker, and the
				 * remainder of the tokens does not include a token ending with
				 * the marker, we're done. Otherwise, we continue. OK?
				 */
				break;
			}
			result.add(token);
		}

		return result;
	}

	/**
	 * Specialisation: Working around the bug that the tokeniser treats PERIOD as a word character.
	 */
	@Override
	public boolean contains(Object target) {
		boolean result = false;
		if (target != null) {
			String marker = target.toString();

			for (String token : this) {
				if (token.endsWith(marker)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
