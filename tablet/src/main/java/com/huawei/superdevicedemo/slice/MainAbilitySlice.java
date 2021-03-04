package com.huawei.superdevicedemo.slice;

import com.huawei.superdevicedemo.ResourceTable;
import com.huawei.superdevicedemo.controller.LogUtil;
import com.huawei.superdevicedemo.controller.VideoElementManager;
import com.huawei.superdevicedemo.controller.VideoPlayerPlugin;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.surfaceprovider.SurfaceProvider;
import ohos.agp.graphics.Surface;
import ohos.agp.graphics.SurfaceOps;
import ohos.agp.window.service.WindowManager;
import ohos.media.common.sessioncore.AVElement;

import java.util.ArrayList;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice implements SurfaceOps.Callback {
    private static final String TAG = MainAbilitySlice.class.getSimpleName();
    private List<AVElement> avElements = new ArrayList<>();
    private VideoPlayerPlugin videoPlayerPlugin;
    private SurfaceProvider surfaceProvider;
    private Surface surface;
    private Text title;
    private Image playButton;
    private int currentPosition;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        setupUI();
        initData();
    }

    @Override
    public void surfaceCreated(SurfaceOps surfaceOps) {
        if (surfaceProvider.getSurfaceOps().isPresent()) {
            surface = surfaceProvider.getSurfaceOps().get().getSurface();
            LogUtil.info(TAG, "surface set");
        }
        play(currentPosition);
        LogUtil.info(TAG, "surface created");
    }

    @Override
    public void surfaceChanged(SurfaceOps surfaceOps, int i, int i1, int i2) {
        LogUtil.info(TAG, "surface updated with (" + i + "," + i1 + "," + i2 + ")" );
    }

    @Override
    public void surfaceDestroyed(SurfaceOps surfaceOps) {
        LogUtil.info(TAG, "surface destroyed");
    }

    @Override
    public void onStop() {
        videoPlayerPlugin.release();
        super.onStop();
    }

    private void initData() {
        videoPlayerPlugin = new VideoPlayerPlugin(this);
        VideoElementManager videoElementManager = new VideoElementManager(this);
        avElements = videoElementManager.getAvElements();
        currentPosition = 0;
    }

    private void setupUI() {
        surfaceProvider = (SurfaceProvider) findComponentById(ResourceTable.Id_surface_provider);
        surfaceProvider.pinToZTop(false);
        WindowManager.getInstance().getTopWindow().get().setTransparent(true);
        surfaceProvider.getSurfaceOps().get().addCallback(this);

        title = (Text) findComponentById(ResourceTable.Id_title);
        playButton = (Image) findComponentById(ResourceTable.Id_play_button);
        Image forwardButton = (Image) findComponentById(ResourceTable.Id_forward_button);
        Image rewindButton = (Image) findComponentById(ResourceTable.Id_rewind_button);
        playButton.setClickedListener(component -> {
            if (videoPlayerPlugin.isPlaying()) {
                videoPlayerPlugin.pausePlay();
                playButton.setPixelMap(ResourceTable.Media_play_button);
            } else {
                videoPlayerPlugin.startPlay();
                playButton.setPixelMap(ResourceTable.Media_pause_button);
            }
        });
        forwardButton.setClickedListener(component -> play(currentPosition + 1));
        rewindButton.setClickedListener(component -> play(currentPosition - 1));
    }

    private void play(int position) {
        int maxPosition = avElements.size() - 1;
        if (position > maxPosition) {
            position = 0;
        } else if (position < 0) {
            position = maxPosition;
        }
        currentPosition = position;
        AVElement item = avElements.get(position);
        String itemText = item.getAVDescription().getTitle().toString();
        title.setText(itemText);
        videoPlayerPlugin.startPlay(avElements.get(position), surface);
        playButton.setPixelMap(ResourceTable.Media_pause_button);
    }

}
