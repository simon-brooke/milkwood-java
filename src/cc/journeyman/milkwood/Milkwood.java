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
     * Parse command line arguments and kick off the process. Expected 
     * arguments include:
     * <dl>
     * <dt>-i, -input</dt>
     * <dd>Input file, expected to be an English (or, frankly, other natural
     * language) text. Defaults to standard in.</dd>
     * <dt>-n, -tuple-length</dt>
     * <dd>The length of tuples into which the file will be analised, default 2.</dd>
     * <dt>-o, -output</dt>
     * <dd>Output file, to which generated text will be written. 
     * Defaults to standard out.</dd>
     * </dl>
     * 
     * @param args the command line arguments
     * @exception FileNotFoundException if the user specifies a file which 
     * isn't available.
     * @excpetion IOException if could not read from input or write to output.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        InputStream in = System.in;
        OutputStream out = System.out;
        int tupleLength = 2;
        
        for (int cursor = 0; cursor < args.length; cursor++) {
            String arg = args[cursor];

            if (arg.startsWith("-") && arg.length() > 1) {
                switch (arg.charAt(1)) {
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
                        throw new IllegalArgumentException(
                                String.format("Unrecognised argument '%s'", arg));
                }
            }
        }

        new TextGenerator().readAndGenerate( in, out, tupleLength);
    }
}
