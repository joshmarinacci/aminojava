package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.GridBox;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 30, 2010
 * Time: 9:24:14 PM
 * To change this template use File | Settings | File Templates.
 */

//spy kitteh listenz to your secretz

public class GridTest implements Runnable {

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new GridTest());
    }
    
    public void run() {
        InputStream stream = MailApp.class.getResourceAsStream("gridtest.css");
        URL uri = MailApp.class.getResource("gridtest.css");
        try {
            Core.getShared().loadCSS(stream,uri);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        EventBus.getSystem().addListener(SystemMenuEvent.Quit,new Callback<Event>() {
            public void call(Event event) throws Exception {
                System.exit(0);
            }
        });

        Stage stage = Stage.createStage();

        Textbox tb1 = new Textbox("1024");
        tb1.addCSSClass("rgbbox");
        u.p("layout bounds = " + tb1.getLayoutBounds());
        GridBox widthHeight = new GridBox()
                .setPadding(0)
                .createColumn(70, GridBox.Align.Fill)
                .createColumn(100, GridBox.Align.Fill)
                .createColumn(100, GridBox.Align.Left)
                //.addControl(new Label("Width:"))
                .addControl(tb1)
                //.addControl(new Button("pixels"))
                .nextRow()
                //.addControl(new Label("Height:"))
                .addControl(new Textbox("768").addCSSClass("rgbbox"))
                //.addControl(new Button("pixels"))
                ;
        widthHeight.setFill(FlatColor.WHITE);
        widthHeight.debug(true);

        /*
        TitleBorderPanel title1 = new TitleBorderPanel(widthHeight);
        GridBox docSize = new GridBox()
                .createColumn(70, GridBox.Align.Right)
                .createColumn(100, GridBox.Align.Left)
                .createColumn(100, GridBox.Align.Left)
                .addControl(new Label("Width:"))
                .addControl(new Textbox("48"))
                .addControl(new Button("inches"))
                .nextRow()
                .addControl(new Label("Height:"))
                .addControl(new Textbox("32"))
                .addControl(new Button("inches"))
                .nextRow()
                .addControl(new Label("Resolution:"))
                .addControl(new Textbox("72"))
                .addControl(new Button("pixels/inch"))
                ;
        docSize.setFill(FlatColor.WHITE);
        TitleBorderPanel title2 = new TitleBorderPanel(docSize);
        title1.setHeight(130);
        title2.setHeight(165);


        VFlexBox box = new VFlexBox();
        box.add(new Button("OK"),new Button("Cancel"),new Button("Auto"));
        GridBox master = new GridBox()
                .createColumn(300, GridBox.Align.Fill)
                .createColumn(100, GridBox.Align.Left, GridBox.VAlign.Top)
                .addControl(title1)
                .addControl(box)
                .nextRow()
                .addControl(title2)
                .nextRow()
                .addControl(new Checkbox("Scale Styles"))
                .nextRow()
                .addControl(new Checkbox("Constrain Proportions"))
                .nextRow()
                .addControl(new Checkbox("Resample Image:"))
                ;
        master.debug(false);
        master.setFill(FlatColor.YELLOW);            
        stage.setContent(master);
        */
        stage.setContent(widthHeight);

    }

    private class TitleBorderPanel extends Panel {
        double inset = 20;
        public TitleBorderPanel(GridBox grid) {
            super();
            add(grid);
        }

        @Override
        public void doLayout() {
            for(Control c : controlChildren()) {
                c.setTranslateX(inset);
                c.setTranslateY(inset);
                c.setWidth(getWidth()-inset*2);
                c.setHeight(getHeight()-inset*2);
                c.doLayout();
            }
        }

        @Override
        protected void drawSelf(GFX g) {
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0,0,getWidth(),getHeight());
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0,0,getWidth()-1,getHeight()-1);
        }
    }
}
