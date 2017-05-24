package com.jackchan.videoplayer.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jackchan.videoplayer.R;
import com.jackchan.videoplayer.adapter.VideoPlayListAdatper;
import com.jackchan.videoplayer.utils.MediaHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ============================================================
 * Copyright：JackChan和他的朋友们有限公司版权所有 (c) 2017
 * Author：   JackChan
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChan1999
 * GitBook：  https://www.gitbook.com/@alleniverson
 * CSDN博客： http://blog.csdn.net/axi295309066
 * 个人博客： https://jackchan1999.github.io/
 * 微博：     AndroidDeveloper
 * <p>
 * Project_Name：VideoPlayer
 * Package_Name：com.jackchan.videoplayer
 * Version：1.0
 * time：2017/5/24 18:05
 * des ：对应视频播放控制界面的封装
 * gitVersion：2.12.0.windows.1
 * updateAuthor：AllenIverson
 * updateDate：2017/5/24 18:05
 * updateDes：${TODO}
 * ============================================================
 */

public class VideoMediaController extends RelativeLayout {

    private static final String TAG = "VideoMediaController";
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.iv_replay)
    ImageView ivReplay;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.rl_play_finish)
    RelativeLayout rlPlayFinish;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_all_time)
    TextView tvAllTime;
    @BindView(R.id.tv_use_time)
    TextView tvUseTime;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullscreen;
    @BindView(R.id.ll_play_control)
    LinearLayout llPlayControl;

    private boolean hasPause;//是否暂停

    private static final  int MSG_HIDE_TITLE = 0;
    private static final int MSG_UPDATE_TIME_PROGRESS = 1;
    private static final int  MSG_HIDE_CONTROLLER = 2;
    //消息处理器
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_HIDE_TITLE:
                    tvTitle.setVisibility(View.GONE);
                    break;
                case MSG_UPDATE_TIME_PROGRESS:
                    updatePlayTimeAndProgress();
                    break;
                case MSG_HIDE_CONTROLLER:
                    showOrHideVideoController();
                    break;
            }
        }
    };

    public void delayHideTitle(){
        //移除消息
        mHandler.removeMessages(MSG_HIDE_TITLE);
        //发送一个空的延时2秒消息
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_TITLE,2000);
    }

    public VideoMediaController(Context context) {
        this(context, null);
    }

    public VideoMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    //初始化控件
    private void initView() {
        View view = View.inflate(getContext(), R.layout.video_controller, this);
        ButterKnife.bind(this,view);

        initViewDisplay();
        //设置视频播放时的点击界面
        setOnTouchListener(onTouchListener);
        //设置SeekBar的拖动监听
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        //播放完成的界面要销毁触摸事件
        rlPlayFinish.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        //拖动的过程中调用
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        //开始拖动的时候调用
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //暂停视频的播放、停止时间和进度条的更新
            MediaHelper.pause();
            mHandler.removeMessages(MSG_UPDATE_TIME_PROGRESS);
        }

        //停止拖动时调用
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //把视频跳转到对应的位置
            int progress = seekBar.getProgress();
            int duration = myVideoPlayer.mPlayer.getDuration();
            int position = duration * progress / 100;
            myVideoPlayer.mPlayer.seekTo(position);
            //开始播放、开始时间和进度条的更新
            MediaHelper.play();
            updatePlayTimeAndProgress();
        }
    };


    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //按下+已经播放了
            if(event.getAction() == MotionEvent.ACTION_DOWN && myVideoPlayer.hasPlay){
                //显示或者隐藏视频控制界面
                showOrHideVideoController();
            }
            return true;//去处理事件
        }
    };
    //显示或者隐藏视频控制界面
    private void showOrHideVideoController() {
        if(llPlayControl.getVisibility() == View.GONE){
            //显示（标题、播放按钮、视频进度控制）
            tvTitle.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
            //加载动画
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_enter);
            animation.setAnimationListener(new SimpleAnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    llPlayControl.setVisibility(View.VISIBLE);
                    //过2秒后自动隐藏
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER,2000);
                }
            });
            //执行动画
            llPlayControl.startAnimation(animation);
        }else{
            //隐藏（标题、播放按钮、视频进度控制）
            tvTitle.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
            //加载动画
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_exit);
            animation.setAnimationListener(new SimpleAnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    llPlayControl.setVisibility(View.GONE);
                }
            });
            //执行动画
            llPlayControl.startAnimation(animation);
        }
    }

    //更新进度条的第二进度（缓存）
    public void updateSeekBarSecondProgress(int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    //设置播放视频的总时长
    public void setDuration(int duration) {
        String time = formatDuration(duration);
        tvTime.setText(time);
        tvUseTime.setText("00:00");
    }

    //格式化时间 00：00
    public String formatDuration(int duration){
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(duration));
    }

    //更新播放的时间和进度
    public void updatePlayTimeAndProgress() {
        //获取目前播放的进度
        int currentPosition = MediaHelper.getInstance().getCurrentPosition();
        //格式化
        String useTime = formatDuration(currentPosition);
        tvUseTime.setText(useTime);
        //更新进度
        int duration = MediaHelper.getInstance().getDuration();
        if(duration == 0){
            return;
        }
        int progress = 100*currentPosition/duration;
        seekBar.setProgress(progress);
        //发送一个更新的延时消息
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME_PROGRESS,500);
    }

    //移除所有的消息
    public void removeAllMessage(){
        mHandler.removeCallbacksAndMessages(null);
    }

    //显示视频播放完成的界面
    public void showPlayFinishView() {
        tvTitle.setVisibility(View.VISIBLE);
        rlPlayFinish.setVisibility(View.VISIBLE);
        tvAllTime.setVisibility(View.VISIBLE);
    }

    private int position;
    public void setPosition(int position) {
        this.position = position;
    }

    private VideoPlayListAdatper adapter;
    public void setAdapter(VideoPlayListAdatper videoPlayListAdatper) {
        this.adapter = videoPlayListAdatper;
    }

    //简单的动画监听器（不需要其他的监听器去实现多余的方法）
    private class SimpleAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    //初始化控件的显示状态
    public void initViewDisplay() {
        tvTitle.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);
        ivPlay.setImageResource(R.drawable.new_play_video);
        tvAllTime.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        llPlayControl.setVisibility(View.GONE);
        rlPlayFinish.setVisibility(View.GONE);
        tvUseTime.setText("00:00");
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);
    }

    @OnClick({R.id.iv_replay, R.id.iv_share, R.id.iv_play, R.id.iv_fullscreen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_replay:
                //隐藏播放完成界面
                rlPlayFinish.setVisibility(View.GONE);
                //隐藏时间
                tvAllTime.setVisibility(View.GONE);
                tvUseTime.setText("00:00");
                //进度条
                seekBar.setProgress(0);
                //把媒体播放器的位置移动到开始的位置
                MediaHelper.getInstance().seekTo(0);
                //开始播放
                MediaHelper.play();
                //延时隐藏标题
                delayHideTitle();
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_play:

                //点击一个新的条目进行播放
                //点击的条目下标是否是之前播放的条目下标
                if(position != adapter.currentPosition && adapter.currentPosition != -1 ){
                    Log.i(TAG,"点击了其他的条目");

                    //让其他的条目停止播放(还原条目开始的状态)
                    MediaHelper.release();
                    //把播放条目的下标设置给适配器
                    adapter.setPlayPosition(position);
                    //刷新显示
                    adapter.notifyDataSetChanged();
                    //播放
                    ivPlay.setVisibility(View.GONE);
                    tvAllTime.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.VISIBLE);
                    //视频播放界面也需要显示
                    myVideoPlayer.setVideoViewVisiable(View.VISIBLE);
                    ivPlay.setImageResource(R.drawable.new_pause_video);
                    return;
                }

                if(MediaHelper.getInstance().isPlaying()){
                    //暂停
                    MediaHelper.pause();
                    //移除隐藏Controller布局的消息
                    mHandler.removeMessages(MSG_HIDE_CONTROLLER);
                    //移除更新播放时间和进度的消息
                    mHandler.removeMessages(MSG_UPDATE_TIME_PROGRESS);
                    ivPlay.setImageResource(R.drawable.new_play_video);
                    hasPause = true;
                }else{
                    if(hasPause){
                        //继续播放
                        MediaHelper.play();
                        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER,2000);
                        updatePlayTimeAndProgress();
                        hasPause = false;
                    }else{
                        //播放
                        ivPlay.setVisibility(View.GONE);
                        tvAllTime.setVisibility(View.GONE);
                        pbLoading.setVisibility(View.VISIBLE);
                        //视频播放界面也需要显示
                        myVideoPlayer.setVideoViewVisiable(View.VISIBLE);
                        //把播放条目的下标设置给适配器
                        adapter.setPlayPosition(position);
                    }
                    ivPlay.setImageResource(R.drawable.new_pause_video);
                }


                break;
            case R.id.iv_fullscreen:
                break;
        }
    }

    private VideoPlayer myVideoPlayer;
    public void setVideoPlayer(VideoPlayer myVideoPlayer) {
        this.myVideoPlayer = myVideoPlayer;
    }

    //设置视频加载进度条的显示状态
    public void setPbLoadingVisiable(int visiable) {
        pbLoading.setVisibility(visiable);
    }
}
