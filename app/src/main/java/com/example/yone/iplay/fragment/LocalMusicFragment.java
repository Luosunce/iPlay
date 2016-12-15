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
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yone.iplay.R;
import com.example.yone.iplay.adapter.LocalSongsAdapter;
import com.example.yone.iplay.model.LocalSongs;
import com.example.yone.iplay.service.MusicService;
import com.example.yone.iplay.service.MusicService.MusicBinder;
import com.example.yone.iplay.utils.MediaUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by Yone on 2015/6/28.
 */
public class LocalMusicFragment extends Fragment{


    private ListView localListview;
    private List<LocalSongs> localSongsList;
    private ImageView ablbumbtn,play,repeatbtn,prebtn,playbtn,nextbtn,soundbtn;
    private TextView songsTitle,songsArtist,songsCurrent,songsDuration;
    private SeekBar SongSeekBar,SongSeekBarPlaying,sb_player_voice;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private RelativeLayout ll_player_voice;
    private View view;
    private int currentMusic; //当前播放歌曲
    private int currentPostion; //当前播放歌曲位置
    private int currentMax;
    private MusicBinder musicBinder;
    private MusicReceiver musicReceiver;
    private AudioManager am;  //音频管理引用，提供对音频的控制
    private int currentVolume; //当前音量
    private int maxVolume; //最大音量
    //音量面板显示和隐藏动画
    private Animation showVoicePanelAnimation;
    private Animation hiddenVoicePanelAnimation;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void connectToMusicService(){
        Intent intent = new Intent(view.getContext(), MusicService.class);
        getActivity().bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.local_layout, container, false);
        initView();
        connectToMusicService();
        localSongsList = MediaUtils.getLocalSongsInfo(view.getContext());
      //  localListview.setOnScrollListener(new ListViewOnScrollListener());
        if (localSongsList.size() != 0) {
            localListview.setAdapter(new LocalSongsAdapter(getActivity(), R.layout.local_songs_info, localSongsList));
            Bitmap bitmap = MediaUtils.getArtwork(view.getContext(), localSongsList.get(0).getId(),
                    localSongsList.get(0).getAlbumId(), true, true);
            ablbumbtn.setImageBitmap(bitmap);
            songsTitle.setText(localSongsList.get(0).getTitle());
            songsArtist.setText(localSongsList.get(0).getArtist());
        }else {
            Toast.makeText(getActivity(),"您的本地歌曲空空如也,请尽快下载！",Toast.LENGTH_SHORT).show();
            play.setVisibility(view.INVISIBLE);
        }

        localListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentMusic = position;
                    musicBinder.startPlay(currentMusic,0);
                    if (musicBinder.isPlaying()){
                        play.setImageResource(R.mipmap.ic_pause_black_large);
                        playbtn.setImageResource(R.mipmap.ic_pause_black_large);
                    }
            }
        });


        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                play.setVisibility(view.VISIBLE);
                SongSeekBar.setVisibility(view.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View view) {
                play.setVisibility(view.INVISIBLE);
                SongSeekBar.setVisibility(view.INVISIBLE);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        //添加来电监听事件
       TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(
                Context.TELEPHONY_SERVICE); //获取系统服务
        telephonyManager.listen(new MobliePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);
        return  view;
    }

    /**
     * listview 滚动监听器
     */
    public class ListViewOnScrollListener implements AbsListView.OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == 1){  //当用户向上滑时，隐藏SlidingUpPaneLayout
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }else if (scrollState == 2){  //当用户向下滑时，显示SlidingUpPanelLayout
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }

    /**
     * 初始化控件
     */
    private void initView(){
        localListview = (ListView) view.findViewById(R.id.local_song_list);
        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_UpPaneL);
        play = (ImageView) view.findViewById(R.id.play);
        ablbumbtn = (ImageView) view.findViewById(R.id.album_image);
        repeatbtn = (ImageView) view.findViewById(R.id.repeat);
        prebtn = (ImageView) view.findViewById(R.id.pre);
        playbtn = (ImageView) view.findViewById(R.id.playbtn);
        nextbtn = (ImageView) view.findViewById(R.id.next);
        soundbtn = (ImageView) view.findViewById(R.id.song_sound);
        songsTitle = (TextView) view.findViewById(R.id.playing_title);
        songsArtist = (TextView) view.findViewById(R.id.playing_artist);
        songsCurrent = (TextView) view.findViewById(R.id.current_position);
        songsDuration = (TextView) view.findViewById(R.id.playing_duration);
        SongSeekBar = (SeekBar) view.findViewById(R.id.songSeekBar);
        SongSeekBarPlaying = (SeekBar) view.findViewById(R.id.local_song_Seekbar);
        ll_player_voice = (RelativeLayout) view.findViewById(R.id.ll_player_voice);
        sb_player_voice = (SeekBar) view.findViewById(R.id.sb_player_voice);
        SongSeekBar.setOnSeekBarChangeListener(new SongSeekBarListtener());
        SongSeekBarPlaying.setOnSeekBarChangeListener(new SongSeekBarListtener());
        sb_player_voice.setOnSeekBarChangeListener(new SeekBarChangeListener());
        ViewOnclickListener viewOnclickListener = new ViewOnclickListener();
        play.setOnClickListener(viewOnclickListener);
        playbtn.setOnClickListener(viewOnclickListener);
        prebtn.setOnClickListener(viewOnclickListener);
        nextbtn.setOnClickListener(viewOnclickListener);
        soundbtn.setOnClickListener(viewOnclickListener);
        repeatbtn.setOnClickListener(viewOnclickListener);

        showVoicePanelAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.puch_up_in);
        hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.puch_up_out);

        //获得系统音频管理服务对象
        am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sb_player_voice.setMax(maxVolume);
        sb_player_voice.setProgress(currentVolume);
    }

    private void play(int position){
        if (musicBinder.isPlaying()){
            musicBinder.stopPlay();
            play.setImageResource(R.mipmap.ic_play_black_round_big);
            playbtn.setImageResource(R.mipmap.ic_play_black_round);
        }else {
            musicBinder.startPlay(position,currentPostion);
            play.setImageResource(R.mipmap.ic_pause_black_large);
            playbtn.setImageResource(R.mipmap.ic_pause_black_large);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
        if (musicBinder != null){
            if (musicBinder.isPlaying()){
                play.setImageResource(R.mipmap.ic_pause_black_large);
            }else {
                play.setImageResource(R.mipmap.ic_play_black_round_big);
            }
            musicBinder.notifyActivity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(musicReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicBinder != null){
            getActivity().unbindService(serviceConnection);
        }
    }

    /**
     * 电话监听类
     */
    private boolean phoneState = false; //电话状态
    private class MobliePhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    if (phoneState){
                        play(currentMusic);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //通话状态
                case TelephonyManager.CALL_STATE_RINGING: //响铃状态
                    musicBinder.stopPlay();
                    phoneState = true;
                    break;
                default:
                    break;
            }
        }
    }

    private void registerReceiver(){
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_UPDATE_PROGRESS);
        intentFilter.addAction(MusicService.ACTION_UPDATE_DURATION);
        intentFilter.addAction(MusicService.ACTION_UPDATE_CURRENT_MUSIC);
        getActivity().registerReceiver(musicReceiver, intentFilter);
    }

    private class ViewOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play:
                    play(currentMusic);
                    break;
                case R.id.playbtn:
                    play(currentMusic);
                    break;
                case R.id.next:
                    musicBinder.toPlayNext();
                    playbtn.setImageResource(R.mipmap.ic_pause_black_large);
                    play.setImageResource(R.mipmap.ic_pause_black_large);
                    break;
                case R.id.pre:
                    musicBinder.toPlayPrevious();
                    playbtn.setImageResource(R.mipmap.ic_pause_black_large);
                    play.setImageResource(R.mipmap.ic_pause_black_large);
                    break;
                case R.id.song_sound:
                    voicePanelAnimation();
                    break;
                case R.id.repeat:
                    switch (musicBinder.getCurrentMode()){
                        case MusicService.MODE_ALL_LOOP:
                            repeatbtn.setImageResource(R.mipmap.ic_repeat_one_song_dark);
                            musicBinder.changeMode(MusicService.MODE_ONE_LOOP);
                            break;
                        case MusicService.MODE_ONE_LOOP:
                            repeatbtn.setImageResource(R.mipmap.ic_shuffle_dark);
                            musicBinder.changeMode(MusicService.MODE_RANDOM);
                            break;
                        case MusicService.MODE_RANDOM:
                            repeatbtn.setImageResource(R.mipmap.ic_repeat_dark);
                            musicBinder.changeMode(MusicService.MODE_SEQUENCE);
                            break;
                        case MusicService.MODE_SEQUENCE:
                            repeatbtn.setImageResource(R.mipmap.ic_repeat_dark_selected);
                            musicBinder.changeMode(MusicService.MODE_ALL_LOOP);
                            break;
                        default:
                            repeatbtn.setImageResource(R.mipmap.ic_repeat_dark);
                    }
                default:
                    break;
            }
        }
    }

    //控制显示音量控制面板的动画
    public void voicePanelAnimation(){
        if (ll_player_voice.getVisibility() == View.GONE){
            ll_player_voice.startAnimation(showVoicePanelAnimation);
            ll_player_voice.setVisibility(View.VISIBLE);
        }else {
            ll_player_voice.startAnimation(hiddenVoicePanelAnimation);
            ll_player_voice.setVisibility(View.GONE);
        }
    }

    /**
     * 广播接收类
     */
    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (localSongsList.size() != 0) {
                if (MusicService.ACTION_UPDATE_PROGRESS.equals(action)) {
                    int progress = intent.getIntExtra(MusicService.ACTION_UPDATE_PROGRESS, 0);
                    if (progress > 0) {
                        currentPostion = progress;
                        SongSeekBar.setProgress(progress / 1000);
                        SongSeekBarPlaying.setProgress(progress / 1000);
                        songsCurrent.setText(MediaUtils.formatime(progress));
                    }
                } else if (MusicService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)) {
                    currentMusic = intent.getIntExtra(MusicService.ACTION_UPDATE_CURRENT_MUSIC, 0);
                    LocalSongs localSongs = localSongsList.get(currentMusic);
                    Bitmap bitmap = MediaUtils.getArtwork(view.getContext(), localSongs.getId(),
                            localSongs.getAlbumId(), true, true);
                    ablbumbtn.setImageBitmap(bitmap);
                    songsTitle.setText(localSongs.getTitle());
                    songsArtist.setText(localSongs.getArtist());
                } else if (MusicService.ACTION_UPDATE_DURATION.equals(action)) {
                    currentMax = intent.getIntExtra(MusicService.ACTION_UPDATE_DURATION, 0);
                    int max = currentMax / 1000;
                    SongSeekBar.setMax(max);
                    SongSeekBarPlaying.setMax(max);
                    songsDuration.setText(MediaUtils.formatime(currentMax));
                }
            }
        }
    }

    /**
     * 进度条监听器
     */
    private class SongSeekBarListtener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
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

    private class  SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()){
                case R.id.sb_player_voice:
                    //设置音量
                    am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
