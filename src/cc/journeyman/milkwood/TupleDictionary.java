/*
 * Proprietary unpublished source code property of 
 * Simon Brooke <simon@journeyman.cc>.
 * 
 * Copyright (c) 2013 Simon Brooke <simon@journeyman.cc>
 */
package cc.journeyman.milkwood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Simon Brooke <simon@journeyman.cc>
 */
public class TupleDictionary extends HashMap<String, Collection<WordSequence>> {

	private static final long serialVersionUID = 1L;

	/**
     * Specialisation: if there isn't an existing entry, create one.
     *
     * @param token the token to look up
     * @return the collection of possible tuples for that token.
     */
    public Collection<WordSequence> get(String token) {
        Collection<WordSequence> result = super.get(token);

        if (result == null) {
            result = new ArrayList<WordSequence>();
            this.put(token, result);
        }

        return result;
    }

    /**
     * Add a new, empty sequence to my entry for this token.
     * @param token the token
     * @return the new sequence which was added.
     */
    protected WordSequence addSequence(String token) {
        return this.addSequence(token, new WordSequence());
    }

    /**
     * Add this sequence to my entry for this token.
     * @param token the token.
     * @param sequence the sequence to add. Must not be null!
     * @return the sequence which was added.
     */
    protected WordSequence addSequence(String token, WordSequence sequence) {
        assert (sequence != null) : "invalid sequence argument";

        this.get(token).add(sequence);

        return sequence;
    }
}
