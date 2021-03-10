package com.huawei.superdevicedemo;

import com.huawei.superdevicedemo.controller.LogUtil;
import com.huawei.superdevicedemo.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.IBundleManager;

public class MainAbility extends Ability {
    public static final int REQUEST_CODE = 1;
    private static final String TAG = MainAbility.class.getSimpleName();

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        setWakeUpScreen(true);
        setShowOnLockScreen(true);
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == IBundleManager.PERMISSION_GRANTED) {
                LogUtil.debug(TAG, "Permission granted");
            } else {
                new ToastDialog(this).setText("Permission is required to proceed").show();
            }
        }
    }
}
