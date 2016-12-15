package com.example.yone.iplay.model;

import java.io.Serializable;

/**
 * Created by Yone on 2015/7/13.
 */
public class DownFileInfo  implements Serializable{

    private int id;
    private String url;
    private String fileName;
    private int length;
    private int finished;

    public DownFileInfo(){

    }

    public DownFileInfo(int id,String url,String fileName,int length,int finished){
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "DownFileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }

    public int getLength() {
        return length;
    }

    public int getFinished() {
        return finished;
    }
}
