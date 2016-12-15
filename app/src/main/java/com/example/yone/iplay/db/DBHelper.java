package com.example.yone.iplay.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.ref.PhantomReference;

/**
 * Created by Yone on 2015/7/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME="music.db";
    private final static int VERSION = 1;
    private final static String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement," +
            "thread_id integer,url text,start integer,end integer,finished integer)";
    private final static String SQL_DROP = "drop table if exists thread_info";

    public DBHelper(Context context) {
        super(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL(SQL_CREATE);
         db.execSQL(SQL_DROP);
    }
}
