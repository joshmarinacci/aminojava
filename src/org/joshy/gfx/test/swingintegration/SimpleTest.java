package org.joshy.gfx.test.swingintegration;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.VFlexBox;

import javax.swing.*;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 15, 2010
 * Time: 8:09:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new SimpleTest());
    }

    @Override
    public void run() {
        Control box = new VFlexBox()
                .setBoxAlign(VFlexBox.Align.Stretch)
                .setFill(FlatColor.RED)
                .add(new Button("An Amino Button"))
                ;

        JComponentWrapper wrapper = new JComponentWrapper();
        wrapper.setContent(box);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(BorderLayout.NORTH, new JButton("A Swing Button"));
        panel.add(BorderLayout.CENTER, wrapper);


        
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setSize(400,400);
        frame.setVisible(true);
    }
}
