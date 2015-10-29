package com.droidlogic.tvinput.services;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.droidlogic.app.tv.DroidLogicTvInputService;
import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.app.tv.TvInputBaseSession;
import com.droidlogic.tvclient.TvClient;
import com.droidlogic.utils.Utils;
import android.amlogic.Tv;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.text.TextUtils;

public class HdmiInputService extends DroidLogicTvInputService {
    private static final String TAG = HdmiInputService.class.getSimpleName();
    private static TvClient client = TvClient.getTvClient();
    private HdmiInputSession mSession;

    @Override
    public Session onCreateSession(String inputId) {
        super.onCreateSession(inputId);
        mSession = new HdmiInputSession(getApplicationContext(), inputId, getHardwareDeviceId(inputId));
        registerInputSession(mSession);
        client.curSource = Tv.SourceInput_Type.SOURCE_TYPE_HDMI;
        return mSession;
    }

    public class HdmiInputSession extends TvInputBaseSession {
        public HdmiInputSession(Context context, String inputId, int deviceId) {
            super(context, inputId, deviceId);
        }

        @Override
        public void doAppPrivateCmd(String action, Bundle bundle) {
            super.doAppPrivateCmd(action, bundle);
            if (TextUtils.equals(DroidLogicTvUtils.ACTION_STOP_TV, action)) {
                stopTv();
            }
        }
    }

    public TvInputInfo onHardwareAdded(TvInputHardwareInfo hardwareInfo) {
        if (hardwareInfo.getType() != TvInputHardwareInfo.TV_INPUT_TYPE_HDMI)
            return null;

        Utils.logd(TAG, "=====onHardwareAdded====="+hardwareInfo.getDeviceId());

        TvInputInfo info = null;
        ResolveInfo rInfo = getResolveInfo(HdmiInputService.class.getName());
        if (rInfo != null) {
            try {
                info = TvInputInfo.createTvInputInfo(
                        getApplicationContext(),
                        rInfo,
                        hardwareInfo,
                        getTvInputInfoLabel(hardwareInfo.getDeviceId()),
                        null);
            } catch (XmlPullParserException e) {
                // TODO: handle exception
            }catch (IOException e) {
                // TODO: handle exception
            }
        }
        updateInfoListIfNeededLocked(hardwareInfo, info, false);

        return info;
    }

    public String onHardwareRemoved(TvInputHardwareInfo hardwareInfo) {
        if (hardwareInfo.getType() != TvInputHardwareInfo.TV_INPUT_TYPE_HDMI)
            return null;

        TvInputInfo info = getTvInputInfo(hardwareInfo);
        String id = null;
        if (info != null)
            id = info.getId();
        updateInfoListIfNeededLocked(hardwareInfo, info, true);

        Utils.logd(TAG, "=====onHardwareRemoved=====" + id);
        return id;
    }

}
