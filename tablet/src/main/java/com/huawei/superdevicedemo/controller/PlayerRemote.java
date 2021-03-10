package com.huawei.superdevicedemo.controller;

import ohos.rpc.*;

public class PlayerRemote extends RemoteObject implements IRemoteBroker {
    static final int REMOTE_COMMAND = 0;
    private DistributeNotificationPlugin distributeNotificationPlugin;

    public PlayerRemote() {
        super("Player Remote");
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
            distributeNotificationPlugin.publishEvent(command);
            return true;
        }
        return false;
    }
}
