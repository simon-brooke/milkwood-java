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
 * @author Simon Brooke <simon@journeyman.cc>
 */
class WordSequence extends LinkedList<String> implements Queue<String> {

	private static final long serialVersionUID = 1L;
    
}
