package com.example.yone.iplay.fragment;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.example.yone.iplay.R;
import com.example.yone.iplay.activity.MainActivity;
import com.example.yone.iplay.adapter.OnlineSongsAdapter;
import com.example.yone.iplay.adapter.RecyclerAdapter;
import com.example.yone.iplay.http.GsonRequest;
import com.example.yone.iplay.http.RequestManager;
import com.example.yone.iplay.http.TingAPI;
import com.example.yone.iplay.model.LocalSongs;
import com.example.yone.iplay.model.OnlineSongs;
import com.example.yone.iplay.model.SongsUrl;
import com.example.yone.iplay.service.MusicService;
import com.example.yone.iplay.service.MusicService.*;

import com.example.yone.iplay.service.OnlineMusicService;
import com.example.yone.iplay.service.OnlineMusicService.MusicBinder;
import com.example.yone.iplay.utils.MediaUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Yone on 2015/6/29.
 */
public class OnlineMusicFragment extends Fragment {

    private List<OnlineSongs> onlineSongsList;
    public static String playUrl = "";
    public static OnlineSongs onlineSongs;
    private View onlineView;
    private ListView songlistView;
    private SwipeRefreshLayout onlineSongSwipe;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    public ImageView ablbumbtn, imgBig, play, playbtn, soundbtn;
    private TextView songsTitle, songsArtist, songsCurrent, songsDuration;
    private SeekBar onlineSongSeekBar, onlineSongSeekBarPlaying, sb_player_voice;
    private RelativeLayout ll_player_voice;
    private AudioManager am;
    private int currentVolume;
    private int maxVolume;
    private Animation showVoicePanelAnimation;
    private Animation hiddenVoicePanelAnimation;
    private int currentPosition; //当前播放歌曲位置
    private int currentMusic; //当前播放歌曲
    private int currentMax; //歌曲最大长度
    private MusicBinder musicBinder;
    private MusicService.MusicBinder localmusicBinder;
    private OnlineMusicReceiver onlineMusicReceiver;

