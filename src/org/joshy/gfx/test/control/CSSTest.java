package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;

import java.io.File;

public class CSSTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new CSSTest());
        Core.setDebugCSS(new File("test.css"));
    }

    @Override
    public void run() {
        Stage stage = Stage.createStage();
        stage.setContent(new VFlexBox()
                .add(new Button("button1").setId("button1"))
                .add(new Button("button2").setId("button2"))
                .add(new Button("button3").setId("button3"))
        );
    }
}
