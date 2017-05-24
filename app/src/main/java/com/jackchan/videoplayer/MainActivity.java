package com.jackchan.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jackchan.videoplayer.adapter.VideoPlayListAdatper;
import com.jackchan.videoplayer.bean.VideoPlayerItemInfo;
import com.jackchan.videoplayer.utils.MediaHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
 * des ：TextureView+MediaPlayer在线短视频播放
 * gitVersion：2.12.0.windows.1
 * updateAuthor：AllenIverson
 * updateDate：2017/5/24 18:05
 * updateDes：${TODO}
 * ============================================================
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv)
    RecyclerView rv;

    private List<VideoPlayerItemInfo> videoPlayerItemInfoList;
    private LinearLayoutManager       lm;
    private VideoPlayListAdatper      adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initData();
        initRecyclerView();
    }

    private void initRecyclerView() {
        //初始化RecyclerView
        lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);

        // 添加分割线
        // rv.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.HORIZONTAL,1, Color.BLACK));

        adapter = new VideoPlayListAdatper(this, videoPlayerItemInfoList);
        rv.setAdapter(adapter);
        //设置滑动监听
        rv.addOnScrollListener(onScrollListener);
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void initData() {
        //网络视频路径
        String url = "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4";

        //数据的初始化
        videoPlayerItemInfoList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            videoPlayerItemInfoList.add(new VideoPlayerItemInfo(i,url));
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        //进行滑动
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //获取屏幕上显示的第一个条目和最后一个条目的下标
            int firstVisibleItemPosition = lm.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
            //获取播放条目的下标
            int currentPosition = adapter.currentPosition;
            if((firstVisibleItemPosition > currentPosition || lastVisibleItemPosition < currentPosition) && currentPosition > -1){
                //让播放隐藏的条目停止
                MediaHelper.release();
                adapter.currentPosition = -1;
                adapter.notifyDataSetChanged();
            }
        }
    };

}
