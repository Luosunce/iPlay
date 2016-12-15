package com.example.yone.iplay.model;

/**
 * Created by Yone on 2015/7/6.
 */
public class SongsUrl {

    private int id ;  //歌曲播放id

    private String songurl; //歌曲播放地址

    public void setId(int id) {
        this.id = id;
    }

    public void setSongurl(String songurl) {
        this.songurl = songurl;
    }

    public int getId() {
        return id;
    }

    public String getSongurl() {
        return songurl;
    }


}
