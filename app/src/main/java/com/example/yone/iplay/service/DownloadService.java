package com.example.yone.iplay.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.yone.iplay.model.DownFileInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.RandomAccess;

/**
 * Created by Yone on 2015/7/13.
 */
public class DownloadService  extends Service{

    public static final String DOWLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/music/";

    public final static String ACTION_START = "ACTION_START";
    public final static String ACTION_STOP = "ACTION_STOP";
    public final static String ACTION_UPDATE = "ACTION_UPDATE";
    public final static String ACTION_OK = "ACTION_OK";
    public final static String ACTION_DELETE = "ACTION_DELETE";
    public final static String ACTION_FILEINFO = "ACTION_fileInfo";
    public final static int MSG_INIT = 0;
    private DownloadTask mTask = null;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取OnlineMusicFragment传过来的数据
        if (ACTION_START.equals(intent.getAction())){
            Log.d("start","" + intent.getAction());
            DownFileInfo fileInfo = (DownFileInfo) intent.getSerializableExtra("fileInfo");
            //启动初始化线程
            new InitThread(fileInfo).start();
        }else if (ACTION_STOP.equals(intent.getAction())){
            DownFileInfo fileInfo = (DownFileInfo) intent.getSerializableExtra("fileInfo");
           if (mTask != null){
                mTask.isPause = true;
            }
            stopSelf(); //停止服务
        }else if (ACTION_DELETE.equals(intent.getAction())){
            stopSelf();
            DownFileInfo fileInfo = (DownFileInfo) intent.getSerializableExtra("fileInfo");
            mTask = new DownloadTask(DownloadService.this,fileInfo);
            mTask.cancelDownload();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //Service断开连接时回调该方法

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT:
                    DownFileInfo fileInfo = (DownFileInfo) msg.obj;
                    //启动下载任务
                    mTask = new DownloadTask(DownloadService.this,fileInfo);
                    mTask.download();
                    break;
            }
        }
    };

    /**
     * 初始化子线程
     */
    class InitThread extends Thread{
        private DownFileInfo mDownFileInfo = null;

        public InitThread(DownFileInfo mDownFileInfo){
            this.mDownFileInfo = mDownFileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try{

                //连接网络文件
                URL url = new URL(mDownFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000); //连接超时
                conn.setRequestMethod("GET"); //请求方法
                int length = -1;
                if (conn.getResponseCode() == HttpStatus.SC_OK){  //请求成功
                    //获取文件长度
                    length = conn.getContentLength();
                }
                if (length <= 0){
                    return;
                }
                File dir = new File(DOWLOAD_PATH);
                if (!dir.exists()){  //如果文件不存在就创建
                    dir.mkdir();
                }
                //在本地创建文件
                File file = new File(dir,mDownFileInfo.getFileName());
                raf = new RandomAccessFile(file,"rwd"); //对后面的续点下载帮助

                //设置文件长度
                raf.setLength(length);
                mDownFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT,mDownFileInfo).sendToTarget();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    raf.close();
                    conn.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
