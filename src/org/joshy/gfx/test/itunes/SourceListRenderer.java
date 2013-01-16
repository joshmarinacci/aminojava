package org.joshy.gfx.test.itunes;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.ListView;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 28, 2010
* Time: 9:20:47 PM
* To change this template use File | Settings | File Templates.
*/
class SourceListRenderer implements ListView.ItemRenderer {
    private Font font;

    SourceListRenderer() {
        font = Font.name("Helvetica").size(12).resolve();
    }

    public void draw(GFX gfx, ListView listView, Object item, int index, double x, double y, double width, double height) {
        gfx.setPaint(new FlatColor(0xf0e0ff));
        if(listView.getSelectedIndex() == index) {
            gfx.setPaint(new FlatColor(0x000088));
        }
        gfx.fillRect(x,y,width,height);
        gfx.setPaint(FlatColor.BLACK);
        if(listView.getSelectedIndex() == index) {
            gfx.setPaint(FlatColor.WHITE);
        }
        String source = (String) item;
        Font.drawCenteredVertically(gfx,source,font,x+3,y,width,height,false);
    }
}
