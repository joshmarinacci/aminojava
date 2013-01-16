package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 4, 2010
 * Time: 12:46:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextAndLabelTests {
    @Before
    public void conf() throws Exception {
        Core.setTesting(true);
        Core.init();
        //Core.getShared().defer(new ListViewTests());
    }

    @Test
    public void labelTests() {
        String text;

        //just do a single word and make sure the line count is exactly 1
        Label label = new Label("shortwords");
        label.doSkins();
        label.doPrefLayout();
        label.doLayout();
        assertTrue(label._layout_model.lineCount() == 1);
        assertTrue(label._layout_model.line(0).letterCount() == 10);

        //do several words
        text = "several words to read";
        label.setText(text);
        label.doPrefLayout();
        label.doLayout();
        assertTrue(label._layout_model.lineCount() == 1);
        assertTrue(label._layout_model.line(0).letterCount() == text.length());

        //forced newlines
        text = "some text\nwith a newline";
        label.setText(text);
        label.doPrefLayout();
        label.doLayout();
        assertTrue(label._layout_model.lineCount() == 2);


        //layout wrapped lines
        text = "some text with a very long amount of text in it";
        label.setText(text);
        label.doPrefLayout();
        label.setWidth(100);
        label.doLayout();
        assertTrue(label._layout_model.lineCount() == 3);

        //wrapping at the word boundary
        text = "sometextwitha verylongamountoftextinit";
        label.setText(text);
        label.doPrefLayout();
        label.setWidth(100);
        label.doLayout();
        assertTrue(label._layout_model.lineCount() == 2);


        
    }

    @Test
    public void textboxTests() {
        String text;

        //just do a single word and make sure the line count is exactly 1
        Textbox box = new Textbox("shortwords");
        box.doSkins();
        box.doPrefLayout();
        box.setWidth(100);
        box.setHeight(100);
        box.doLayout();
        assertTrue(box._layout_model.lineCount() == 1);
        assertTrue(box._layout_model.line(0).letterCount() == 10);
        
        //check that multiple spaces are collapsed
        //select all, check that length of selection is correct
        //select all and delete, check that length of text is 0 and lines is now 1
        //move cursor to end, check that spot is right after last text node
        //move cursor to end of first line, in multiline. check spot
        //move cursor to start of second line, check spot
    }

    @Test
    public void textareaTests() {
        
    }


    @After
    public void tearDown() throws Exception {
    }

}
