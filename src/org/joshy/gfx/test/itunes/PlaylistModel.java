package org.joshy.gfx.test.itunes;

import org.joshy.gfx.node.control.TableView;

import java.util.ArrayList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 28, 2010
* Time: 9:20:33 PM
* To change this template use File | Settings | File Templates.
*/
class PlaylistModel implements TableView.TableModel<Song,String> {
    private List<Song> songs;

    PlaylistModel() {
        songs = new ArrayList<Song>();
        for(int i=0; i<15; i++) {
            songs.add(new Song(i+1,15,"song " + i, "The Album", "The Artist", (int)(Math.random()*300)));
        }
        for(int i=0; i<15; i++) {
            songs.add(new Song(i+1,15,"nother " + i, "The Nother Album", "The Nother Artist", (int)(Math.random()*300)));
        }
    }

    public int getRowCount() {
        return songs.size();
    }

    public int getColumnCount() {
        return 6;
    }

    public String getColumnHeader(int column) {
        String text = "";
        switch (column) {
            case 0 : text = "Name"; break;
            case 1 : text = "Artist"; break;
            case 2 : text = "Album"; break;
            case 3 : text = "duration"; break;
            case 4 : text = "TrackNumber"; break;
            case 5 : text = "totalTracks"; break;
            default: text = "--";
        }
        return text;
    }

    public Song get(int row, int column) {
        return songs.get(row);
    }
}
