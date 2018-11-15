package com.yunarm.appstore.events;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.yunarm.appstore.AppStoreApplication;
import com.yunarm.appstore.R;
import com.yunarm.appstore.app.AppInstallManager;
import com.yunarm.appstore.app.AppInstallTask;
import com.yunarm.appstore.app.AppInstallTaskManagerThread;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.ftp.FtpConnectStateListener;
import com.yunarm.appstore.ftp.FtpDownLoadListener;
import com.yunarm.appstore.ftp.FtpManagerTasks;

public class ClickEvent {
    private boolean mLoginSucc = false;
    private static final AppInstallManager installManager;

    static {
        installManager = AppInstallManager.getInstance();
        new Thread(new AppInstallTaskManagerThread()).start();
    }

    public void onClick(final AppInfoBean.MessageBean.DataBean bean) {
        final Context context = AppStoreApplication.getContext();
        bean.setInstallState(context.getResources().getString(R.string.wait_download));
        FtpManagerTasks ftpManagerTasks = ConnectToFtpServer();

        if (!(mLoginSucc && ftpManagerTasks.isConnected())) {
            //ftp disconnect reconnect again
            ConnectToFtpServer();
            SystemClock.sleep(500);
        }

        String remotePath = "/app/" + bean.getPath();
        ftpManagerTasks.getFtpFileByPathTask("/sdcard/", remotePath, new FtpDownLoadListener() {
            @Override
            public void onDownProgress(int progress) {
                bean.setInstallState(context.getResources().getString(R.string.downloading));
                bean.setDownloadProgress(progress);
            }

            @Override
            public void onDownloadSucc(String localPath) {
                bean.setInstallState(context.getResources().getString(R.string.installing));
                //startInstallApk(localPath);
                Log.d("tag", "====onDownloadSucc=========" + localPath);
            }

            @Override
            public void onDownloadFail() {

            }
        });


    }

    private void startInstallApk(String localPath) {
        AppInstallTask task = new AppInstallTask(localPath);
        installManager.addAppInstallTask(task);
    }

    private FtpManagerTasks ConnectToFtpServer() {
        FtpManagerTasks managerTasks;
        managerTasks = FtpManagerTasks.getInstance();
        managerTasks.loginFtpServer(new FtpConnectStateListener() {
            @Override
            public void onLoginSucc(boolean success) {
                mLoginSucc = success;
            }
        });
        return managerTasks;
    }
}
