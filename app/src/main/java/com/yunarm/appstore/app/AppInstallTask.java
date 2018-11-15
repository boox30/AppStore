package com.yunarm.appstore.app;

import com.yunarm.appstore.events.AppInstallFinishEvent;
import com.yunarm.appstore.utils.ApplicationHelper;

import org.greenrobot.eventbus.EventBus;

public class AppInstallTask implements Runnable {
    private String apkPath;

    public AppInstallTask(String apkPath) {
        this.apkPath = apkPath;
    }

    @Override
    public void run() {
        boolean b = ApplicationHelper.clientInstall(apkPath);
        if (b) {
            EventBus.getDefault().post(new AppInstallFinishEvent(apkPath));
        }
    }

    public String getApkFileId() {
        return apkPath;
    }
}
