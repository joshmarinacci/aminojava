package org.joshy.gfx.test.itunes;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.Spacer;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 28, 2010
 * Time: 6:53:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ITunes implements Runnable {
    
    public static void main(String... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new ITunes());
    }

    public void run() {

        Stage stage = Stage.createStage();
        stage.setMinimumWidth(700);
        stage.setMinimumHeight(400);


        Button prevButton = new Button("<");
//            prevButton.setVariant("imageOnly");
//            prevButton.setNormalIcon(new File("assets/itunes/prevButton.png").toURI().toURL());
//            prevButton.setPressedIcon(new File("assets/itunes/prevButton_pressed.png").toURI().toURL());

        Button playButton = new Button("p");
//            playButton.setVariant("imageOnly");
//            playButton.setNormalIcon(new File("assets/itunes/play_button.png").toURI().toURL());
//            playButton.setPressedIcon(new File("assets/itunes/pause_button.png").toURI().toURL());

        Button nextButton = new Button(">");
//            nextButton.setVariant("imageOnly");
//            nextButton.setNormalIcon(new File("assets/itunes/nextButton.png").toURI().toURL());
//            nextButton.setPressedIcon(new File("assets/itunes/nextButton_pressed.png").toURI().toURL());

        final Slider volume = new Slider(false);
        volume.setMin(1);
        volume.setValue(5);
        volume.setMax(20);


        final Panel statusPanel = new PlayerStatusPanel();
        statusPanel.setPrefWidth(200);
        statusPanel.setPrefHeight(50);
        statusPanel.setFill(FlatColor.PURPLE);

        Button listToggle = new Button("L");
        Button gridToggle = new Button("G");
        Button coverflowToggle = new Button("C");

        final Textbox search = new Textbox();
        search.setHintText("search");
        search.addCSSClass("searchbox")
                .setPrefWidth(100);
        final Control header = new HFlexBox()
            .setBoxAlign(HFlexBox.Align.Baseline)
            .add(prevButton).add(playButton).add(nextButton)
            .add(new Spacer(),1)
            .add(volume)
            .add(new Spacer(),1)
            .add(statusPanel,1)
            .add(new Spacer(),1)
            .add(listToggle,gridToggle,coverflowToggle)
            .add(search)
            ;
        
        header.setPrefHeight(70);
        final Label status = new Label("10 items, 40:07 total time, 46.1 MB");
        final Panel footer = new HFlexBox()
                .add(new Spacer(),1)
//                    .add(new Button("+").setVariant("footerControl"),0)
//                    .add(new Button("s").setVariant("footerControl"),0)
//                    .add(new Button("r").setVariant("footerControl"),0)
//                    .add(new Button("^").setVariant("footerControl"),0)
                .add(new Spacer(),1)
                .add(status)
                .add(new Spacer(),1);
//                    .add(new Button("L").setVariant("footerControl"),0)
//                    .add(new Button("*").setVariant("footerControl"),0)
//                    .add(new Button("<").setVariant("footerControl"),0);

        //footer.setTranslateY(400);
        footer.setPrefHeight(40);

        final ListView sourceList = new ListView();
        sourceList.setModel(new SourceListModel());
        sourceList.setRenderer(new SourceListRenderer());

        final ScrollPane sourcePane = new ScrollPane();
        sourcePane.setWidth(200);
        sourcePane.setContent(sourceList);
        sourcePane.setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never);

        final TableView<Song, String> playList = new TableView<Song,String>();
        playList.setModel(new PlaylistModel());
        playList.setDefaultColumnWidth(150);
        playList.setRenderer(new PlaylistRenderer());
        playList.setFilter(new TableView.Filter<Song,String>() {
            @Override
            public boolean matches(TableView.TableModel<Song,String> table, int row) {
                String text = search.getText();
                if(text.length() < 2) return true;
                Song data = table.get(row,0);
                if(data.name.toLowerCase().contains(text.toLowerCase())) {
                    return true;
                }
                if(data.album.toLowerCase().contains(text.toLowerCase())) {
                    return true;
                }
                if(data.artist.toLowerCase().contains(text.toLowerCase())) {
                    return true;
                }
                return false;
            }
        });

        EventBus.getSystem().addListener(search, KeyEvent.KeyReleased,new Callback<KeyEvent>(){
            @Override
            public void call(KeyEvent event) throws Exception {
                playList.refilter();
            }
        });


        final ScrollPane playPane = new ScrollPane();
        playPane.setContent(playList);

        Panel top = new Panel() {
            @Override
            public void doLayout() {
                //position children, then do their layouts
                header.setWidth(this.getWidth());
                footer.setWidth(this.getWidth());
                footer.setTranslateY(this.getHeight() - footer.getHeight());

                sourcePane.setTranslateY(header.getHeight());
                sourcePane.setHeight(this.getHeight() - footer.getHeight() - header.getHeight());
                sourcePane.setTranslateX(0);

                playPane.setTranslateX(sourcePane.getWidth());
                playPane.setTranslateY(header.getHeight());
                playPane.setHeight(this.getHeight() - footer.getHeight() - header.getHeight());
                playPane.setWidth(this.getWidth() - sourcePane.getWidth());
                super.doLayout();
            }
        };
        //top.setFill(FlatColor.BLUE);

        top.add(header, sourcePane, playPane, footer);

        stage.setContent(top);
        stage.setWidth(800);
    }

}
