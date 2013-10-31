/*
 * Proprietary unpublished source code property of 
 * Simon Brooke <simon@journeyman.cc>.
 * 
 * Copyright (c) 2013 Simon Brooke <simon@journeyman.cc>
 */
package cc.journeyman.milkwood;

import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Read an input stream of text and digest it into a set of generation rules.
 * Separated out of TextGenerator mainly to declutter tht class.
 *
 * @author simon
 *
 */
public class Digester {

    /**
     * Read tokens from the input stream, and compile them into the rule tree
     * below this root.
     *
     * @param in the input stream from which I read.
     * @param tupleLength the length of the tuples I read.
     * @param root the ruleset to which I shall add.
     * @return the number of tokens read.
     * @throws IOException if can't read from file system.
     */
    protected int digest(final InputStream in, final int tupleLength,
            final RuleTreeNode root) throws IOException {
        int result = 0;
        final Queue<WordSequence> openTuples = new LinkedList<>();
        final Tokeniser tok = new Tokeniser(in);

        for (int type = tok.nextToken(); type != StreamTokenizer.TT_EOF; type = tok
                .nextToken()) {
            result++;
            final WordSequence newTuple = new WordSequence();
            String token = tok.readBareToken();

            openTuples.add(newTuple);
            for (WordSequence tuple : openTuples) {
                tuple.add(token);
            }

            if (openTuples.size() > tupleLength) {
                root.addSequence(openTuples.remove());
            }
        }

        return result;
    }
}
