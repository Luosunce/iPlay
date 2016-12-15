package com.example.yone.iplay.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yone.iplay.R;

/**
 * Created by Yone on 2015/6/29.
 */
public class FavoriteFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View favoriteView = inflater.inflate(R.layout.favorite_layout,container,false);
        return favoriteView;
    }
}
