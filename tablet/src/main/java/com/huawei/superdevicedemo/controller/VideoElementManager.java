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

import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.Context;
import ohos.data.resultset.ResultSet;
import ohos.media.common.AVDescription;
import ohos.media.common.sessioncore.AVElement;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to prepare audio files for applications.
 *
 * @since 2020-09-11
 */
public class VideoElementManager {
    private static final String TAG = VideoElementManager.class.getSimpleName();
    private static final String WEB_VIDEO_PATH = "https://ss0.bdstatic.com/-0U0bnSm1A5BphGlnYG/cae-legoup-video-target/93be3d88-9fc2-4fbd-bd14-833bca731ca7.mp4";
    private static final String HARMONY_VIDEO_PATH_1 = "https://mos-vod-drcn.dbankcdn.cn/P_VT/video_injection/AD1346286/v3/1AD16FA31131002495678550784/MP4Mix_H.264_1920x1080_6000_HEAAC1_PVC_NoCut.mp4";
    private static final String HARMONY_VIDEO_PATH_2 = "https://mos-vod-drcn.dbankcdn.cn/P_VT/video_injection/4A1346286/v3/65F9A8A51131004011894281344/MP4Mix_H.264_1920x1080_6000_HEAAC1_PVC_NoCut.mp4";
    private final List<AVElement> avElements = new ArrayList<>();

    /**
     * The construction method of this class
     *
     * @param context Context
     */
    public VideoElementManager(Context context) {
        loadFromMediaLibrary(context);
    }

    /**
     * get the list of videoElements
     *
     * @return videoElements the list of videoElements
     */
    public List<AVElement> getAvElements() {
        return avElements;
    }

    private void loadFromMediaLibrary(Context context) {
        Uri remoteUri = AVStorage.Video.Media.EXTERNAL_DATA_ABILITY_URI;
        DataAbilityHelper helper = DataAbilityHelper.creator(context, remoteUri, false);
        try {
            ResultSet resultSet = helper.query(remoteUri, null, null);
            LogUtil.info(TAG, "The result size: " + resultSet.getRowCount());
            processResult(resultSet);
            resultSet.close();
        } catch (DataAbilityRemoteException e) {
            LogUtil.error(TAG, "Query system media failed.");
        } finally {
            helper.release();
        }
    }

    private void processResult(ResultSet resultSet) {
        while (resultSet.goToNextRow()) {
            String path = resultSet.getString(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.DATA));
            String title = resultSet.getString(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.TITLE));
            AVDescription bean =
                    new AVDescription.Builder().setTitle(title).setIMediaUri(Uri.parse(path)).setMediaId(path).build();
            avElements.add(new AVElement(bean, AVElement.AVELEMENT_FLAG_PLAYABLE));
        }
        writeDefault();
    }

    private void writeDefault() {
        avElements.add(
                new AVElement(new AVDescription.Builder()
                        .setTitle("web_video")
                        .setIMediaUri(Uri.parse(WEB_VIDEO_PATH))
                        .setMediaId(WEB_VIDEO_PATH)
                        .build(),
                AVElement.AVELEMENT_FLAG_PLAYABLE));
        avElements.add(
                new AVElement(new AVDescription.Builder()
                        .setTitle("harmony_video_1")
                        .setIMediaUri(Uri.parse(HARMONY_VIDEO_PATH_1))
                        .setMediaId(WEB_VIDEO_PATH)
                        .build(),
                        AVElement.AVELEMENT_FLAG_PLAYABLE));
        avElements.add(
                new AVElement(new AVDescription.Builder()
                        .setTitle("harmony_video_2")
                        .setIMediaUri(Uri.parse(HARMONY_VIDEO_PATH_2))
                        .setMediaId(WEB_VIDEO_PATH)
                        .build(),
                        AVElement.AVELEMENT_FLAG_PLAYABLE));
    }
}
