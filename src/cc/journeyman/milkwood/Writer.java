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
import java.util.Locale;
import java.util.Random;

/**
 * 
 * @author Simon Brooke <simon@journeyman.cc>
 */
class Writer extends BufferedWriter {
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

	/**
	 * Whether or not I am in debugging mode.
	 */
	@SuppressWarnings("unused")
	private final boolean debug;

	/**
	 * @param out
	 *            the output stream to which I shall write.
	 * @param debug
	 *            Whether or not I am in debugging mode.
	 */
	public Writer(OutputStream out, final boolean debug) {
		super(new OutputStreamWriter(out));
		this.debug = debug;
	}

	/**
	 * Write this sequence of tokens on this stream, sorting out minor issues of
	 * orthography.
	 * 
	 * @param tokens
	 *            the tokens.
	 * @throws IOException
	 *             if it is impossible to write (e.g. file system full).
	 */
	public void generate(WordSequence tokens) throws IOException {
		boolean capitaliseNext = true;

		try {
			for (String token : tokens) {
				capitaliseNext = writeToken(capitaliseNext, token);
			}
		} finally {
			this.flush();
			this.close();
		}
	}

	/**
	 * Deal with end of paragraph, capital after full stop, and other minor
	 * orthographic conventions.
	 * 
	 * @param capitalise
	 *            whether or not the token should be capitalised
	 * @param token
	 *            the token to write;
	 * @returnvtrue if the next token to be written should be capitalised.
	 * @throws IOException
	 */
	private boolean writeToken(boolean capitalise, String token)
			throws IOException {
		if (this.spaceBefore(token)) {
			this.write(" ");
		}
		if (capitalise) {
			this.write(token.substring(0, 1).toUpperCase(Locale.getDefault()));
			this.write(token.substring(1));
		} else {
			this.write(token);
		}

		this.maybeParagraph(token);

		return (token.endsWith(Milkwood.PERIOD));
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
	 * @throws IOException
	 *             if Mr this has run out of ink
	 */
	private void maybeParagraph(String token) throws IOException {
		if (token.endsWith(Milkwood.PERIOD) && RANDOM.nextInt(AVSENTENCESPERPARA) == 0) {
			this.write("\n\n");
		}
	}

}
