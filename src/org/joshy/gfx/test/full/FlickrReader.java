package org.joshy.gfx.test.full;

import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import org.joshy.gfx.Core;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.FlexBox;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.ArrayListModel;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.xml.XMLRequest;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Sep 28, 2010
 * Time: 3:17:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlickrReader implements Runnable {

    public static class Photo {
        String title;
        String url;
    }

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new FlickrReader());
    }

    //create an event handler to invoke the search
    private Callback<ActionEvent> searchHandler = new Callback<ActionEvent>() {
        public void call(ActionEvent event) {
            try {
                System.out.println("searching: " + searchBox.getText());
                XMLRequest req = new XMLRequest();
                req.setURL("http://api.flickr.com/services/feeds/photos_public.gne");
                req.setParameter("tags","lolcat");
                req.onComplete(resultsHandler);
                req.start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Callback<Doc> resultsHandler = new Callback<Doc>() {
        public void call(Doc doc) {
            try {
                doc.dump();
                resultsModel.clear();
                for(Elem entry : doc.xpath("feed/entry")) {
                    Photo photo = new Photo();
                    photo.title = entry.xpathString("title/text()");
                    photo.url = entry.xpathString("link/@href");
                    resultsModel.add(photo);
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
    };

    private Callback<ActionEvent> openHandler = new Callback<ActionEvent>() {
        public void call(ActionEvent event) {
            int n = photoList.getSelectedIndex();
            Photo photo = photoList.getModel().get(n);
            OSUtil.openBrowser(photo.url);
        }
    };

    private ListView.TextRenderer<Photo> photoFormatter = new ListView.TextRenderer<Photo>(){
        public String toString(SelectableControl view, Photo item, int index) {
            if(item == null) return "";
            return "Photo: "+item.title;
        }
    };


    private ArrayListModel<Photo> resultsModel = new ArrayListModel<Photo>();
    private Textbox searchBox;
    private ListView<Photo> photoList;


    public void run() {
        //create the search box
        photoList = new ListView<Photo>()
                        .setTextRenderer(photoFormatter)
                        .setModel(resultsModel);
        searchBox = new Textbox("lolcat");
        //set up the GUI
        Control panel = new VFlexBox()
                .setBoxAlign(FlexBox.Align.Stretch)
                //a hbox with the search field and button
                .add(new HFlexBox()
                        .setBoxAlign(FlexBox.Align.Stretch)
                        .add(searchBox,1)
                        .add(new Button("search")
                        .onClicked(searchHandler))
                )
                //create a list of photos in a scroll pane. give it all the vspace
                .add(new ScrollPane(photoList),1)
                //an open button at the bottom
                .add(new Button("open")
                        .onClicked(openHandler))
                ;
        Stage stage = Stage.createStage();
        stage.setContent(panel);
    }
}
