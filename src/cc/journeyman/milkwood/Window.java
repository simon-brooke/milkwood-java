package cc.journeyman.milkwood;

import java.util.Stack;

/**
 * Sliding window which rules may match.
 *
 * @author simon
 *
 */
public class Window extends Stack<String> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new, empty, wordstack.
     */
    public Window() {
        super();
    }

    /**
     * create a new window from this window, having this new word as its
     * terminal and ommitting the current first word. That is, the new window
     * should be as long as the old, with each word shuffled up one place.
     *
     * @param prototype the window to copy from.
     * @param terminal the new terminal word.
     */
    public Window(Window prototype, String terminal) {
        this();

        Window copy = prototype.duplicate();
        copy.pop();
        this.populate(copy, terminal);
    }

    private void populate(Window copy, String terminal) {
        if (copy.isEmpty()) {
            this.push(terminal);
        } else {
            String token = copy.pop();
            this.populate(copy, terminal);
            this.push(token);
        }
    }

    /**
     * A wrapper round clone which hides all the ugly casting.
     *
     * @return a duplicate copy of myself.
     */
    public Window duplicate() {
        return (Window) this.clone();
    }
}
