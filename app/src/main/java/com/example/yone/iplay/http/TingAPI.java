package com.example.yone.iplay.http;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.yone.iplay.model.OnlineSongs;
import com.example.yone.iplay.model.SongsUrl;
import com.example.yone.iplay.utils.Ting;

import java.util.List;

/**
 * Created by Yone on 2015/7/6.
 */
public class TingAPI {

    //API地址
    private final static String TingApi = "http://tinger.herokuapp.com/api";

    /**
     * 获取歌曲信息
     */
    private final static String TingSongApi = TingApi + "/songs";
    private final static String TingSongCount = TingSongApi + "?songs_count=";

    /**
     * 根据歌曲s_id获取该歌曲的播放地址
     * 例：http://inmusic.sinaapp.com/xiami_api/ + s_id(歌曲虾米ID)
     */
    private final static String TingSongsAddressApi = "http://inmusic.sinaapp.com/xiami_api/";

    //歌曲播放Id
    private final static int sId = 0;

    /**
     * 获取歌曲列表集合
     * @param songCount 歌曲数量
     * @return
     */
    public static GsonRequest<List<OnlineSongs>> getSongsRequest(int songCount){
        final String songsUrl;
        if (songCount == 0){
            songsUrl = TingSongApi;  //获取所有歌曲
        }else {
            songsUrl = TingSongCount + songCount;
        }
        return new GsonRequest<List<OnlineSongs>>(songsUrl,buildDefaultErrorListener());
    }

    public static GsonRequest<SongsUrl> getOnLineSongsRequest(long s_Id){
        final String onLineSongsUrl = TingSongsAddressApi + s_Id;
        return new GsonRequest<SongsUrl>(onLineSongsUrl,SongsUrl.class,
                null,buildDefaultErrorListener());
    }

    /**
     * 响应失败
     * @return
     */
    private static Response.ErrorListener buildDefaultErrorListener(){
        return new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(Ting.getInstance(), "您的网络不给力", Toast.LENGTH_LONG).show();
            }
        };
    }
}