    private boolean isFirstPlay = true;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void connectToMusicService() {
        Intent intent = new Intent(onlineView.getContext(), OnlineMusicService.class);
        getActivity().bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onlineView = inflater.inflate(R.layout.online_layoyt, container, false);
        connectToMusicService();
        initView();
        getData(20);
        //给刷新控件一个颜色
        onlineSongSwipe.setColorSchemeColors(R.color.colorPrimary);
        /**
         *   进入界面进行刷新
         */
        onlineSongSwipe.post(new Runnable() {
            @Override
            public void run() {
                onlineSongSwipe.setRefreshing(true);
            }
        });
        /**
         * 刷新控件监听控件
         */
        onlineSongSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(20);
            }
        });

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                play.setVisibility(view.VISIBLE);
                onlineSongSeekBar.setVisibility(view.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View view) {
                play.setVisibility(view.INVISIBLE);
                onlineSongSeekBar.setVisibility(view.INVISIBLE);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });


        return onlineView;
    }

    public void initView() {
        songlistView = (ListView) onlineView.findViewById(R.id.online_songs_list);
        onlineSongSwipe = (SwipeRefreshLayout) onlineView.findViewById(R.id.song_swipe);
        slidingUpPanelLayout = (SlidingUpPanelLayout) onlineView.findViewById(R.id.online_sliding_panel);
        play = (ImageView) onlineView.findViewById(R.id.play);
        ablbumbtn = (ImageView) onlineView.findViewById(R.id.album_image);
        imgBig = (ImageView) onlineView.findViewById(R.id.song_Image_background);
        playbtn = (ImageView) onlineView.findViewById(R.id.playbtn);
        soundbtn = (ImageView) onlineView.findViewById(R.id.song_sound);
        songsTitle = (TextView) onlineView.findViewById(R.id.playing_title);
        songsArtist = (TextView) onlineView.findViewById(R.id.playing_artist);
        songsCurrent = (TextView) onlineView.findViewById(R.id.current_position);
        songsDuration = (TextView) onlineView.findViewById(R.id.playing_duration);
        onlineSongSeekBar = (SeekBar) onlineView.findViewById(R.id.online_SeekBar);
        onlineSongSeekBarPlaying = (SeekBar) onlineView.findViewById(R.id.online_song_Seekbar);
        sb_player_voice = (SeekBar) onlineView.findViewById(R.id.sb_player_voice);
        ll_player_voice = (RelativeLayout) onlineView.findViewById(R.id.ll_player_voice);
        onlineSongSeekBar.setOnSeekBarChangeListener(new SongSeekBarListener());
        onlineSongSeekBarPlaying.setOnSeekBarChangeListener(new SongSeekBarListener());
        sb_player_voice.setOnSeekBarChangeListener(new SeekBarChangeListener());
        ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
        play.setOnClickListener(viewOnClickListener);
        playbtn.setOnClickListener(viewOnClickListener);
        soundbtn.setOnClickListener(viewOnClickListener);

        showVoicePanelAnimation = AnimationUtils.loadAnimation(onlineView.getContext(), R.anim.puch_up_in);
        hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(onlineView.getContext(), R.anim.puch_up_out);

        am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sb_player_voice.setMax(maxVolume);
        sb_player_voice.setProgress(currentVolume);
        songlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentMusic = position;
                onlineSongs = onlineSongsList.get(currentMusic);
                long url = onlineSongsList.get(currentMusic).getsId();
                getSongPlayUrlBysId(url, currentMusic);
                play.setImageResource(R.mipmap.ic_pause_black_large);
                playbtn.setImageResource(R.mipmap.ic_pause_black_large);
                isFirstPlay = false;
            }
        });
    }

    /**
     * 获取API上的歌曲
     *
     * @param songCount
     */
    public void getData(final int songCount) {
        final GsonRequest<List<OnlineSongs>> request = TingAPI.getSongsRequest(songCount);
        final Response.Listener<List<OnlineSongs>> response = new Response.Listener<List<OnlineSongs>>() {
            @Override
            public void onResponse(List<OnlineSongs> onlineSongs) {
                onlineSongsList = onlineSongs;
                songlistView.setAdapter(new OnlineSongsAdapter(getActivity(), R.layout.online_songs_info, onlineSongsList));
                Picasso.with(onlineView.getContext()).load(onlineSongsList.get(0).getUrlPic()).into(ablbumbtn);
                songsTitle.setText(onlineSongsList.get(0).getTitle());
                songsArtist.setText(onlineSongsList.get(0).getArtist());
                onlineSongSwipe.setRefreshing(false);
            }
        };
        request.setSuccessListener(response);
        RequestManager.addRequest(request, null);
    }

    /**
     * 根据s_Id获取歌曲播放地址
     *
     * @param s_Id
     */
    public void getSongPlayUrlBysId(long s_Id, final int position) {
        final GsonRequest<SongsUrl> request = TingAPI.getOnLineSongsRequest(s_Id);
        final Response.Listener<SongsUrl> response = new Response.Listener<SongsUrl>() {
            @Override
            public void onResponse(SongsUrl songsUrl) {
                playUrl = songsUrl.getSongurl();
                musicBinder.startPlay(playUrl, 0, position);
            }
        };
        request.setSuccessListener(response);
        RequestManager.addRequest(request, s_Id);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
        if (musicBinder != null) {
            if (musicBinder.isPlaying()) {
                play.setImageResource(R.mipmap.ic_pause_black_large);
            } else {
                play.setImageResource(R.mipmap.ic_play_black_round_big);
            }
            musicBinder.notifyActivity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(onlineMusicReceiver);
    }

    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicBinder != null) {
            getActivity().unbindService(serviceConnection);
        }
    }

    public void play(int currentMusic) {
        if (musicBinder.isPlaying()) {
            musicBinder.stopPlay();
            play.setImageResource(R.mipmap.ic_play_black_round_big);
            playbtn.setImageResource(R.mipmap.ic_play_black_round_big);
        } else {
            musicBinder.startPlay(playUrl, currentPosition, currentMusic);
            play.setImageResource(R.mipmap.ic_pause_black_large);
            playbtn.setImageResource(R.mipmap.ic_pause_black_large);
        }
    }

    class ViewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play:
                case R.id.playbtn:
                    if (isFirstPlay) {
                        getSongPlayUrlBysId(onlineSongsList.get(0).getsId(), 0);
                        isFirstPlay = false;
                    } else {
                        play(currentMusic);
                    }
                    break;
                case R.id.song_sound:
                    voicePanelAnimation();
                    break;
                default:
                    break;
            }
        }
    }

    public void voicePanelAnimation() {
        if (ll_player_voice.getVisibility() == View.GONE) {
            ll_player_voice.startAnimation(showVoicePanelAnimation);
            ll_player_voice.setVisibility(View.VISIBLE);
        } else {
            ll_player_voice.startAnimation(hiddenVoicePanelAnimation);
            ll_player_voice.setVisibility(View.GONE);
        }
    }

    private class SongSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                musicBinder.changProgress(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.sb_player_voice:
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void registerReceiver() {
        onlineMusicReceiver = new OnlineMusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OnlineMusicService.ACTION_UPDATE_PROGRESS);
        intentFilter.addAction(OnlineMusicService.ACTION_UPDATE_DURATION);
        intentFilter.addAction(OnlineMusicService.ACTION_UPDATE_CURRENT_MUSIC);
        getActivity().registerReceiver(onlineMusicReceiver, intentFilter);
    }

    private class OnlineMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (OnlineMusicService.ACTION_UPDATE_PROGRESS.equals(action)) {
                int progress = intent.getIntExtra(OnlineMusicService.ACTION_UPDATE_PROGRESS, 0);
                if (progress > 0) {
                    currentPosition = progress;
                    onlineSongSeekBar.setProgress(progress / 1000);
                    onlineSongSeekBarPlaying.setProgress(progress / 1000);
                    songsCurrent.setText(MediaUtils.formatime(progress));
                }
            } else if (OnlineMusicService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)) {
                currentMusic = intent.getIntExtra(OnlineMusicService.ACTION_UPDATE_CURRENT_MUSIC, 0);
                playUrl = intent.getStringExtra(OnlineMusicService.ACTION_UPDATE_CURRENT_PLAYURL);
                OnlineSongs onlineSongs = onlineSongsList.get(currentMusic);
                Picasso.with(onlineView.getContext()).load(onlineSongsList.get(currentMusic).getUrlPic()).into(ablbumbtn);
                Picasso.with(onlineView.getContext()).load(onlineSongsList.get(currentMusic).getUrlPic()).into(imgBig);
                songsTitle.setText(onlineSongsList.get(currentMusic).getTitle());
                songsArtist.setText(onlineSongsList.get(currentMusic).getArtist());
            } else if (OnlineMusicService.ACTION_UPDATE_DURATION.equals(action)) {
                currentMax = intent.getIntExtra(OnlineMusicService.ACTION_UPDATE_DURATION, 0);
                int max = currentMax / 1000;
                onlineSongSeekBar.setMax(max);
                onlineSongSeekBarPlaying.setMax(max);
                songsDuration.setText(MediaUtils.formatime(currentMax));
            }

        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvArtist, tvComment;
        public CircleImageView imSong;
        public CardView songCarview;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.online_song_title);
            tvArtist = (TextView) itemView.findViewById(R.id.online_song_artist);
            tvComment = (TextView) itemView.findViewById(R.id.comment);
            imSong = (CircleImageView) itemView.findViewById(R.id.online_song_Image);
          /*  songCarview = (CardView) itemView.findViewById(R.id.songCardView);
            songCarview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentMusic = getPosition();
                    songsTitle.setText(onlineSongsList.get(currentMusic).getTitle());
                    songsArtist.setText(onlineSongsList.get(currentMusic).getArtist());
                    Picasso.with(itemView.getContext()).load(onlineSongsList.get(currentMusic).getUrlPic()).into(ablbumbtn);
                    getSongPlayUrlBysId(onlineSongsList.get(currentMusic).getsId());
                    musicBinder.startPlay(playUrl, currentMusic);
                }
            });*/
        }
    }

}
