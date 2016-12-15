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
import android.widget.Toast;

import com.android.volley.Response;
import com.example.yone.iplay.R;
import com.example.yone.iplay.adapter.OnlineSongsAdapter;
import com.example.yone.iplay.fragment.OnlineMusicFragment;
import com.example.yone.iplay.http.GsonRequest;
import com.example.yone.iplay.http.RequestManager;
import com.example.yone.iplay.http.TingAPI;
import com.example.yone.iplay.model.LocalSongs;
import com.example.yone.iplay.model.OnlineSongs;
import com.example.yone.iplay.model.SongsUrl;
import com.example.yone.iplay.utils.MediaUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

/**
 * Created by Yone on 2015/7/9.
 */
public class OnlineMusicService extends Service {

    private MediaPlayer mediaPlayer;

    private MusicService.MusicBinder localmusicBinder;

    public static boolean isPlaying = false;

    private List<OnlineSongs> OnlineSongsList;

    public String playCurrentUrl = "";

    private Binder musicBinder = new MusicBinder();

    private static int currentMusic;//当前播放歌曲

    private int currentPosition;//当前播放到什么位置

    private static final String TAG = "com.example.yone.iplay.service.MusicService";

    private static final int updateProgress = 1; //更新进度条

    private static final int updateCurrentMusic = 2; //更新当前播放歌曲

    private static final int updateDuration = 3; //更新当前播放时间

    public static final String ACTION_UPDATE_PROGRESS = "com.example.yone.iplay.OnlineMusicService.UPDATE_PROGRESS";
    public static final String ACTION_UPDATE_DURATION = "com.example.yone.iplay.OnlineMusicService.UPDATE_DURATION";
    public static final String ACTION_UPDATE_CURRENT_MUSIC = "com.example.yone.iplay.OnlineMusicService.UPDATE_CURRENT_MUSIC";
    public static final String ACTION_UPDATE_CURRENT_PLAYURL = "com.example.yone.iplay.OnlineMusicService.UPDATE_PLAY_URL";

    private int currentMode = 3; //默认播放模式

    public static final String[] MODE_DESC = {  //模式类型
            "单曲播放", "循环播放", "随机播放", "顺序播放"};

    public static final int MODE_ONE_LOOP = 0;//单曲循环
    public static final int MODE_ALL_LOOP = 1;//全部循环
    public static final int MODE_RANDOM = 2; //随机播放
    public static final int MODE_SEQUENCE = 3; //顺序播放

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            localmusicBinder = (MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void connectToMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        this.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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

    private void toUpdateCurrentMusic() {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_CURRENT_MUSIC);
        intent.putExtra(ACTION_UPDATE_CURRENT_MUSIC, currentMusic);
        intent.putExtra(ACTION_UPDATE_CURRENT_PLAYURL, playCurrentUrl);
        sendBroadcast(intent);
    }

