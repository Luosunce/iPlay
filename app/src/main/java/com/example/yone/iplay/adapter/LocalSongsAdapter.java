package com.example.yone.iplay.adapter;

import android.content.Context;

import com.example.yone.iplay.R;
import com.example.yone.iplay.model.LocalSongs;
import com.example.yone.iplay.utils.MediaUtils;

import java.util.List;

/**
 * Created by Yone on 2015/6/30.
 */
public class LocalSongsAdapter extends SimpleBaseAdapter<LocalSongs> {

    private Context context;

    public LocalSongsAdapter(Context context,int layoutId,List<LocalSongs> data){
        super(context,layoutId,data);
        this.context = context;
    }
    @Override
    public void getItemView(ViewHolder holder, LocalSongs localSongs) {
        holder.setText(R.id.song_title,localSongs.getTitle())
                .setText(R.id.song_artist,localSongs.getArtist())
                .setText(R.id.song_duration, MediaUtils.formatime(localSongs.getDuration()));

    }
}
