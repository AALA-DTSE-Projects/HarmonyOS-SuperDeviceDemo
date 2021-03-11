package com.huawei.superdevicedemo.slice;

import com.huawei.superdevicedemo.MainAbility;
import com.huawei.superdevicedemo.ResourceTable;
import com.huawei.superdevicedemo.controller.Const;
import com.huawei.superdevicedemo.controller.LogUtil;
import com.huawei.superdevicedemo.controller.PlayerRemoteProxy;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Image;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.ElementName;
import ohos.bundle.IBundleManager;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.distributedschedule.interwork.IDeviceStateCallback;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.rpc.IRemoteObject;
import ohos.security.SystemPermission;

import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private static final String TAG = MainAbilitySlice.class.getSimpleName();
    private static final int EVENT_STATE_CHANGE = 10001;
    private DeviceInfo tablet;
    private PlayerRemoteProxy remoteProxy;
    private boolean isPlaying;

    private EventHandler handler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            if (event.eventId == EVENT_STATE_CHANGE) {
                getTabletDevice();
            }
        }
    };

    private IDeviceStateCallback callback = new IDeviceStateCallback() {
        @Override
        public void onDeviceOffline(String deviceId, int deviceType) {
            if (tablet != null && tablet.getDeviceId().equals(deviceId)) {
                showToast("Device offline");
                disconnectAbility(connection);
                tablet = null;
            }
        }

        @Override
        public void onDeviceOnline(String deviceId, int deviceType) {
            handler.sendEvent(EVENT_STATE_CHANGE);
        }
    };

    private IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject remote, int resultCode) {
            remoteProxy = new PlayerRemoteProxy(remote);
            isPlaying = true;
            LogUtil.info(TAG, "ability connect done!");
            setupRemoteButton();
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
            LogUtil.info(TAG, "ability disconnect done!");
            disconnectAbility(connection);
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        requestPermissions(
                SystemPermission.DISTRIBUTED_DATASYNC,
                SystemPermission.GET_BUNDLE_INFO
        );
    }

    @Override
    public void onActive() {
        super.onActive();
        if (tablet == null) {
            getTabletDevice();
            DeviceManager.registerDeviceStateCallback(callback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnectAbility(connection);
        DeviceManager.unregisterDeviceStateCallback(callback);
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

    private void getTabletDevice() {
        List<DeviceInfo> devices = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        if (devices.isEmpty()) {
            showToast("No device found");
        } else {
            devices.forEach(deviceInfo -> {
                LogUtil.info(TAG, "Found device " + deviceInfo.getDeviceType());
                if (deviceInfo.getDeviceType() == DeviceInfo.DeviceType.SMART_PAD &&
                        (tablet == null || !tablet.getDeviceId().equals(deviceInfo.getDeviceId()))) {
                    tablet = deviceInfo;
                    startVideoPlayerOnTablet();
                }
            });
        }
    }

    private void startVideoPlayerOnTablet() {
        String deviceId = tablet.getDeviceId();
        if (deviceId == null) {
            return;
        }
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(deviceId)
                .withBundleName(Const.BUNDLE_NAME)
                .withAbilityName(Const.ABILITY_NAME)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        connectAbility(intent, connection);
        LogUtil.info(TAG, "connect ability on tablet with id " + deviceId );
    }

    private void setupRemoteButton() {
        Image playButton = (Image) findComponentById(ResourceTable.Id_play_button);
        playButton.setClickedListener(component -> {
            isPlaying = !isPlaying;
            remoteProxy.remoteControl(isPlaying ? Const.PLAY : Const.PAUSE);
            playButton.setPixelMap(isPlaying ? ResourceTable.Media_pause_button : ResourceTable.Media_play_button);
        });

        Image forwardButton = (Image) findComponentById(ResourceTable.Id_forward_button);
        forwardButton.setClickedListener(component -> {
            remoteProxy.remoteControl(Const.FORWARD);
        });

        Image rewindButton = (Image) findComponentById(ResourceTable.Id_rewind_button);
        rewindButton.setClickedListener(component -> {
            remoteProxy.remoteControl(Const.REWIND);
        });

        Image nextButton = (Image) findComponentById(ResourceTable.Id_next_button);
        nextButton.setClickedListener(component -> {
            remoteProxy.remoteControl(Const.NEXT);
        });

        Image previousButton = (Image) findComponentById(ResourceTable.Id_previous_button);
        previousButton.setClickedListener(component -> {
            remoteProxy.remoteControl(Const.PREVIOUS);
        });
    }

    private void showToast(String text) {
        LogUtil.info(TAG, text);
        new ToastDialog(this)
                .setText(text)
                .setAutoClosable(false)
                .show();
    }
}
