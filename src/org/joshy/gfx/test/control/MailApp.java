package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.*;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.ArrayListModel;
import org.joshy.gfx.util.u;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 4, 2010
 * Time: 9:10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailApp implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        //Core.setDebugCSS();
        Core.getShared().defer(new MailApp());
    }

    @Override

    public void run() {
        InputStream stream = MailApp.class.getResourceAsStream("mailapp/test.css");
        URL uri = MailApp.class.getResource("mailapp/test.css");
        u.p("uri = " + uri);
        try {
            Core.getShared().loadCSS(stream,uri);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Control content = null;
        content = buildGUI();
        //content = test2();
        Stage stage = Stage.createStage();
        stage.setContent(content);
        stage.setWidth(850);
        stage.setHeight(600);

    }

    public static FlexBox buildGUI() {

        ArrayListModel<String> mailboxModel = new ArrayListModel<String>();
        mailboxModel.add("Inbox");
        mailboxModel.add("Drafts");
        mailboxModel.add("Sent");
        mailboxModel.add("Trash");
        mailboxModel.add("Friends");
        mailboxModel.add("Work");
        mailboxModel.add("Mailing Lists");


        FlexBox content = new VFlexBox()
            .setBoxAlign(VFlexBox.Align.Stretch)
            .add(
                    new HFlexBox()
                        .setBoxAlign(VFlexBox.Align.Top)
                        .add(new Button("").setId("get_mail"))
                        .add(new Spacer(), 1)
                        .add(new Button("").setId("delete"))
                        .add(new Button("").setId("junk"))
                        .add(new Spacer(), 1)
                        .add(new Button("").setId("reply"))
                        .add(new Button("").setId("reply_all"))
                        .add(new Button("").setId("forward"))
                        .add(new Spacer(), 1)
                        .add(new Button("").setId("new_mail"))
                        .add(new Spacer(), 1)
                        .add(new Button("").setId("note"))
                        .add(new Button("").setId("todo"))
                        .add(new Spacer(),1)
                        .add(new Textbox("Search").addCSSClass("searchbox").setPrefWidth(150))
                        .setId("toolbar")
                 , 0)
            .add(new HFlexBox()
                .setBoxAlign(HFlexBox.Align.Stretch)
                .add(new VFlexBox()
                        .setBoxAlign(FlexBox.Align.Stretch)
                        .add(new ScrollPane(new ListView().setModel(mailboxModel))
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                .setVerticalVisiblePolicy(ScrollPane.VisiblePolicy.WhenNeeded)
                                ,1)
                        .add(new HFlexBox()
                            .setBoxAlign(HFlexBox.Align.Stretch)
                            .add(new Button("").setId("plus"))
                            .add(new Button("").setId("activity"))
                            .add(new Button("").setId("action")),0)
                        .setPrefWidth(200)
                    ,0)

                .add(new SplitPane(true)
                    .setFirst(new ScrollPane(new TableView())
                            .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never))
                    .setSecond(new Panel()
                        .setFill(FlatColor.BLUE)
                        .setWidth(100)
                        .setHeight(100))
                    .setPosition(200)
                ,1)
            ,1)
                ;
        return content;
    }

    private FlexBox test2() {
    FlexBox content = new VFlexBox()
        .setBoxAlign(VFlexBox.Align.Stretch)
        .add(new Button("asdf"))
        .add(new VFlexBox()
            //.add(new Button("asdf"))
            .setBoxAlign(VFlexBox.Align.Stretch)

                .add(new ListView().setWidth(200),1)
                //.add(new Button().setWidth(200),1)
            /*.add(new HFlexBox()
                .setBoxAlign(HFlexBox.Align.Stretch)
                .add(new Button("+"))
                .add(new Button("^"))
                .add(new Button("*")),0)*/
        ,1);
        return content;
    }

}
