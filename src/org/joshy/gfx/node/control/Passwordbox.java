package org.joshy.gfx.node.control;

/**
 * a subclass of Textbox that renders the text with bullets
 */
public class Passwordbox extends Textbox {
    private String passwordChar = "\u2022";

    @Override
    protected String filterText(String text) {
        String bullets = "";
        for(int i=0; i<text.length(); i++) {
            bullets += passwordChar;
        }
        return bullets;
    }

    /*@Override
    CursorPoint cursorCharToCursorPoint(int cursorCharX, String text) {
        return super.cursorCharToCursorPoint(cursorCharX, filterText(text));
    }*/
}
