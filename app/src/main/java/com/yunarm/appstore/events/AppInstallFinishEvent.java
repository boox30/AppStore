package com.yunarm.appstore.events;

public class AppInstallFinishEvent {
    private String apkPath;

    public AppInstallFinishEvent(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }
}
