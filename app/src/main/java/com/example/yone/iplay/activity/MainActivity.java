package com.example.yone.iplay.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yone.iplay.R;
import com.example.yone.iplay.fragment.DownloadMusicFragment;
import com.example.yone.iplay.fragment.FavoriteFragment;
import com.example.yone.iplay.fragment.LocalMusicFragment;
import com.example.yone.iplay.fragment.OnlineMusicFragment;
import com.example.yone.iplay.model.OnlineSongs;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private OnlineSongs onlineSongs;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        NavigationView naviagtionView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (naviagtionView != null){
             setupDrawerContent(naviagtionView);
        }
        LocalMusicFragment localFragment = new LocalMusicFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, localFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_share:
              /*  Intent intentShare = getIntent();
                onlineSongs = intentShare.getParcelableExtra("share");*/

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                if (OnlineMusicFragment.onlineSongs == null || OnlineMusicFragment.playUrl == null){
                    Toast.makeText(this,"请在在线音乐中选择您想要分享的歌曲",Toast.LENGTH_SHORT).show();
                }else {
                    onlineSongs = OnlineMusicFragment.onlineSongs;
                    url = OnlineMusicFragment.playUrl;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, onlineSongs.getTitle() + "by"
                            + onlineSongs.getArtist() + "\n" + url +
                            "分享来自 http://tinger.herokuapp.com/");
                    shareIntent.setType("text/plain");
                    startActivity(shareIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupDrawerContent(final NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.nav_local:
                        LocalMusicFragment localFragment = new LocalMusicFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, localFragment)
                                .addToBackStack(null).commit();
                        break;

                    case R.id.nav_Online:
                        OnlineMusicFragment onlineFragment = new OnlineMusicFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, onlineFragment)
                                .addToBackStack(null).commit();
                        break;

                    case R.id.nav_down:
                        DownloadMusicFragment downloadMusicFragment = new DownloadMusicFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,downloadMusicFragment)
                                .addToBackStack(null).commit();
                        break;

                    case R.id.nav_favorite:
                        FavoriteFragment favoriteFragment = new FavoriteFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,favoriteFragment)
                                .addToBackStack(null).commit();
                        break;

                    default:
                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
