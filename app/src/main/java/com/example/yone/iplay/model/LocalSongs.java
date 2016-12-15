package com.example.yone.iplay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yone on 2015/6/30.
 */
public class LocalSongs implements Parcelable{
    //歌曲Id
    private long id;
    //歌曲名
    private String title;
    //歌手
    private String artist;
    //歌曲图片id
    private int albumId;
    //歌曲图片
    private String album;
    //歌曲时长
    private long duration;
    //歌曲路径
    private String url;
    //歌曲大小
    private int size;

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }

    public int getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
           dest.writeLong(id);
           dest.writeString(title);
           dest.writeString(artist);
           dest.writeInt(albumId);
           dest.writeString(album);
           dest.writeLong(duration);
           dest.writeString(url);
           dest.writeInt(size);
    }

    public static final Creator<LocalSongs>
            CREATOR = new Creator<LocalSongs>() {
        @Override
        public LocalSongs createFromParcel(Parcel source) {
            LocalSongs localSongs = new LocalSongs();
            localSongs.setId(source.readLong());
            localSongs.setTitle(source.readString());
            localSongs.setArtist(source.readString());
            localSongs.setAlbumId(source.readInt());
            localSongs.setAlbum(source.readString());
            localSongs.setDuration(source.readLong());
            localSongs.setSize(source.readInt());
            localSongs.setUrl(source.readString());
            return localSongs;
        }

        @Override
        public LocalSongs[] newArray(int size) {
            return new LocalSongs[size];
        }
    };
}
