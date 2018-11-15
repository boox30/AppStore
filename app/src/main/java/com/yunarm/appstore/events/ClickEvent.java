package com.yunarm.appstore.events;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.william.androidsdk.utils.ToastUtils;
import com.yunarm.appstore.AppStoreApplication;
import com.yunarm.appstore.R;
import com.yunarm.appstore.app.AppInstallManager;
import com.yunarm.appstore.app.AppInstallTask;
import com.yunarm.appstore.app.AppInstallTaskManagerThread;
import com.yunarm.appstore.app.ApplicationHelper;
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
        String state = bean.getInstallState();

        if (state.equals(context.getResources().getString(R.string.install_app))) {
            downloadAndInstall(bean, context);
        } else if (state.equals(context.getResources().getString(R.string.open))) {
            openApp(bean, context);
        } else if (state.equals(context.getResources().getString(R.string.uninstall_app))) {
            uninstallApp(bean, context);
        }
    }

    private void uninstallApp(final AppInfoBean.MessageBean.DataBean bean, final Context context) {
        ApplicationHelper.clientUninstallTask(bean.getPackageX(), new ApplicationHelper.Callback() {
            @Override
            public void finish(boolean success) {
                ToastUtils.showToast(context, success ? R.string.uninstall_app_success : R.string.uninstall_app_error);
                bean.setInstallState(context.getResources().getString(success ? R.string.install_app : R.string.uninstall_app));
            }
        });
    }

    private void openApp(final AppInfoBean.MessageBean.DataBean bean, final Context context) {
        String pkgName = bean.getPackageX();
        Intent intent = ApplicationHelper.getAppIntentByPkgName(context.getPackageManager(), pkgName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void downloadAndInstall(final AppInfoBean.MessageBean.DataBean bean, final Context context) {
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
                startInstallApk(localPath, bean);
            }

            @Override
            public void onDownloadFail() {

            }
        });
    }

    private void startInstallApk(String localPath, AppInfoBean.MessageBean.DataBean bean) {
        AppInstallTask task = new AppInstallTask(localPath, bean);
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
