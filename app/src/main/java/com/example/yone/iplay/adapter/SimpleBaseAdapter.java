package com.example.yone.iplay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yone.iplay.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jun on 2015/5/18.
 */
public abstract class SimpleBaseAdapter<T> extends BaseAdapter {

    public Context mContext;
    public LayoutInflater mInflater;
    public List<T> mData;
    public int layoutId;

    public SimpleBaseAdapter(Context context, int layoutId, List<T> data){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
        this.mData = data;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
     public View getView(int position, View convertView, ViewGroup parent){


        ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
                layoutId, position);

        getItemView(holder, getItem(position));
        return holder.getConvertView();
    }
    public abstract void getItemView(ViewHolder holder, T t);


    public void addAll(List<T> elem) {
        mData.addAll(elem);
        notifyDataSetChanged();
    }

    public void add(T elem) {
        mData.add(elem);
        notifyDataSetChanged();
    }

    public void remove(T elem) {
        mData.remove(elem);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mData.remove(index);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> elem) {
        mData.clear();
        if (elem != null) {
            mData.addAll(elem);
        }
        notifyDataSetChanged();
    }


    /**
     * ͨ
     * ��ViewHolder��
     */
    public static class ViewHolder{
        private  SparseArray<View> mViews;
        private int position;
        private  View mConvertView;
        private Context mContext;

        public ViewHolder(Context context,ViewGroup parent,int layoutId,int position){
            this.mContext = context;
            this.position = position;
            this.mViews = new SparseArray<View>();
            mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
            mConvertView.setTag(this);
        }

        public static ViewHolder get(Context context,View convertView,
                                     ViewGroup parent,int layoutId,int position){
            if(convertView == null){
                return  new ViewHolder(context,parent,layoutId,position);
            }else{
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.position = position;
                return  holder;
            }
        }

        public View getConvertView(){
            return mConvertView;
        }

        /**
         * ͨ��viewId��ȡ�ؼ�
         * @param viewId
         * @param <T>
         * @return
         */
        public  <T extends View> T getView(int viewId){
            View view = mViews.get(viewId);

            if(view == null){
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId,view);
            }
            return (T) view;
        }

        /**
         * ��TextVIew�ؼ���ֵ
         * @param viewId �ؼ�Id
         * @param text ֵ
         * @return
         */
        public ViewHolder setText(int viewId,String text){
            TextView tv = getView(viewId);
            tv.setText(text);
            return this;
        }

        /**
         * ��ImageView�ؼ���ֵ
         * @param viewId �ؼ�Id
         * @param resourceId ��ԴId
         * @return
         */
        public ViewHolder setImageResource(int viewId,int resourceId){
            ImageView imageView = getView(viewId);
            imageView.setImageResource(resourceId);
            return  this;
        }

        /**
         * ��ImageView�ؼ���ֵ
         * @param viewId �ؼ�Id
         * @param bitmap ͼ��λͼ
         * @return
         */
        public ViewHolder setImageBitmap(int viewId,Bitmap bitmap){
            ImageView imageView = getView(viewId);
            imageView.setImageBitmap(bitmap);
            return  this;
        }

        /**
         * ��ImageView�ؼ���ֵ
         * @param viewId �ؼ�Id
         * @param url ͼƬ���ӵ�ַ
         * @return
         */
        public ViewHolder setImageURL(int viewId,String url){
            ImageView imageView = getView(viewId);
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.music_listen)
                    .error(R.mipmap.header)
                    .centerCrop()
                    .resize(100,100)
                    .into(imageView);

            return this;
        }

    }
}


