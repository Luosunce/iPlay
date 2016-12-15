package com.example.yone.iplay.utils;

import android.app.Application;

/**
 * Created by Yone on 2015/7/3.
 */
public class Ting extends Application{

    private static Ting mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Ting getInstance(){
        return mContext;
    }
}
