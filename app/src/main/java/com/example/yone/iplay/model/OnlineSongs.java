package com.example.yone.iplay.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yone on 2015/7/3.
 */
public class OnlineSongs implements Parcelable{

     //歌曲Id
    private int id;

    //歌曲来源Id
    @SerializedName("s_id")
    private Long sId;

    //歌曲名称
    private String title;

    //歌曲名称
    private String artist;

    //歌曲封面图片Url
    @SerializedName("pic")
    private String urlPic;

    //歌曲评论
    private String content;

    public void setId(int id) {
        this.id = id;
    }

    public void setsId(Long sId) {
        this.sId = sId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrlPic(String urlPic) {
        this.urlPic = urlPic;
    }

    public int getId() {
        return id;
    }

    public Long getsId() {
        return sId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getContent() {
        return content;
    }

    public String getUrlPic() {
        return urlPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
          dest.writeInt(id);
          dest.writeLong(sId);
          dest.writeString(title);
          dest.writeString(artist);
          dest.writeString(content);
          dest.writeString(urlPic);
    }
    public OnlineSongs(){

    }

    private OnlineSongs(Parcel in){
        this.id = in.readInt();
        this.sId = in.readLong();
        this.title = in.readString();
        this.artist = in.readString();
        this.content = in.readString();
        this.urlPic = in.readString();
    }

    public static final Creator<OnlineSongs> CREATOR = new Creator<OnlineSongs>() {
        @Override
        public OnlineSongs createFromParcel(Parcel source) {
            return new OnlineSongs(source);
        }

        @Override
        public OnlineSongs[] newArray(int size) {
            return new OnlineSongs[size];
        }
    };
}
