package org.joshy.gfx.test.swingintegration;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.LinearGradientFill;
import org.joshy.gfx.draw.MultiGradientFill;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.node.shape.Text;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Dec 6, 2010
 * Time: 3:00:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwingChartDemo implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new SwingChartDemo());
    }

    @Override
    public void run() {
        List<ElfDataPoint> data = new ArrayList<ElfDataPoint>();
        data.add(new ElfDataPoint(0,90,9));
        data.add(new ElfDataPoint(1,60,5));
        data.add(new ElfDataPoint(2,50,6));
        data.add(new ElfDataPoint(3,50,5));
        data.add(new ElfDataPoint(4,50,5));
        data.add(new ElfDataPoint(5,50,6));
        data.add(new ElfDataPoint(6,70,8));
        data.add(new ElfDataPoint(7,90,9));
        data.add(new ElfDataPoint(8,50,6));
        data.add(new ElfDataPoint(9,60,2));
        data.add(new ElfDataPoint(10,80,9));
        data.add(new ElfDataPoint(11,90,10));


        //create a popup indicator
        final Group popup = new Group();
        //popup background
        popup.add(new Rectangle()
                .setWidth(200)
                .setHeight(100)
                .setArcWidth(10)
                .setArcHeight(10)
                .setStrokeWidth(5)
                .setStroke(new FlatColor(0x202020))
                .setFill(FlatColor.BLACK.deriveWithAlpha(0.7))
        );
        //popup current month
        final Text currentMonth = new Text();
        currentMonth
                .setFont(Font.name("Arial").size(24).resolve())
                .setFill(FlatColor.WHITE)
                .setTranslateX(10)
                .setTranslateY(25);
        popup.add(currentMonth);

        //popup avg pay label
        popup.add(new Text()
                .setText("Avg Pay:")
                .setFont(Font.name("Arial").size(24).resolve())
                .setFill(new FlatColor(0xd0d0d0))
                .setTranslateX(10)
                .setTranslateY(60)
        );

        //popup avg pay of current month
        final Text currentAvgPay = new Text();
        currentAvgPay
                .setFont(Font.name("Arial").size(24).resolve())
                .setFill(FlatColor.WHITE)
                .setTranslateX(130)
                .setTranslateY(60)
                ;
        popup.add(currentAvgPay);
        popup.setVisible(false);

        //popup avg pay label
        popup.add(new Text()
                .setText("Morale:")
                .setFont(Font.name("Arial").size(24).resolve())
                .setFill(new FlatColor(0xd0d0d0))
                .setTranslateX(10)
                .setTranslateY(90)
        );

        //popup elf attitude of current month
        final Text currentAttitude = new Text();
        currentAttitude
                .setFont(Font.name("Arial").size(24).resolve())
                .setFill(FlatColor.WHITE)
                .setTranslateX(130)
                .setTranslateY(90)
                ;
        popup.add(currentAttitude);
        popup.setVisible(false);


        final Group graph = new Group();
        for(final ElfDataPoint edp : data) {
            //create a group in the right position
            final Group g = new Group();
            g.setTranslateX(10+edp.month*40);
            g.setTranslateY(0);

            //calculate the color of the bar based on the elf attitude
            double colorAngle = edp.attitude*360/10;
            FlatColor start = FlatColor.hsb(colorAngle,1,0.8);
            FlatColor end = FlatColor.hsb(colorAngle,0.8,1);
            MultiGradientFill grad = new LinearGradientFill()
                    .setStartX(0).setStartY(0).setEndX(30-5).setEndY(0)
                    .addStop(0,start).addStop(1,end);

            //create a bar with the height based on pay
            final Rectangle bar = new Rectangle(0, 400-edp.avgPay * 4, 30, edp.avgPay * 4)
                    .setArcWidth(20)
                    .setArcHeight(20);
            bar.setFill(grad);
            g.add(bar);


            // add text for each month
            SimpleDateFormat sdf = new SimpleDateFormat("MMM");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.MONTH,edp.month);

            final String month = sdf.format(cal.getTime());
            g.add(new Text()
                    .setText(month)
                    .setFill(FlatColor.BLACK)
                    .setFont(Font.name("Arial").size(18).resolve())
                    .setTranslateY(420)
            );

            //add event to update and show the popup
            EventBus.getSystem().addListener(bar, MouseEvent.MouseMoved, new Callback<MouseEvent>(){
                @Override
                public void call(MouseEvent event) throws Exception {
                    popup.setVisible(true);
                    popup.setTranslateX(g.getTranslateX()+10);
                    popup.setTranslateY(bar.getY()-10);
                    currentMonth.setText(month);
                    currentAvgPay.setText(""+edp.avgPay);
                    currentAttitude.setText(""+edp.attitude);
                }
            });

            //add to the graph
            graph.add(g);
        }
        graph.setTranslateX(20);

        graph.add(popup);

        JButton b1 = new JButton("I'm a Swing Button");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(b1,BorderLayout.WEST);
        panel.add(new JLabel("I'm a Swing Label"),BorderLayout.NORTH);
        JComponentWrapper wrapper = new JComponentWrapper();
        wrapper.setContent(new Panel().setFill(FlatColor.WHITE).add(graph));
        panel.add(wrapper,BorderLayout.CENTER);

        JFrame frame = new JFrame("Amino + Swing Chart Demo");
        frame.pack();
        frame.add(panel);
        frame.setSize(800,480);
        frame.setVisible(true);

        
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<SystemMenuEvent>() {
            @Override
            public void call(SystemMenuEvent event) throws Exception {
                System.exit(-1);
            }
        });

    }

    //elf pay vs month, color elf attitude
    public class ElfDataPoint {
        private int attitude;
        private double avgPay;
        private int month;

        public ElfDataPoint(int month, double avgPay, int attitude) {
            this.month = month;
            this.avgPay = avgPay;
            this.attitude = attitude;
        }
    }
}