    private void toUpdateDuration() {
        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_DURATION);
            intent.putExtra(ACTION_UPDATE_DURATION, duration);
            sendBroadcast(intent);
        }
    }

    private void toUpdateProgress() {
        if (mediaPlayer != null) {
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
        getData(20);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 初始化MediaPlayer
     */
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        connectToMusicService();
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
                if (isPlaying) {
                    switch (currentMode) {
                        case MODE_ONE_LOOP:
                            mediaPlayer.start();
                            break;
                        case MODE_ALL_LOOP:
                            //     playUrl((currentMusic + 1) %OnlineSongsList.size(), 0);
                            break;
                        case MODE_RANDOM:
                            //    playUrl(getRandomPosition(), 0);
                            break;
                        case MODE_SEQUENCE:
                            if (currentMusic < OnlineSongsList.size() - 1) {
                                currentMusic++;
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
    private void setCurrentMusic(int pCurrentMusic) {
        currentMusic = pCurrentMusic;
        handler.sendEmptyMessage(updateCurrentMusic);
    }

    //在歌曲列表长度中随机生成一个位置
    private int getRandomPosition() {
        int random = (int) (Math.random() * (OnlineSongsList.size() - 1));
        return random;
    }

    private void playUrl(String playUrl, int pcurrentPosition, int currentMusic) {
        currentPosition = pcurrentPosition;
        playCurrentUrl = playUrl;
        setCurrentMusic(currentMusic);
        if (localmusicBinder != null) {
            if (localmusicBinder.isPlaying()) {
                localmusicBinder.stopPlay();
            }
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(playUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        handler.sendEmptyMessage(updateProgress);
        isPlaying = true;
    }

    private void playUrl(int currentMusic, int pcurrentPosition) {
        currentPosition = pcurrentPosition;
        setCurrentMusic(currentMusic);
        if (localmusicBinder != null) {
            if (localmusicBinder.isPlaying()) {
                localmusicBinder.stopPlay();
            }
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(playCurrentUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(updateProgress);
        isPlaying = true;
    }

    private void stop() {
        mediaPlayer.stop();
        isPlaying = false;
    }

    private void playNext() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                //      play(currentMusic,0);
                break;
            case MODE_ALL_LOOP:
                if (currentMusic == OnlineSongsList.size()) { //最后一首
                    //        play(0,0);
                } else {
                    //         play(currentMusic + 1,0);
                }
                break;
            case MODE_SEQUENCE:
                if (currentMusic == OnlineSongsList.size()) {
                    Toast.makeText(this, "这是最后一首了", Toast.LENGTH_SHORT).show();
                } else {
                    getSongPlayUrlBysId(OnlineSongsList.get(currentMusic).getsId());
                    //   playUrl(playUrl,currentMusic);
                }
                break;
            case MODE_RANDOM:
                //   play(getRandomPosition(),0);
                break;
        }
    }

    private void playPrevious() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                //     play(currentMusic,0);
                break;
            case MODE_ALL_LOOP:
                if (currentMusic - 1 < 0) {
                    //         play(OnlineSongsList.size() - 1,0);
                } else {
                    //         play(currentMusic - 1, 0);
                }
                break;
            case MODE_SEQUENCE:
                if (currentMusic - 1 < 0) {
                    Toast.makeText(this, "这是第一首歌", Toast.LENGTH_SHORT).show();
                } else {
                    getSongPlayUrlBysId(OnlineSongsList.get(currentMusic - 1).getsId());
                    playUrl(playCurrentUrl, 0, 0);
                }
                break;
            case MODE_RANDOM:
                //    play(getRandomPosition(),0);
                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    public class MusicBinder extends Binder {

        public void startPlay(int currentMusic, int currentPosition) {
            playUrl(currentMusic, currentPosition);
        }

        public void startPlay(String playUrl, int currentPosition, int currentMusic) {
            playUrl(playUrl, currentPosition, currentMusic);
        }

        public void stopPlay() {
            stop();
        }

        public void toPlayNext() {
            playNext();
        }

        public void toPlayPrevious() {
            playPrevious();
        }

        public void changeMode() {
            currentMode = (currentMode + 1) % 4;
            Toast.makeText(OnlineMusicService.this, MODE_DESC[currentMode], Toast.LENGTH_SHORT).show();
        }

        public void changeMode(int pCurrentMode) {
            currentMode = pCurrentMode;
            //   Toast.makeText(MusicService.this,MODE_DESC[currentMode],Toast.LENGTH_SHORT).show();
        }

        public int getCurrentMode() {
            return currentMode;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        /**
         * 更新当前歌曲和时间
         */
        public void notifyActivity() {
            toUpdateCurrentMusic();
            toUpdateDuration();
        }

        /**
         * 更新进度条
         */
        public void changProgress(int progress) {
            if (mediaPlayer != null) {
                currentPosition = progress * 1000;
                if (isPlaying) {
                    mediaPlayer.seekTo(currentPosition);
                } else {
                    getSongPlayUrlBysId(OnlineSongsList.get(currentMusic).getsId());
                    //  playUrl(playUrl,currentMusic);
                    playUrl(playCurrentUrl, currentPosition, currentMusic);
                }
            }
        }
    }

    public void getData(final int songCount) {
        final GsonRequest<List<OnlineSongs>> request = TingAPI.getSongsRequest(songCount);
        final Response.Listener<List<OnlineSongs>> response = new Response.Listener<List<OnlineSongs>>() {
            @Override
            public void onResponse(List<OnlineSongs> onlineSongs) {
                OnlineSongsList = onlineSongs;
            }
        };
        request.setSuccessListener(response);
        RequestManager.addRequest(request, null);
    }

    public void getSongPlayUrlBysId(long s_Id) {
        final GsonRequest<SongsUrl> request = TingAPI.getOnLineSongsRequest(s_Id);
        final Response.Listener<SongsUrl> response = new Response.Listener<SongsUrl>() {
            @Override
            public void onResponse(SongsUrl songsUrl) {
                playCurrentUrl = songsUrl.getSongurl();
                playUrl(playCurrentUrl, 0, currentMusic++);
            }
        };
        request.setSuccessListener(response);
        RequestManager.addRequest(request, s_Id);
    }
}
