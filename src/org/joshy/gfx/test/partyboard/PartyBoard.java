package org.joshy.gfx.test.partyboard;

import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import org.joshy.gfx.Core;
import org.joshy.gfx.animation.Animateable;
import org.joshy.gfx.animation.AnimationDriver;
import org.joshy.gfx.animation.KeyFrameAnimator;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.Slider;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.FlexBox;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;
import org.joshy.gfx.util.xml.XMLRequest;

import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 5, 2010
 * Time: 7:34:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartyBoard implements Runnable {
    private Callback<ActionEvent> quitHandler;
    private Callback<ActionEvent> startHandler;
    private ParticleSimulator sim;
    private AnimationDriver anim;
    double width;
    double height;
    private XMLRequest twitterFeed;
    private Label tweetText;
    private Label tweetLabel;
    private Textbox hashBox;
    private Label messageLabel;
    private Textbox messageBox;
    private FlexBox controlPanel;
    Double speed = 1.0;
    Double gravity = 1.0;
    Double spring = 0.05;
    Double color = 1.0;
    private long lastMouseMove;
    private Double fontSize = 40.0;
    private Label tweetUser;

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new PartyBoard());
    }

    @Override
    public void run() {
        width = 1400;
        height = 900;
        hashBox = new Textbox("#webosdev");
        messageBox = new Textbox("Welcome to the Party");

        quitHandler = new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) {
                System.exit(0);
            }
        };

        startHandler = new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) {
                startSim(hashBox.getText(),messageBox.getText());
            }
        };


        Stage setup = Stage.createStage();
        setup.setContent(new VFlexBox()
                .setBoxAlign(VFlexBox.Align.Stretch)
                .add(new HFlexBox()
                    .setBoxAlign(HFlexBox.Align.Stretch)
                    .add(new Label("Twitter Hashcode"))
                    .add(hashBox,1)
                )
                .add(new HFlexBox()
                    .setBoxAlign(HFlexBox.Align.Stretch)
                    .add(new Label("message"))
                    .add(messageBox,1))
                .add(new HFlexBox()
                    .add(new Button("quit").onClicked(quitHandler))
                    .add(new Button("start").onClicked(startHandler)))

        );
    }

    private void startSim(final String hashTag, String message) {
        Stage fullscreen = Stage.createStage();
        fullscreen.setUndecorated(true);
        fullscreen.setWidth(width);
        fullscreen.setHeight(height);
        fullscreen.setFullScreen(true);
        width = fullscreen.getWidth();
        height = fullscreen.getHeight();
        sim = new ParticleSimulator(this);
        tweetLabel = new Label("Most recent tweet to " + hashTag);
        tweetLabel.setFont(Font.name("Arial").size(40).resolve())
                .setColor(new FlatColor(0.8,0.8,0.8,1.0))
                .setTranslateX(50)
                .setTranslateY(50)
                ;

        Font font = Font.name("Arial").size(60).resolve();
        tweetUser = new Label("@username (realname) :");
        tweetUser.setColor(new FlatColor(0xa0a0a0));
        tweetUser.setFont(font);
        tweetUser.setTranslateX(50);
        tweetUser.setTranslateY(140);

        tweetText = new Label("This is a tweet that is super crazy long");
        tweetText.setFont(font);
        tweetText.setPrefWidth(width/2-100)
                .setTranslateX(50)
                .setTranslateY(140+60);
        tweetText.setColor(FlatColor.WHITE);


        messageLabel = new Label(message);
        messageLabel.setFont(font)
                .setColor(FlatColor.WHITE)
                .setPrefWidth(width/2-100)
                .setTranslateX(width/2+50)
                .setTranslateY(height/2);


        controlPanel = new VFlexBox()
                .add(new Label("speed").setColor(FlatColor.BLACK))
                .add(new Slider(false).setMin(0).setMax(2.0).setValue(1.0).setId("speed"))
                .add(new Label("gravity").setColor(FlatColor.BLACK))
                .add(new Slider(false).setMin(0).setMax(3.0).setValue(1.0).setId("gravity"))
                .add(new Label("spring").setColor(FlatColor.BLACK))
                .add(new Slider(false).setMin(0).setMax(0.20).setValue(0.05).setId("spring"))
                .add(new Label("color speed").setColor(FlatColor.BLACK))
                .add(new Slider(false).setMin(0).setMax(5).setValue(1).setId("color"))
                .add(new Label("font size").setColor(FlatColor.BLACK))
                .add(new Slider(false).setMin(18).setMax(100).setValue(40).setId("fontsize"))
                .add(new Button("quit").onClicked(new Callback<ActionEvent>(){
                    @Override
                    public void call(ActionEvent event) throws Exception {
                        System.exit(0);
                    }
                }))
                ;

        controlPanel
                .setFill(FlatColor.WHITE)
                .setPrefHeight(300)
                ;
        controlPanel.
                setTranslateX(50)
                .setTranslateY(height-controlPanel.getPrefHeight())
                .setVisible(true);

        fullscreen.setContent(new Panel().add(sim
                ,tweetLabel
                ,tweetUser
                ,tweetText
                ,messageLabel
                ,controlPanel));

        EventBus.getSystem().addListener(ChangedEvent.DoubleChanged, new Callback<ChangedEvent>(){
            @Override
            public void call(ChangedEvent event) throws Exception {
                if(event.getSource() instanceof Slider) {
                    Slider sl = (Slider) event.getSource();
                    if("speed".equals(sl.getId())) {
                        speed = (Double)event.getValue();
                    }
                    if("gravity".equals(sl.getId())) {
                        gravity = (Double)event.getValue();
                    }
                    if("spring".equals(sl.getId())) {
                        spring = (Double)event.getValue();
                    }
                    if("color".equals(sl.getId())) {
                        color = (Double)event.getValue();
                    }
                    if("fontsize".equals(sl.getId())) {
                        setFontSize((Double)event.getValue());
                    }
                }
            }
        });

        EventBus.getSystem().addListener(MouseEvent.MouseMoved, new Callback<MouseEvent>() {
            @Override
            public void call(MouseEvent event) throws Exception {
                lastMouseMove = System.currentTimeMillis();
                controlPanel.setVisible(true);
            }
        });

        anim = new AnimationDriver(new Animateable() {

            @Override
            public void onStart(long current) {

            }

            @Override
            public void update(long currentTime) {
                if(System.currentTimeMillis() - lastMouseMove > 5*1000) {
                    controlPanel.setVisible(false);
                }
                sim.update();
            }

            @Override
            public void onStop(long currentTime) {

            }

            @Override
            public void loop() {

            }

            @Override
            public boolean isDone() {
                return false;
            }
        });
        anim.setFPS(30);
        anim.start();
        KeyFrameAnimator kf1 = new KeyFrameAnimator(tweetText)
                .property("opacity")
                .keyFrame(0,1.0)
                .keyFrame(3,1.0)
                .keyFrame(4,0.0)
                .keyFrame(9,0.0)
                .keyFrame(10,1.0)
                .repeat(KeyFrameAnimator.INFINITE);
        KeyFrameAnimator kf3 = new KeyFrameAnimator(tweetUser)
                .property("opacity")
                .keyFrame(0,1.0)
                .keyFrame(3,1.0)
                .keyFrame(4,0.0)
                .keyFrame(9,0.0)
                .keyFrame(10,1.0)
                .repeat(KeyFrameAnimator.INFINITE);
        KeyFrameAnimator kf3a = new KeyFrameAnimator(tweetLabel)
                .property("opacity")
                .keyFrame(0,1.0)
                .keyFrame(3,1.0)
                .keyFrame(4,0.0)
                .keyFrame(9,0.0)
                .keyFrame(10,1.0)
                .repeat(KeyFrameAnimator.INFINITE);
        KeyFrameAnimator kf2 = new KeyFrameAnimator(messageLabel)
                .property("opacity")
                .keyFrame(0,0.0)
                .keyFrame(4,0.0)
                .keyFrame(5,1.0)
                .keyFrame(8,1.0)
                .keyFrame(9,0.0)
                .keyFrame(10,0.0)
                .repeat(KeyFrameAnimator.INFINITE);

        AnimationDriver.start(kf1);
        AnimationDriver.start(kf2);
        AnimationDriver.start(kf3);
        AnimationDriver.start(kf3a);

        new PeriodicTask(10*1000)
                .call(new Callback(){
                    @Override
                    public void call(Object event) throws Exception {
                        searchTweet(hashTag);                        
                    }
                }).start();

        searchTweet(hashTag);
    }

    private void searchTweet(String hashTag) {
        if(twitterFeed == null || twitterFeed.isDone()) {
            try {

                twitterFeed = new XMLRequest()
                        .setURL("http://search.twitter.com/search.atom")
                        .setParameter("q",hashTag)
                        .setMethod(XMLRequest.METHOD.GET)
                        .onComplete(new Callback<Doc>(){
                            @Override
                            public void call(Doc doc) throws Exception {
                                //doc.dump();
                                String firstTweet = "";
                                String firstUser = "";
                                for(Elem e : doc.xpath("/feed/entry")) {
                                    //u.p("text = " + e.xpathString("title/text()").trim());
                                    //u.p("user = " + e.xpathString("author/name/text()").trim());
                                    firstTweet = e.xpathString("title/text()");
                                    firstUser = e.xpathString("author/name/text()").trim();
                                    break;
                                }
                                u.p("first tweet = " + firstTweet);
                                tweetText.setText(firstTweet);
                                tweetUser.setText("@"+firstUser+" :");
                            }
                        })
                        .onError(new Callback<Throwable>(){
                            @Override
                            public void call(Throwable event) throws Exception {
                                u.p("error!: " + event);
                                event.printStackTrace();
                            }
                        })
                        ;
                twitterFeed.start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
        Font font = Font.name("Arial").size(((float)fontSize)).resolve();
        tweetLabel.setFont(font);//Font.name("Arial").size((float) fontSize).resolve());
        tweetText.setFont(font);//Font.name("Arial").size((float) fontSize).resolve());
        messageLabel.setFont(font);//Font.name("Arial").size((float) fontSize).resolve());
        tweetUser.setFont(font);
    }
}
