package com.yunarm.appstore.app;

import com.william.androidsdk.utils.FileUtils;
import com.yunarm.appstore.AppStoreApplication;
import com.yunarm.appstore.R;
import com.yunarm.appstore.bean.AppInfoBean;

public class AppInstallTask implements Runnable {
    private AppInfoBean.MessageBean.DataBean bean;
    private String apkPath;

    public AppInstallTask(String apkPath, AppInfoBean.MessageBean.DataBean bean) {
        this.apkPath = apkPath;
        this.bean = bean;
    }

    @Override
    public void run() {
        boolean b = ApplicationHelper.clientInstall(apkPath);
        if (b) {
            boolean avilible = ApplicationHelper.isApplicationAvilible(AppStoreApplication.getContext().getPackageManager(), bean.getPackageX());
            if (avilible) {
                bean.setInstallState(AppStoreApplication.getContext().getResources().getString(R.string.open));
                FileUtils.deleteFile(apkPath);
            }
        }
    }

    public String getApkFileId() {
        return apkPath;
    }
}
