package com.example.yone.iplay.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.yone.iplay.R;
import com.example.yone.iplay.model.DownFileInfo;
import com.example.yone.iplay.service.DownloadService;

/**
 * Created by Yone on 2015/6/29.
 */
public class DownloadMusicFragment extends Fragment {

    private View view;
    private ImageView downLoadControl,cancelDownLoad;
    private NumberProgressBar progressBar;
    private TextView songTitle;
    public static boolean isDownLoad = false;
    private boolean isPause = true;
    private DownFileInfo mFileInfo;
    private DownloadReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (isDownLoad){
            view = inflater.inflate(R.layout.download_layout,container,false);
            registerReceiver();

            downLoadControl = (ImageView) view.findViewById(R.id.download_control);
            cancelDownLoad = (ImageView) view.findViewById(R.id.delete_download);
            progressBar = (NumberProgressBar) view.findViewById(R.id.progressBar);
            songTitle = (TextView) view.findViewById(R.id.song_title);


            ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
            downLoadControl.setOnClickListener(viewOnClickListener);
            cancelDownLoad.setOnClickListener(viewOnClickListener);
        } else {
        view = inflater.inflate(R.layout.not_download_layout,container,false);
        }
        return view;
    }

    public void stopService(){
        Intent intent = new Intent(view.getContext(),DownloadService.class);
        view.getContext().stopService(intent);
    }

    class ViewOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
             Intent intent = new Intent(view.getContext(),DownloadService.class);
             switch (v.getId()){
                 case R.id.download_control:
                     if (isPause){
                         downLoadControl.setImageResource(R.mipmap.ic_fa_arrow_down);
                         intent.setAction(DownloadService.ACTION_STOP);
                         intent.putExtra("fileInfo",mFileInfo);
                         getActivity().startService(intent);
                         isPause = false;
                     } else {
                         downLoadControl.setImageResource(R.mipmap.ic_fa_pause);
                         intent.setAction(DownloadService.ACTION_START);
                         intent.putExtra("fileInfo", mFileInfo);
                         getActivity().startService(intent);
                         isPause = true;
                     }
                     break;
                 case R.id.delete_download:
                     stopService();
                     intent.setAction(DownloadService.ACTION_DELETE);
                     intent.putExtra("fileInfo", mFileInfo);
                     getActivity().startService(intent);
                     break;
             }
        }
    }

    public void registerReceiver(){
        mReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_DELETE);
        filter.addAction(DownloadService.ACTION_FILEINFO);
        filter.addAction(DownloadService.ACTION_OK);
        getActivity().registerReceiver(mReceiver,filter);
    }

    public class DownloadReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
              String action = intent.getAction();
              if (DownloadService.ACTION_UPDATE.equals(action)){
                  int finished = intent.getIntExtra("finished",0);
                  progressBar.setProgress(finished);
                  mFileInfo = (DownFileInfo) intent.getSerializableExtra("fileInfo");
                  songTitle.setText(mFileInfo.getFileName());
              }else if (DownloadService.ACTION_DELETE.equals(action)){
                  isDownLoad = false;
                  stopService();
                  DownloadMusicFragment downloadMusicFragment = new DownloadMusicFragment();
                  getFragmentManager().beginTransaction().replace(R.id.container,downloadMusicFragment).
                          show(downloadMusicFragment).commit();
              }
        }
    }
}
