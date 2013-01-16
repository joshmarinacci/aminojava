package org.joshy.gfx.node.control;

/**
 * An interface which indicates that a control can receive the focus.
 * Any control implementing Focusable should implement 'isFocused' to
 * check with the focus manager if it is currently the focus.
 */
public interface Focusable {
    public boolean isFocused();
}
