package com.huawei.superdevicedemo.slice;

import com.huawei.superdevicedemo.MainAbility;
import com.huawei.superdevicedemo.ResourceTable;
import com.huawei.superdevicedemo.controller.*;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.RoundProgressBar;
import ohos.agp.components.Text;
import ohos.agp.components.surfaceprovider.SurfaceProvider;
import ohos.agp.graphics.Surface;
import ohos.agp.graphics.SurfaceOps;
import ohos.agp.window.service.WindowManager;
import ohos.bundle.IBundleManager;
import ohos.media.common.sessioncore.AVElement;
import ohos.security.SystemPermission;

import java.util.ArrayList;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice implements SurfaceOps.Callback, DistributeNotificationPlugin.DistributeNotificationEvenListener {
    private static final String TAG = MainAbilitySlice.class.getSimpleName();
    private List<AVElement> avElements = new ArrayList<>();
    private VideoPlayerPlugin videoPlayerPlugin;
    private SurfaceProvider surfaceProvider;
    private Surface surface;
    private Text title;
    private Image playButton;
    private RoundProgressBar progressBar;
    private int currentPosition;
    private DistributeNotificationPlugin distributeNotificationPlugin;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        requestPermissions(
                SystemPermission.READ_MEDIA,
                SystemPermission.MEDIA_LOCATION
        );
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
    public void onEventPublish(String result) {
        LogUtil.info(TAG, result);
    }

    @Override
    public void onEventSubscribe(String result) {
        LogUtil.info(TAG, result);
    }

    @Override
    public void onEventUnsubscribe(String result) {
        LogUtil.info(TAG, result);
    }

    @Override
    public void onEventReceive(String result) {
        LogUtil.info(TAG, result);
        switch (result) {
            case Const.PLAY:
                play();
                break;
            case Const.PAUSE:
                pause();
                break;
            case Const.FORWARD:
                play(currentPosition + 1);
                break;
            case Const.REWIND:
                play(currentPosition - 1);
                break;
            case Const.NEXT:
                videoPlayerPlugin.seek();
                break;
            case Const.PREVIOUS:
                videoPlayerPlugin.back();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        videoPlayerPlugin.release();
        distributeNotificationPlugin.unsubscribeEvent();
        super.onStop();
    }

    private void requestPermissions(String... permissions) {
        for (String permission : permissions) {
            if (verifyCallingOrSelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
                requestPermissionsFromUser(
                        new String[] {
                                permission
                        },
                        MainAbility.REQUEST_CODE);
            }
        }
    }

    private void initData() {
        videoPlayerPlugin = new VideoPlayerPlugin(this, new VideoPlayerPlugin.MediaPlayerCallback() {
            @Override
            public void onPlayBackComplete() {
                getUITaskDispatcher().asyncDispatch(() -> play(currentPosition + 1));
            }

            @Override
            public void onBuffering(int percent) {
                getUITaskDispatcher().asyncDispatch(() -> {
                    if (percent == 100) {
                        progressBar.setVisibility(Component.HIDE);
                    } else {
                        progressBar.setVisibility(Component.VISIBLE);
                        progressBar.setProgressValue(percent);
                    }
                });
            }
        });
        VideoElementManager videoElementManager = new VideoElementManager(this);
        avElements = videoElementManager.getAvElements();
        currentPosition = 0;
        distributeNotificationPlugin = DistributeNotificationPlugin.getInstance();
        distributeNotificationPlugin.setEventListener(this);
        distributeNotificationPlugin.subscribeEvent();
    }

    private void setupUI() {
        surfaceProvider = (SurfaceProvider) findComponentById(ResourceTable.Id_surface_provider);
        surfaceProvider.pinToZTop(false);
        WindowManager.getInstance().getTopWindow().get().setTransparent(true);
        surfaceProvider.getSurfaceOps().get().addCallback(this);

        title = (Text) findComponentById(ResourceTable.Id_title);
        progressBar = (RoundProgressBar) findComponentById(ResourceTable.Id_round_progress_bar);
        playButton = (Image) findComponentById(ResourceTable.Id_play_button);
        Image forwardButton = (Image) findComponentById(ResourceTable.Id_forward_button);
        Image rewindButton = (Image) findComponentById(ResourceTable.Id_rewind_button);
        playButton.setClickedListener(component -> {
            if (videoPlayerPlugin.isPlaying()) {
                pause();
            } else {
                play();
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
        playButton.setPixelMap(ResourceTable.Media_pause_button);
        videoPlayerPlugin.startPlay(avElements.get(position), surface);
    }

    private void play() {
        videoPlayerPlugin.startPlay();
        playButton.setPixelMap(ResourceTable.Media_pause_button);
    }

    private void pause() {
        videoPlayerPlugin.pausePlay();
        playButton.setPixelMap(ResourceTable.Media_play_button);
    }
}
