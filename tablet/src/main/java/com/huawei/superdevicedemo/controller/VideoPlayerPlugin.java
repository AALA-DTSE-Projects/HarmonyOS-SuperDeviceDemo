/*
 * Copyright (c) 2020 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.superdevicedemo.controller;

import ohos.agp.graphics.Surface;
import ohos.app.Context;
import ohos.media.common.Source;
import ohos.media.common.sessioncore.AVElement;
import ohos.media.player.Player;

/**
 * VideoPlayerPlugin
 *
 * @since 2020-09-14
 */
public class VideoPlayerPlugin implements Player.IPlayerCallback {
    private static final String TAG = VideoPlayerPlugin.class.getSimpleName();

    private static final int REWIND_TIME = 2000;

    private static final int MIL_TO_MICRO = 1000;

    private Player videoPlayer;

    private final Context context;

    private Runnable videoRunnable;

    private final MediaPlayerCallback callback;

    public interface MediaPlayerCallback {
        void onPlayBackComplete();
        void onBuffering(int percent);
    }

    /**
     * VideoPlayerPlugin
     *
     * @param sliceContext Context
     */
    public VideoPlayerPlugin(Context sliceContext, MediaPlayerCallback callback) {
        context = sliceContext;
        this.callback = callback;
    }

    /**
     * start
     */
    public synchronized void startPlay() {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.play();
        LogUtil.info(TAG, "start play");
    }

    /**
     * pause
     */
    public synchronized void pausePlay() {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.pause();
        LogUtil.info(TAG, "pause play");
    }

    /**
     * Set source,prepare,start
     *
     * @param avElement AVElement
     * @param surface Surface
     */
    public synchronized void startPlay(AVElement avElement, Surface surface) {
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.release();
        }

        if (videoRunnable != null) {
            ThreadPoolManager.getInstance().cancel(videoRunnable);
        }

        videoPlayer = new Player(context);
        videoPlayer.setPlayerCallback(this);
        videoRunnable = () -> play(avElement, surface);
        ThreadPoolManager.getInstance().execute(videoRunnable);
    }

    /**
     *  check is playing
     */
    public synchronized boolean isPlaying() {
        if (videoPlayer == null) {
            return false;
        }
        return videoPlayer.isNowPlaying();
    }

    private void play(AVElement avElement, Surface surface) {
        Source source = new Source(avElement.getAVDescription().getMediaUri().toString());
        videoPlayer.setSource(source);
        videoPlayer.setVideoSurface(surface);
        LogUtil.info(TAG, source.getUri());

        videoPlayer.prepare();
    }

    /**
     * seek
     */
    public void seek() {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.rewindTo((videoPlayer.getCurrentTime() + REWIND_TIME) * MIL_TO_MICRO);
        LogUtil.info(TAG, "seek" + videoPlayer.getCurrentTime());
    }

    /**
     * back
     */
    public void back() {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.rewindTo((videoPlayer.getCurrentTime() - REWIND_TIME) * MIL_TO_MICRO);
        LogUtil.info(TAG, "seek" + videoPlayer.getCurrentTime());
    }

    /**
     * release player
     */
    public void release() {
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.release();
        }
    }

    /**
     * Get current play position
     *
     * @return play position
     */
    public int getCurrentPlayPosition() {
        if (videoPlayer == null) {
            return  0;
        }
        return videoPlayer.getCurrentTime();
    }

    @Override
    public void onPrepared() {
        LogUtil.info(TAG, "onPrepared");
        videoPlayer.play();
    }

    @Override
    public void onMessage(int type, int extra) {
        LogUtil.info(TAG, "onMessage" + type);
    }

    @Override
    public void onError(int errorType, int errorCode) {
        LogUtil.error(TAG, "onError" + errorType + ", skip to the next video");
        videoPlayer.stop();
        if (this.callback != null) {
            this.callback.onPlayBackComplete();
        }
    }

    @Override
    public void onResolutionChanged(int width, int height) {
        LogUtil.info(TAG, "onResolutionChanged" + width);
    }

    @Override
    public void onPlayBackComplete() {
        LogUtil.info(TAG, "onPlayBackComplete");
        if (this.callback != null) {
            this.callback.onPlayBackComplete();
        }
    }

    @Override
    public void onRewindToComplete() {
        LogUtil.info(TAG, "onRewindToComplete");
    }

    @Override
    public void onBufferingChange(int percent) {
        LogUtil.info(TAG, "onBufferingChange" + percent);
        if (this.callback != null) {
            this.callback.onBuffering(percent);
        }
    }

    @Override
    public void onNewTimedMetaData(Player.MediaTimedMetaData mediaTimedMetaData) {
        LogUtil.info(TAG, "onNewTimedMetaData" + mediaTimedMetaData.toString());
    }

    @Override
    public void onMediaTimeIncontinuity(Player.MediaTimeInfo mediaTimeInfo) {
        LogUtil.info(TAG, "onNewTimedMetaData" + mediaTimeInfo.toString());
    }

}
