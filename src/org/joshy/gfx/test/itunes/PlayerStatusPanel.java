package org.joshy.gfx.test.itunes;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.layout.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 1, 2010
 * Time: 9:08:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerStatusPanel extends Panel {
    @Override
    protected void drawSelf(GFX g) {

        //#EEF0E3
        //#EAEDDE

        //#E8ECD4
        //#F3F4E5

        double arc = 10;

        //background
        g.setPaint(new FlatColor("#EEf0E3"));
        g.fillRoundRect(0,0,getWidth(),getHeight(),arc,arc);
        g.setPaint(new FlatColor("#E8ECD4"));
        g.fillRect(0,getHeight()/2,getWidth(),getHeight()/2-2);

        //border
        g.setPaint(new FlatColor("#b0b0b0"));
        g.drawRoundRect(0,1,getWidth(),getHeight(),arc,arc);
        g.setPaint(new FlatColor("#808080"));
        g.drawRoundRect(0,0,getWidth(),getHeight(),arc,arc);

        //text
        g.setPaint(new FlatColor("#3D3F35"));
        Font.drawCentered(g, "Everybody Knows", Font.DEFAULT, 0,0, getWidth(),getHeight()/2, false);
        Font.drawCentered(g, "Evolver", Font.DEFAULT, 0,18, getWidth(),getHeight()/2, false);

        g.setPaint(new FlatColor("#B9BCA9"));
        g.fillRoundRect(60,getHeight()-13, getWidth()-120, 9, 8,8);
        g.setPaint(new FlatColor("#3D3F35"));
        g.drawRoundRect(60,getHeight()-13, getWidth()-120, 9, 8,8);
        double fraction = 0.33;
        g.fillRoundRect(60,getHeight()-13, (getWidth()-120)*fraction, 9, 8,8);



        g.setPaint(new FlatColor("#3D3F35"));
        g.drawText("3:13", Font.DEFAULT, 30, getHeight()-7);
        g.drawText("-3:13", Font.DEFAULT, getWidth()-30-Font.DEFAULT.getWidth("-3:13"), getHeight()-7);

    }
}
