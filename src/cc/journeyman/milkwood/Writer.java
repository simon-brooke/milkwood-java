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
 * A special purpose writer to write sequences of tokens, chopping them up into
 * paragraphs on the fly..
 *
 * @author Simon Brooke <simon@journeyman.cc>
 */
class Writer extends BufferedWriter {

    /**
     * Line separator on this platform.
     */
    public static final String NEWLINE = System.getProperty("line.separator");
    /**
     * The average number of sentences in a paragraph.
     */
    public static final int AVSENTENCESPERPARA = 5;
    /**
     * A random number generator.
     */
    private static Random RANDOM = new Random();
    /**
     * Whether or not I am in debugging mode.
     */
    @SuppressWarnings("unused")
    private final boolean debug;

    /**
     * @param out the output stream to which I shall write.
     * @param debug Whether or not I am in debugging mode.
     */
    public Writer(OutputStream out, final boolean debug) {
        super(new OutputStreamWriter(out));
        this.debug = debug;
    }

    /**
     * Write this sequence of tokens on this stream, sorting out minor issues of
     * orthography.
     *
     * @param tokens the tokens.
     * @throws IOException if it is impossible to write (e.g. file system full).
     */
    public void writeSequence(WordSequence tokens) throws IOException {
        boolean capitaliseNext = true;

        for (String token : tokens) {
            capitaliseNext = writeToken(capitaliseNext, token);
        }
        this.write(NEWLINE);
    }

    /**
     * Deal with end of paragraph, capital after full stop, and other minor
     * orthographic conventions.
     *
     * @param capitalise whether or not the token should be capitalised
     * @param token the token to write;
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

        return (endOfSentence(token));
    }

    /**
     * Return false if token is punctuation, else true. Wouldn't it be nice if
     * Java provided Character.isPunctuation(char)? However, since it doesn't, I
     * can give this slightly special semantics: return true only if this is
     * punctuation which would not normally be preceded with a space.
     *
     * @param ch a character.
     * @return true if the should be preceded by a space, else false.
     */
    private boolean spaceBefore(String token) {
        final boolean result;

        switch (token.length()) {
            case 0:
                result = false;
                break;
            case 1:
                switch (token.charAt(0)) {
                    case '.':
                    case ',':
                    case ':':
                    case ';':
                    case '!':
                    case '?':
                    case ')':
                    case ']':
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
                break;
            default:
                result = true;
        }

        return result;
    }

    /**
     * If this token is an end-of-sentence token, then, on one chance in some,
     * have the writer write two new lines.
     *
     * @param token a token
     * @throws IOException if Mr this has run out of ink
     */
    private void maybeParagraph(String token) throws IOException {
        if (this.endOfSentence(token)
                && RANDOM.nextInt(AVSENTENCESPERPARA) == 0) {
            this.write(NEWLINE);
            this.write(NEWLINE);
        }
    }

    /**
     * Does this token mark the end of a sentence? NOTE: The tokeniser is
     * treating PERIOD ('.') as a word character, even though it has not been
     * told to. Token.endsWith( PERIOD) is a hack to get round this problem.
     * TODO: investigate and fix.
     *
     * @param token a token.
     * @return True if in conventional orthography this token should mark the
     * end of a sentence, else false.
     */
    private boolean endOfSentence(String token) {
        return token.endsWith(Milkwood.PERIOD)
                || token.equals("?")
                || token.endsWith("!");
    }
}
