package org.joshy.gfx.test.itunes;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 28, 2010
* Time: 9:20:01 PM
* To change this template use File | Settings | File Templates.
*/
class Song {
    int trackNumber;
    int totalTracks;
    String name;
    String album;
    String artist;
    int duration;

   public Song(int trackNumber, int totalTracks, String name, String album, String artist, int duration) {
       this.trackNumber = trackNumber;
       this.totalTracks = totalTracks;
       this.name = name;
       this.album = album;
       this.artist = artist;
       this.duration = duration;
   }
}
