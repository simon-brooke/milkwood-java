/*
 * Proprietary unpublished source code property of 
 * Simon Brooke <simon@journeyman.cc>.
 * 
 * Copyright (c) 2013 Simon Brooke <simon@journeyman.cc>
 */
package cc.journeyman.milkwood;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * A tokeniser which reads tokens in a manner which suits me. Although this
 * implementation is based on a StreamTokenizer, the point of separating this
 * out into its own class is that if I had more time I could reimplement.
 *
 * @author simon
 *
 */
public class Tokeniser extends StreamTokenizer {

    /**
     * Initialise me appropriately wrapping this reader.
     * @param r the reader to wrap.
     */
    public Tokeniser(Reader r) {
        super(r);

        this.resetSyntax();
        this.whitespaceChars(8, 15);
        this.whitespaceChars(28, 32);
        /*
         * treat quotemarks as white space. Actually it would be better if quote
         * marks were white space only if preceded or followed by whitespace, so
         * that, e.g., 'don't' and 'can't' appeared as single tokens. But that
         * means really reimplementing the parser and I don't have time.
         */
        this.whitespaceChars((int) '\"', (int) '\"');
        this.whitespaceChars((int) '\'', (int) '\'');
        /*
         * treat underscore and hyphen as whitespace as well. Again, hyphen with
         * either leading or trailing non-whitespace probably ought to be
         * treated specially, but...
         */
        this.whitespaceChars((int) '_', (int) '_');
        this.whitespaceChars((int) '-', (int) '-');
        this.wordChars((int) '0', (int) '9');
        this.wordChars((int) 'A', (int) 'Z');
        this.wordChars((int) 'a', (int) 'z');
    }

    public Tokeniser(InputStream in) {
        this(new BufferedReader(new InputStreamReader(in)));
    }

    /**
     * There surely must be a better way to get just the token out of a
     * StreamTokenizer...!
     */
    public String readBareToken() {
        final String token;

        switch (this.ttype) {
            case StreamTokenizer.TT_EOL:
                token = "FIXME"; // TODO: fix this!
                break;
            case StreamTokenizer.TT_NUMBER:
                token = new Double(this.nval).toString();
                break;
            case StreamTokenizer.TT_WORD:
                token = this.sval.toLowerCase();
                break;
            default:
                StringBuilder bob = new StringBuilder();
                bob.append((char) this.ttype);
                token = bob.toString();
                break;
        }
        return token;
    }
}
