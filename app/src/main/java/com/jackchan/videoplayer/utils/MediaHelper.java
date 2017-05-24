package com.jackchan.videoplayer.utils;

import android.media.MediaPlayer;

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
 * des ：多媒体的工具类
 * gitVersion：2.12.0.windows.1
 * updateAuthor：AllenIverson
 * updateDate：2017/5/24 18:05
 * updateDes：${TODO}
 * ============================================================
 */
public final class MediaHelper {

    private MediaHelper() {
    }

    private static MediaPlayer mPlayer;

    //获取多媒体对象
    public static MediaPlayer getInstance(){
        if(mPlayer == null){
            synchronized (MediaHelper.class){
                if (mPlayer == null){
                    mPlayer = new MediaPlayer();
                }
            }
        }
        return  mPlayer;
    }

    //播放
    public static void play(){
        if(mPlayer != null){
            mPlayer.start();
        }
    }

    //暂停
    public static void pause(){
        if(mPlayer != null){
            mPlayer.pause();
        }
    }

    //释放
    public static void release(){
        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
    }

}
