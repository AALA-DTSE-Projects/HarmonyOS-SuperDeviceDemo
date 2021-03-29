package com.huawei.superdevicedemo;

import com.huawei.superdevicedemo.controller.PlayerRemote;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.rpc.IRemoteObject;

public class VideoPlayerServiceAbility extends Ability {
    private final PlayerRemote remote = new PlayerRemote(this);

    @Override
    public IRemoteObject onConnect(Intent intent) {
        super.onConnect(intent);
        return remote.asObject();
    }
}