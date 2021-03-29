package com.huawei.superdevicedemo.controller;

import com.huawei.superdevicedemo.MainAbility;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.rpc.*;

public class PlayerRemote extends RemoteObject implements IRemoteBroker {
    static final int REMOTE_COMMAND = 0;
    private final Ability ability;
    private final DistributeNotificationPlugin distributeNotificationPlugin;

    public PlayerRemote(Ability ability) {
        super("Player Remote");
        this.ability = ability;
        distributeNotificationPlugin = DistributeNotificationPlugin.getInstance();
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
        if (code == REMOTE_COMMAND) {
            String command = data.readString();
            if (Const.START.equals(command)) {
                startVideoPlayer();
            } else {
                distributeNotificationPlugin.publishEvent(command);
            }
            return true;
        }
        return false;
    }

    private void startVideoPlayer() {
        Intent intent = new Intent();
        Operation operation =
                new Intent.OperationBuilder()
                        .withBundleName(ability.getBundleName())
                        .withAbilityName(MainAbility.class.getName())
                        .withAction(Const.VIDEO_PLAY_ACTION)
                        .build();
        intent.setOperation(operation);
        ability.startAbility(intent);
    }
}
