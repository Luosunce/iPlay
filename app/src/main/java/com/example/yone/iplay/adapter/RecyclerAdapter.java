package com.example.yone.iplay.adapter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.yone.iplay.R;
import com.example.yone.iplay.fragment.OnlineMusicFragment;
import com.example.yone.iplay.http.GsonRequest;
import com.example.yone.iplay.http.RequestManager;
import com.example.yone.iplay.http.TingAPI;
import com.example.yone.iplay.model.OnlineSongs;
import com.example.yone.iplay.model.SongsUrl;
import com.example.yone.iplay.service.MusicService;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Yone on 2015/7/6.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<OnlineMusicFragment.ViewHolder>{

    private static List<OnlineSongs> SongData;
    private Context mContext;
    private View view;
    public static String playUrl = "";
    private int currentPostion;
    private MusicService.MusicBinder musicBinder;
    public RecyclerAdapter(Context mContext,List<OnlineSongs> SongData){
        this.SongData = SongData;
        this.mContext = mContext;
    }

/*

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_songs_info,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder){
            ((ViewHolder) holder).tvTitle.setText(SongData.get(position).getTitle());
            ((ViewHolder) holder).tvArtist.setText(SongData.get(position).getArtist());
            ((ViewHolder) holder).tvComment.setText(SongData.get(position).getTitle());
            Picasso.with(mContext).load(SongData.get(position).getUrlPic())
                    .placeholder(R.mipmap.music_listen)
                    .error(R.mipmap.music_listen)
                    .into(((ViewHolder) holder).imSong);
            ((ViewHolder) holder).songCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnlineMusicFragment.getSongPlayUrlBysId(SongData.get(position).getsId());
                    String url = OnlineMusicFragment.playUrl;
                    OnlineMusicFragment.play(url);
                }
            });

        }
    }
*/

    /**
     * 根据s_Id获取歌曲播放地址
     * @param s_Id
     */
    public static void getSongPlayUrlBysId(long s_Id){
        final GsonRequest<SongsUrl> request = TingAPI.getOnLineSongsRequest(s_Id);
        final Response.Listener<SongsUrl> response = new Response.Listener<SongsUrl>() {
            @Override
            public void onResponse(SongsUrl songsUrl) {
               playUrl  = songsUrl.getSongurl();
            }
        };
        request.setSuccessListener(response);
        RequestManager.addRequest(request, s_Id);
    }


      @Override
        public OnlineMusicFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_songs_info,parent,false);
            return new OnlineMusicFragment.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OnlineMusicFragment.ViewHolder holder, int position) {
            holder.tvTitle.setText(SongData.get(position).getTitle());
            holder.tvArtist.setText(SongData.get(position).getArtist());
            holder.tvComment.setText(SongData.get(position).getContent());
            Picasso.with(mContext).load(SongData.get(position).getUrlPic())
                    .placeholder(R.mipmap.music_listen)
                    .error(R.mipmap.music_listen)
                    .into(holder.imSong);

        }

    @Override
    public int getItemCount() {
        return SongData.size();
    }
 /*
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle,tvArtist,tvComment,songsTitle;
        CircleImageView imSong;
        CardView songCardView;
       public ViewHolder(final View itemView){
           super(itemView);
           tvTitle = (TextView) itemView.findViewById(R.id.online_song_title);
           tvArtist = (TextView) itemView.findViewById(R.id.online_song_artist);
           tvComment = (TextView) itemView.findViewById(R.id.comment);
           songsTitle = (TextView)itemView.findViewById(R.id.playing_title);
           imSong = (CircleImageView) itemView.findViewById(R.id.online_song_Image);
           songCardView = (CardView) itemView.findViewById(R.id.songCardView);
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

               }
           });
       }
   }*/

}
