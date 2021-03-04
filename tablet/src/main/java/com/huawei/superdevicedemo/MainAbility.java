package com.huawei.superdevicedemo;

import com.huawei.superdevicedemo.controller.LogUtil;
import com.huawei.superdevicedemo.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.PopupDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.IBundleManager;
import ohos.security.SystemPermission;

public class MainAbility extends Ability {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = MainAbility.class.getSimpleName();

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        requestPermission();
        super.setMainRoute(MainAbilitySlice.class.getName());
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == IBundleManager.PERMISSION_GRANTED
                    && grantResults[1] == IBundleManager.PERMISSION_GRANTED) {
                LogUtil.debug(TAG, "Permissions granted");
            } else {
                new ToastDialog(this).setText("Permissions are required to proceed").show();
            }
        }
    }

    private void requestPermission() {
        if (verifyCallingOrSelfPermission(SystemPermission.READ_MEDIA) != IBundleManager.PERMISSION_GRANTED ||
                verifyCallingOrSelfPermission(SystemPermission.MEDIA_LOCATION) != IBundleManager.PERMISSION_GRANTED) {
            requestPermissionsFromUser(new String[] {
                    SystemPermission.READ_MEDIA,
                    SystemPermission.MEDIA_LOCATION },
                    REQUEST_CODE);
        }
    }
}
