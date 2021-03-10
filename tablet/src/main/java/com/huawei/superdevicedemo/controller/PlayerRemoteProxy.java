package com.huawei.superdevicedemo.controller;

import ohos.rpc.*;

public class PlayerRemoteProxy implements IRemoteBroker {
    private final String TAG = PlayerRemoteProxy.class.getSimpleName();
    private final IRemoteObject remote;

    public PlayerRemoteProxy(IRemoteObject remote) {
        this.remote = remote;
    }

    @Override
    public IRemoteObject asObject() {
        return remote;
    }

    public void remoteControl(String action) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        data.writeString(action);
        try {
            remote.sendRequest(PlayerRemote.REMOTE_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "remote action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }
}
