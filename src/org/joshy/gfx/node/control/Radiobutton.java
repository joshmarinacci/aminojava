package org.joshy.gfx.node.control;

/**
 * A basic radio button. It's a selectable button with custom styling.
 */
public class Radiobutton extends Button {

    public Radiobutton(String text) {
        super(text);
        selectable = true;
    }
}
