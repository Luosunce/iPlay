package com.example.yone.iplay.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.example.yone.iplay.R;
import com.example.yone.iplay.model.LocalSongs;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yone on 2015/6/30.
 */
public class MediaUtils {

    private static  final Uri albumArtUri = Uri.
            parse("content://media/external/audio/albumart");

    /**
     * 利用内容提供器查询本地音乐数据，保存到List集合中
     */
    public static List<LocalSongs> getLocalSongsInfo(Context context){
        List<LocalSongs> localSongsList = new ArrayList<LocalSongs>();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );

        for (int i = 0; i < cursor.getCount();i++){
             cursor.moveToNext();
             LocalSongs localSongs = new LocalSongs();
             int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); //是否为音乐
             long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); //歌曲Id
             String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)); //歌曲名
             String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)); //歌手
             int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)); //专辑ID
             String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); //专辑
             long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)); //歌曲时长
             String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); //歌曲路径
             int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)); //歌曲大小

             if (isMusic != 0 && duration/(1000 * 60) > 1){  //只把大于一分钟的音乐添加到集合中
                 localSongs.setId(id);
                 localSongs.setTitle(title);
                 if (artist.equals("<unknown>")){
                     localSongs.setArtist("未知");
                 }else {
                     localSongs.setArtist(artist);
                 }
                 localSongs.setAlbumId(albumId);
                 if (album.equals("<unknown>")){
                     localSongs.setAlbum("未知");
                 }else {
                     localSongs.setAlbum(album);
                 }
                 localSongs.setDuration(duration);
                 localSongs.setUrl(url);
                 localSongs.setSize(size);
                 localSongsList.add(localSongs);
             }
        }
        return localSongsList;
    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     */
    public static String formatime(long time){
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2){
            min = "0" + time / (1000 * 60) + "";
        }else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4){
            sec = "0" + (time % (1000 * 60)) + "";
        }else if (sec.length() == 3){
            sec = "00" + (time % (1000 * 60)) + "";
        }else if (sec.length() == 2){
            sec = "000" + (time % (1000 * 60)) + "";
        }else if (sec.length() == 1){
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0,2);
    }

    /**
     * 获取默认专辑图片
     * @param context
     * @return
     */
    public static Bitmap getDefaultArtwork(Context context,boolean small) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        if(small){  //返回小图片
            return BitmapFactory.decodeStream(context.getResources().
                    openRawResource(R.raw.music_listen), null, opts);
        }
        return BitmapFactory.decodeStream(context.getResources().
                openRawResource(R.raw.music_listen), null, opts);
    }


    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private static Bitmap getArtworkFromFile(Context context, long songId, long albumId){
        Bitmap bm = null;
        if(albumId < 0 && songId < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumId < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songId + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            // 只进行大小判断
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100;
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取专辑封面位图对象
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small){
        if(album_id < 0) {
            if(song_id < 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if(bm != null) {
                    return bm;
                }
            }
            if(allowdefalut) {
                return getDefaultArtwork(context, small);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        if(uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //先制定原始大小
                options.inSampleSize = 4;
                //只进行大小判断
                options.inJustDecodeBounds = true;
                //调用此方法得到options得到图片的大小
                BitmapFactory.decodeStream(in, null, options);
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
                if(small){
                    options.inSampleSize = computeSampleSize(options, 40);
                } else{
                    options.inSampleSize = computeSampleSize(options, 600);
                }
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);
            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if(bm != null) {
                    if(bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if(bm == null && allowdefalut) {
                            return getDefaultArtwork(context, small);
                        }
                    }
                } else if(allowdefalut) {
                    bm = getDefaultArtwork(context, small);
                }
                return bm;
            } finally {
                try {
                    if(in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 对图片进行合适的缩放
     * @param options
     * @param target
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if(candidate == 0) {
            return 1;
        }
        if(candidate > 1) {
            if((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if(candidate > 1) {
            if((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }

}
