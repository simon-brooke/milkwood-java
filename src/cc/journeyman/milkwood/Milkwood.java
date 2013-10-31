package cc.journeyman.milkwood;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Proprietary unpublished source code property of 
 * Simon Brooke <simon@journeyman.cc>.
 * 
 * Copyright (c) 2013 Simon Brooke <simon@journeyman.cc>
 */

/**
 * 
 * @author Simon Brooke <simon@journeyman.cc>
 */
public class Milkwood {
	/**
	 * The magic token which is deemed to end sentences.
	 */
	public static final String PERIOD = ".";

	/**
	 * Parse command line arguments and kick off the process. Expected arguments
	 * include:
	 * <dl>
	 * <dt>-d, -debug</dt>
	 * <dd>Print debugging output to standard error</dd>
	 * <dt>-i, -input</dt>
	 * <dd>Input file, expected to be an English (or, frankly, other natural
	 * language) text. Defaults to standard in.</dd>
	 * <dt>-n, -tuple-length</dt>
	 * <dd>The length of tuples into which the file will be analised, default 2.
	 * </dd>
	 * <dt>-o, -output</dt>
	 * <dd>Output file, to which generated text will be written. Defaults to
	 * standard out.</dd>
	 * </dl>
	 * 
	 * @param args
	 *            the command line arguments
	 * @exception FileNotFoundException
	 *                if the user specifies a file which isn't available.
	 * @excpetion IOException if could not read from input or write to output.
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		/* defaults */
		InputStream in = System.in;
		OutputStream out = System.out;
		int tupleLength = 2;
		boolean debug = false;

		for (int cursor = 0; cursor < args.length; cursor++) {
			String arg = args[cursor];

			if (arg.startsWith("-") && arg.length() > 1) {
				switch (arg.charAt(1)) {
				case 'd':
					debug = true;
					break;
				case 'i':
					// input
					in = new FileInputStream(new File(args[++cursor]));
					break;
				case 'o': // output
					out = new FileOutputStream(new File(args[++cursor]));
					break;
				case 'n':
				case 't': // tuple length
					tupleLength = Integer.parseInt(args[++cursor]);
					break;
				default:
					throw new IllegalArgumentException(String.format(
							"Unrecognised argument '%s'", arg));
				}
			}
		}
		try {
			new Milkwood().readAndGenerate(in, out, tupleLength, debug);
		} finally {
			out.close();
		}
	}

	/**
	 * Read tokens from this input and use them to generate text on this output.
	 * 
	 * @param in
	 *            the input stream to read.
	 * @param out
	 *            the output stream to write to.
	 * @param tupleLength
	 *            the length of tuples to be used in generation.
	 * @param debug
	 *            whether to print debugging output.
	 * @throws IOException
	 *             if the file system buggers up, which is not, in the cosmic
	 *             scheme of things, very likely.
	 */
	void readAndGenerate(final InputStream in, final OutputStream out,
			final int tupleLength, boolean debug) throws IOException {
		/* The root of the rule tree I shall build. */
		RuleTreeNode root = new RuleTreeNode();
		int length = read(in, tupleLength, debug, root);

		WordSequence tokens = compose(tupleLength, debug, root, length);

		write(out, debug, tokens);
	}

	/**
	 * Digest the input into a set of rules.
	 * 
	 * @param in
	 *            the input stream.
	 * @param tupleLength
	 *            the length of tuples we shall consider.
	 * @param debug
	 *            whether or not to print debugging output.
	 * @param root
	 *            the root of the rule tree.
	 * @return the number of tokens read.
	 * @throws IOException
	 *             if the file system buggers up, which is not, in the cosmic
	 *             scheme of things, very likely.
	 */
	private int read(final InputStream in, final int tupleLength,
			boolean debug, RuleTreeNode root) throws IOException {
		int length = new Digester().read(in, tupleLength, root);

		if (debug) {
			System.err.println(root.toString());
		}
		return length;
	}

	private WordSequence compose(final int tupleLength, boolean debug,
			RuleTreeNode root, int length) {
		WordSequence tokens = new Composer(debug).compose(root, tupleLength,
				length);

		if (tokens.contains(PERIOD)) {
			tokens = tokens.truncateAtLastInstance(PERIOD);
		}
		return tokens;
	}

	/**
	 * Write this sequence of tokens to this output.
	 * 
	 * @param out
	 *            the stream to which to write.
	 * @param debug
	 *            whether or not to print debugging output.
	 * @param tokens
	 *            the sequence of tokens to write.
	 * @throws IOException
	 *             if the file system buggers up, which is not, in the cosmic
	 *             scheme of things, very likely.
	 */
	private void write(final OutputStream out, boolean debug,
			WordSequence tokens) throws IOException {
		Writer scrivenor = new Writer(out, debug);
		try {
			scrivenor.generate(tokens);
		} finally {
			scrivenor.close();
		}
	}

}
