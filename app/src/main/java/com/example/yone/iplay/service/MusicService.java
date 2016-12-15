package com.example.yone.iplay.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.yone.iplay.model.LocalSongs;
import com.example.yone.iplay.utils.MediaUtils;

import java.io.IOException;
import java.util.List;
/**
 * Created by Yone on 2015/7/1.
 */
public class MusicService extends Service {

    public static MediaPlayer mediaPlayer;

    public static boolean isPlaying = false;

    private List<LocalSongs> localSongsList;

    private Binder musicBinder = new MusicBinder();

    private OnlineMusicService.MusicBinder onlineMusicBinder;

    private int currentMusic;//当前播放歌曲

    private int currentPosition;//当前播放到什么位置

    private static final String TAG = "com.example.yone.iplay.service.MusicService";

    private static final int updateProgress = 1; //更新进度条

    private static final  int updateCurrentMusic = 2; //更新当前播放歌曲

    private static final int updateDuration = 3; //更新当前播放时间

    public static final String ACTION_UPDATE_PROGRESS = "com.example.yone.iplay.UPDATE_PROGRESS";
    public static final String ACTION_UPDATE_DURATION = "com.example.yone.iplay.UPDATE_DURATION";
    public static final String ACTION_UPDATE_CURRENT_MUSIC = "com.example.yone.iplay.UPDATE_CURRENT_MUSIC";

    private int currentMode = 3; //默认播放模式

    public static final String[] MODE_DESC = {  //模式类型
            "单曲播放","循环播放","随机播放","顺序播放"};

    public static final int MODE_ONE_LOOP = 0;//单曲循环
    public static final int MODE_ALL_LOOP = 1;//全部循环
    public static final int MODE_RANDOM = 2; //随机播放
    public static final int MODE_SEQUENCE = 3; //顺序播放

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            onlineMusicBinder = (OnlineMusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void connectToMusicService(){
        Intent intent = new Intent(this,OnlineMusicService.class);
        this.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case updateProgress:
                    toUpdateProgress();
                    break;
                case updateDuration:
                    toUpdateDuration();
                    break;
                case updateCurrentMusic:
                    toUpdateCurrentMusic();
                    break;
            }
        }
    };

    private void toUpdateCurrentMusic(){
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_CURRENT_MUSIC);
        intent.putExtra(ACTION_UPDATE_CURRENT_MUSIC,currentMusic);
        sendBroadcast(intent);
    }

    private void toUpdateDuration(){
        if (mediaPlayer != null){
            int duration = mediaPlayer.getDuration();
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_DURATION);
            intent.putExtra(ACTION_UPDATE_DURATION,duration);
            sendBroadcast(intent);
        }
    }

    private void toUpdateProgress(){
        if (mediaPlayer != null){
            int progress = mediaPlayer.getCurrentPosition();
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_PROGRESS);
            intent.putExtra(ACTION_UPDATE_PROGRESS, progress);
            sendBroadcast(intent);
            handler.sendEmptyMessageDelayed(updateProgress, 1000);
        }
    }

    @Override
    public void onCreate() {
        initMediaPlayer();
        localSongsList = MediaUtils.getLocalSongsInfo(this);
        connectToMusicService();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     *  初始化MediaPlayer
     */
    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                mediaPlayer.seekTo(currentPosition);
                handler.sendEmptyMessage(updateDuration);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isPlaying){
                    switch (currentMode){
                        case MODE_ONE_LOOP:
                            mediaPlayer.start();
                            break;
                        case MODE_ALL_LOOP:
                            play((currentMusic + 1) % localSongsList.size(),0);
                            break;
                        case MODE_RANDOM:
                            play(getRandomPosition(),0);
                            break;
                        case MODE_SEQUENCE:
                            if (currentMusic < localSongsList.size() - 1){
                                playNext();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    //记录下当前播放歌曲位置
    private void setCurrentMusic(int pCurrentMusic){
        currentMusic = pCurrentMusic;
        handler.sendEmptyMessage(updateCurrentMusic);
    }

    //在歌曲列表长度中随机生成一个位置
    private int getRandomPosition(){
        int random = (int)(Math.random() * (localSongsList.size() - 1));
        return random;
    }

    private void play(int currentMusic,int pCurrentPosition){
        currentPosition = pCurrentPosition;
        setCurrentMusic(currentMusic);
        if (onlineMusicBinder != null){
            if (onlineMusicBinder.isPlaying()){
                onlineMusicBinder.stopPlay();
            }
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(localSongsList.get(currentMusic).getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        handler.sendEmptyMessage(updateProgress);
        isPlaying = true;
    }

    private void playUrl(String playUrl,int pcurrentPosition){
        currentPosition = pcurrentPosition;
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(playUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(updateProgress);
        isPlaying = true;
    }

    public static void stop(){
        mediaPlayer.stop();
        isPlaying = false;
    }

    private void playNext(){
        switch (currentMode){
            case MODE_ONE_LOOP:
                play(currentMusic,0);
                break;
            case MODE_ALL_LOOP:
                if (currentMusic + 1 == localSongsList.size()){ //最后一首
                    play(0,0);
                }else {
                    play(currentMusic + 1,0);
                }
                break;
            case MODE_SEQUENCE:
                if (currentMusic + 1 == localSongsList.size()){
                    Toast.makeText(this,"这是最后一首了",Toast.LENGTH_SHORT).show();
                }else {
                    play(currentMusic + 1,0);
                  //  playUrl(, 0);
                  //  getSongPlayUrlBysId(getPlayUrl(localSongsList.get(currentMusic+1).getsId()));
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(),0);
                break;
        }
    }

    private void playPrevious(){
        switch (currentMode){
            case MODE_ONE_LOOP:
                play(currentMusic,0);
                break;
            case MODE_ALL_LOOP:
                if (currentMusic - 1 < 0){
                    play(localSongsList.size() - 1,0);
                }else {
                    play(currentMusic - 1, 0);
                }
                break;
            case MODE_SEQUENCE:
                if (currentMusic - 1 < 0){
                    Toast.makeText(this,"这是第一首歌",Toast.LENGTH_SHORT).show();
                }else {
                    play(currentMusic - 1,0);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(),0);
                break;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    public class MusicBinder extends Binder{

        public void startPlay(int currentMusic,int currentPosition){
            play(currentMusic,currentPosition);
        }

        public void startPlay(String playUrl,int currentPosition){
            playUrl(playUrl,currentPosition);
            changeMode(MODE_ONE_LOOP);
        }

        public void stopPlay(){
            stop();
        }

        public void toPlayNext(){
            playNext();
        }

        public void toPlayPrevious(){
            playPrevious();
        }

        public void changeMode(){
            currentMode = (currentMode + 1) % 4;
            Toast.makeText(MusicService.this,MODE_DESC[currentMode],Toast.LENGTH_SHORT).show();
        }

        public void changeMode(int pCurrentMode){
            currentMode = pCurrentMode;
         //   Toast.makeText(MusicService.this,MODE_DESC[currentMode],Toast.LENGTH_SHORT).show();
        }

        public int getCurrentMode(){
            return  currentMode;
        }

        public boolean isPlaying(){
            return isPlaying;
        }

        /**
         * 更新当前歌曲和时间
         */
        public void notifyActivity(){
            toUpdateCurrentMusic();
            toUpdateDuration();
        }

        /**
         * 更新进度条
         */
        public void changProgress(int progress){
            if (mediaPlayer != null){
                currentPosition = progress * 1000;
                if (isPlaying){
                    mediaPlayer.seekTo(currentPosition);
                }else {
                    play(currentMusic,currentPosition);
                }
            }
        }
    }
}
