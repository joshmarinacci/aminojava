package org.joshy.gfx.node.control;

/**
 * A basic checkbox. It's a selectable button with custom styling.
 */
public class Checkbox extends Button {

    public Checkbox() {
        selectable = true;
    }

    public Checkbox(CharSequence s) {
        this();
        setText(s);
    }
}
