package cn.com.cunw.gsyplayerdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by YangBin on 2022/1/23 10:07
 * desc:
 */
public class MySGYVideoPlayer extends StandardGSYVideoPlayer {

    private static final String TAG = MySGYVideoPlayer.class.getSimpleName();

    private Activity mActivity;
    private OnMyPlayerListener mOnMyCallBack;
    private OrientationUtils mOrientationUtils;
    private boolean isPlay, isPause;

    public MySGYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MySGYVideoPlayer(Context context) {
        super(context);
    }

    public MySGYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onInitOptions(Activity activity, OnMyPlayerListener onMyCallBack) {
        mActivity = activity;
        mOnMyCallBack = onMyCallBack;
        initVideo();
        initGSYVideoOptionBuilder();
    }

    /**
     * 初始化播放器
     */
    private void initGSYVideoOptionBuilder() {
        // 需要初始化
        GSYVideoOptionBuilder gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
        gsyVideoOptionBuilder
                .setShowFullAnimation(false)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setHideKey(true)// 全屏隐藏虚拟按键，默认打开
                .setIsTouchWiget(true)// 是否可以滑动界面改变进度，声音等
                .setIsTouchWigetFull(true)
                .setNeedShowWifiTip(true)// 是否需要显示流量提示,默认true
                .setEnlargeImageRes(R.drawable.ic_album_video_full_enlarge)// 设置右下角 显示切换到全屏 的按键资源 必须在setUp之前设置 不设置使用默认
                .setShrinkImageRes(R.drawable.ic_album_video_full_shrink)// 设置右下角 显示退出全屏 的按键资源 必须在setUp之前设置 不设置使用默认
                .setCacheWithPlay(false)// 是否边缓存，m3u8等无效
                .setThumbPlay(true)// 是否点击封面可以播放
                .setIsTouchWiget(true)
                .setVideoAllCallBack(mGSYSampleCallBack)
                .build(this);

        // 为什么拖动视屏会弹回来，因为ijk的FFMPEG对关键帧问题。
        // 可以尝试以下设置
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        // http重定向到rtmp/Https，ijkplayer无法播放视频
        VideoOptionModel videoOptionModel2 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        list.add(videoOptionModel2);
        GSYVideoManager.instance().setOptionModelList(list);
    }

    private void initVideo() {
        mBackButton.setVisibility(View.GONE);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mTitleTextView.setTextColor(Color.WHITE);

        mOrientationUtils = new OrientationUtils(mActivity, this);
        mOrientationUtils.setEnable(false);

        if (mFullscreenButton != null) {
            mFullscreenButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    showFull();
                }
            });
        }
    }

    public void setFullscreenButtonGone(boolean gone) {
        mFullscreenButton.setVisibility(gone ? GONE : VISIBLE);
    }

    private boolean mIsFull;

    private GSYSampleCallBack mGSYSampleCallBack = new GSYSampleCallBack() {
        @Override
        public void onPrepared(String url, Object... objects) {
            super.onPrepared(url, objects);
            if (mOrientationUtils != null) {
                mOrientationUtils.setEnable(true);
                isPlay = true;
            }
        }

        @Override
        public void onQuitFullscreen(String url, Object... objects) {
            super.onQuitFullscreen(url, objects);
            mIsFull = false;
            if (mOrientationUtils != null) {
                mOrientationUtils.backToProtVideo();
            }
        }

        @Override
        public void onEnterFullscreen(String url, Object... objects) {
            super.onEnterFullscreen(url, objects);
            mIsFull = true;
        }

        @Override
        public void onAutoComplete(String url, Object... objects) {
            super.onAutoComplete(url, objects);
            if (mIsFull) return;// todo 全屏模式不知道进入下一个视频，暂时有问题，待解决
            if (mOnMyCallBack != null) {
                mOnMyCallBack.onAutoComplete();
            }
        }

        @Override
        public void onPlayError(String url, Object... objects) {
            super.onPlayError(url, objects);
            Log.d(TAG, "onPlayError: url:" + url);
            Log.d(TAG, "onPlayError: objects:" + objects);
        }
    };

    private void showFull() {
        if (mOrientationUtils != null && mOrientationUtils.getIsLand() != 1) {
            mOrientationUtils.resolveByClick();
        }

        this.startWindowFullscreen(getContext(), true, true);
    }

    /**
     * 退出全屏
     *
     * @return
     */
    public boolean backFromWindowFull() {
        if (mOrientationUtils != null) {
            this.mOrientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(getContext())) {
            return true;
        }
        return false;
    }

    /**
     * 开始播放
     *
     * @param header
     * @param title
     * @param url
     */
    public void onStartPlay(HashMap<String, String> header, String title, String url) {
        Log.d(TAG, "onStartPlay: ");
        setUp(url, true, null, header, title);
        startPlayLogic();
    }

    public void onPause() {
        Log.d(TAG, "onPause: ");
        this.getCurrentPlayer().onVideoPause();
        this.isPause = true;
    }

    public void onResume() {
        Log.d(TAG, "onResume: ");
        this.getCurrentPlayer().onVideoResume();
        this.isPause = false;
    }

    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        backFromWindowFull();
        try {
            if (this.isPlay) {
                this.getCurrentPlayer().release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (this.mOrientationUtils != null) {
                this.mOrientationUtils.releaseListener();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.isPlay && !this.isPause) {
            super.onConfigurationChanged(mActivity, newConfig, this.mOrientationUtils, true, true);
        }
    }
//
//    @Override
//    protected void updateStartImage() {
//        if (mStartButton instanceof ImageView) {
//            ImageView imageView = (ImageView) mStartButton;
//            if (mCurrentState == CURRENT_STATE_PLAYING) {
//                imageView.setImageResource(R.drawable.ic_player_pause);
//            } else if (mCurrentState == CURRENT_STATE_ERROR) {
//                Log.e(TAG, "视频onClickStop==1111111111111");
//                imageView.setImageResource(R.drawable.ic_player_start);
//                if (mOnMyCallBack != null) {
//                    mOnMyCallBack.onPause();
//                }
//            } else {
//                Log.e(TAG, "视频onClickStop==22222222222222");
//                imageView.setImageResource(R.drawable.ic_player_start);
//                if (mOnMyCallBack != null) {
//                    mOnMyCallBack.onPause();
//                }
//            }
//        }
//    }
}