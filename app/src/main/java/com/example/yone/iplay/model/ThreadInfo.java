package com.example.yone.iplay.model;

/**
 * Created by Yone on 2015/7/14.
 */
public class ThreadInfo {

    private int id;
    private String url;
    private int start;
    private int end;
    private int finished;

    public ThreadInfo(){

    }

    public ThreadInfo(int id,String url,int start,int end,int finished){
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", finished=" + finished +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public String getUrl() {
        return url;
    }

    public int getFinished() {
        return finished;
    }

    public int getEnd() {
        return end;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
