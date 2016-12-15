package com.example.yone.iplay.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.yone.iplay.R;
import com.example.yone.iplay.fragment.DownloadMusicFragment;
import com.example.yone.iplay.http.GsonRequest;
import com.example.yone.iplay.http.RequestManager;
import com.example.yone.iplay.http.TingAPI;
import com.example.yone.iplay.model.DownFileInfo;
import com.example.yone.iplay.model.OnlineSongs;
import com.example.yone.iplay.model.SongsUrl;
import com.example.yone.iplay.service.DownloadService;

import java.util.List;

/**
 * Created by Yone on 2015/7/8.
 */
public class OnlineSongsAdapter extends SimpleBaseAdapter<OnlineSongs> {
    private List<OnlineSongs> SongData;
    public String playUrl;
    private View view;
    private Context context;
    private OnlineSongs onlineSongs;
    public OnlineSongsAdapter(Context context, int layoutId, List<OnlineSongs> Songdata) {
        super(context, layoutId, Songdata);
        this.SongData = Songdata;
    }

    @Override
    public void getItemView(ViewHolder holder, OnlineSongs onlineSongs) {
         holder.setText(R.id.online_song_title,onlineSongs.getTitle())
                 .setText(R.id.online_song_artist,onlineSongs.getArtist())
                 .setText(R.id.comment,onlineSongs.getContent())
                 .setImageURL(R.id.online_song_Image,onlineSongs.getUrlPic());
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
                layoutId, position);
        getItemView(holder, getItem(position));
        holder.getView(R.id.downloadbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineSongs = SongData.get(position);
                getSongPlayUrlBysId(onlineSongs.getsId());
            }
        });

        return holder.getConvertView();
    }

    public void getSongPlayUrlBysId(long s_Id){
        final GsonRequest<SongsUrl> request = TingAPI.getOnLineSongsRequest(s_Id);
        final Response.Listener<SongsUrl> response = new Response.Listener<SongsUrl>() {
            @Override
            public void onResponse(SongsUrl songsUrl) {
                playUrl = songsUrl.getSongurl();
                final DownFileInfo fileInfo = new DownFileInfo(0,playUrl,onlineSongs.getArtist() + "-" +
                        onlineSongs.getTitle()+".MP3",0,0);
                Intent intent = new Intent(mContext, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("fileInfo", fileInfo);
                mContext.startService(intent);
                DownloadMusicFragment.isDownLoad = true;
                Toast.makeText(mContext,"加入下载队列",Toast.LENGTH_SHORT).show();
            }
        };
        request.setSuccessListener(response);
        RequestManager.addRequest(request, s_Id);
    }
}
