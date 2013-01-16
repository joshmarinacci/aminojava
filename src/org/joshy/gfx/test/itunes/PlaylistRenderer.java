package org.joshy.gfx.test.itunes;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.TableView;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 28, 2010
* Time: 9:20:17 PM
* To change this template use File | Settings | File Templates.
*/
class PlaylistRenderer implements TableView.DataRenderer<Song> {
    private Font font;

    PlaylistRenderer() {
        font = Font.name("Helvetica").size(12).resolve();
    }

    public void draw(GFX gfx, TableView table, Song song, int row, int column, double x, double y, double width, double height) {
        if(row%2==0) {
            gfx.setPaint(new FlatColor(0xf8f8ff));
        } else {
            gfx.setPaint(new FlatColor(0xffffff));
        }
        if(row == table.getSelectedRow()) {
            if(table.isFocused()) {
                gfx.setPaint(new FlatColor("#ddddff"));
            } else {
                gfx.setPaint(new FlatColor("#dddddd"));
            }
        }
        gfx.fillRect(x,y,width,height);
        gfx.setPaint(FlatColor.BLACK);
        String text = "";
        if(song != null) {
            switch (column) {
                case 0 : text = song.name; break;
                case 1 : text = song.artist; break;
                case 2 : text = song.album; break;
                case 3 : text = "" + (int)(song.duration/60) + ":" + song.duration % 60; break;
                case 4 : text = song.trackNumber + " "; break;
                case 5 : text = song.totalTracks + " "; break;
                default: text = "--";
            }
        }
        Font.drawCenteredVertically(gfx,text,font,x+3,y,width,height,false);
        gfx.setPaint(new FlatColor("#d0d0d0"));
        gfx.drawLine(x+width-1,y, x+width-1,y+height);        
    }
}
