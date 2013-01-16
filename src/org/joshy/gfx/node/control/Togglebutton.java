package org.joshy.gfx.node.control;

 /**
  * A basic checkbox. It's a selectable button with custom styling.
  */
public class Togglebutton extends Button {
    public Togglebutton(String text) {
        super(text);
        selectable = true;
    }

}
