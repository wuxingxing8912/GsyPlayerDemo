package cn.com.cunw.gsyplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    MySGYVideoPlayer mGSYVideoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ijk内核，默认模式
        PlayerFactory.setPlayManager(IjkPlayerManager.class);

        mGSYVideoPlayer = findViewById(R.id.my_gsy_player);
        mGSYVideoPlayer.onInitOptions(this, new OnMyPlayerListener() {
            @Override
            public void onAutoComplete() {
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onPlayError() {
            }
        });
        mGSYVideoPlayer.onStartPlay(new HashMap<>(), "测试视频", "https://face.gotime.top/test.mp4");

        String tempDrinkingType = "1,2,3,";
        int index = tempDrinkingType.lastIndexOf(",");
        Log.d(TAG, "index: "+index);
        if (tempDrinkingType.endsWith(",")) {
            tempDrinkingType = tempDrinkingType.substring(0, tempDrinkingType.lastIndexOf(","));
        }
        Log.d(TAG, "tempDrinkingType: "+tempDrinkingType);

    }

    // --------  GSY 相关设置  ---------------
    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        mGSYVideoPlayer.backFromWindowFull();
        mGSYVideoPlayer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
//        mGSYVideoPlayer.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mGSYVideoPlayer.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mGSYVideoPlayer.onConfigurationChanged(newConfig);
    }

}