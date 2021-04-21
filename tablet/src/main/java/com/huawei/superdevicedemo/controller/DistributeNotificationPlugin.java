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

import ohos.aafwk.content.Intent;
import ohos.event.commonevent.*;
import ohos.rpc.RemoteException;

/**
 * Plugin class
 *
 * @since 2020-07-02
 */
public class DistributeNotificationPlugin {
    private static final String TAG = "DistributeNotificationPlugin";
    private static final String NOTIFICATION_ACTION = "com.huawei.superdevicedemo";
    private static final String NOTIFICATION_KEY = "notification_key";
    private CommonEventSubscriber commonEventSubscriber;
    private DistributeNotificationEventListener eventListener;
    private static DistributeNotificationPlugin instance;

    public static synchronized DistributeNotificationPlugin getInstance() {
        if (instance == null) {
            synchronized (DistributeNotificationPlugin.class) {
                if (instance == null) {
                    instance = new DistributeNotificationPlugin();
                }
            }
        }
        return instance;
    }

    /**
     * publish CommonEvent
     */
    public void publishEvent(String event) {
        LogUtil.info(TAG, "publish CommonEvent begin");
        Intent intent = new Intent();
        intent.setAction(NOTIFICATION_ACTION);
        intent.setParam(NOTIFICATION_KEY, event);
        CommonEventData eventData = new CommonEventData(intent);
        try {
            CommonEventManager.publishCommonEvent(eventData);
            LogUtil.info(TAG, "the action of Intent is:" + NOTIFICATION_ACTION);
            if (eventListener != null) {
                eventListener.onEventPublish("CommonEvent Publish Success");
            }
        } catch (RemoteException e) {
            LogUtil.error(TAG, "CommonEvent publish Error!");
        }
    }

    /**
     * CommonEvent Subscribe
     */
    public void subscribeEvent() {
        LogUtil.info(TAG, "CommonEvent onSubscribe begin.");
        MatchingSkills skills = new MatchingSkills();
        skills.addEvent(NOTIFICATION_ACTION);
        skills.addEvent(CommonEventSupport.COMMON_EVENT_SCREEN_ON);

        CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(skills);
        commonEventSubscriber = new CommonEventSubscriber(subscribeInfo) {
            @Override
            public void onReceiveEvent(CommonEventData commonEventData) {
                LogUtil.info(TAG, "CommonEventData onReceiveEvent begin");
                if (commonEventData == null) {
                    LogUtil.info(TAG, "commonEventData is null.");
                    return;
                }
                Intent intent = commonEventData.getIntent();
                if (intent == null) {
                    LogUtil.debug(TAG, "commonEventData getIntent is null.");
                    return;
                }
                String receivedAction = intent.getAction();
                LogUtil.info(TAG, "onReceiveEvent action:" + receivedAction);
                if (receivedAction.equals(NOTIFICATION_ACTION)) {
                    String notificationContent = intent.getStringParam(NOTIFICATION_KEY);
                    if (eventListener != null) {
                        eventListener.onEventReceive(notificationContent);
                    }
                }
            }
        };

        LogUtil.info(TAG, "CommonEventManager subscribeCommonEvent begin.");
        try {
            CommonEventManager.subscribeCommonEvent(commonEventSubscriber);
            if (eventListener != null) {
                eventListener.onEventSubscribe("CommonEvent Subscribe Success");
            }
        } catch (RemoteException exception) {
            LogUtil.error(TAG, "CommonEvent Subscribe Error!");
        }
    }

    /**
     * CommonEvent Unsubscribe
     */
    public void unsubscribeEvent() {
        LogUtil.info(TAG, "CommonEvent onUnsubscribe begin.");
        if (commonEventSubscriber == null) {
            LogUtil.info(TAG, "CommonEvent onUnsubscribe commonEventSubscriber is null");
            return;
        }
        try {
            CommonEventManager.unsubscribeCommonEvent(commonEventSubscriber);
            if (eventListener != null) {
                eventListener.onEventUnsubscribe("CommonEvent Unsubscribe Success");
            }
        } catch (RemoteException exception) {
            LogUtil.error(TAG, "CommonEvent Unsubscribe Error!");
        }
        commonEventSubscriber = null;
    }

    /**
     * interface
     *
     * @since 2020-07-02
     */
    public interface DistributeNotificationEventListener {
        void onEventPublish(String result);

        void onEventSubscribe(String result);

        void onEventUnsubscribe(String result);

        void onEventReceive(String result);
    }

    public void setEventListener(DistributeNotificationEventListener eventListener) {
        this.eventListener = eventListener;
    }
}
