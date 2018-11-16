package com.yunarm.appstore.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.app.ApplicationHelper;
import com.yunarm.appstore.ftp.FtpConnectStateListener;
import com.yunarm.appstore.ftp.FtpDownLoadListener;
import com.yunarm.appstore.ftp.FtpManagerTasks;

import java.io.File;

public class ApksFtpService extends Service {

    private FtpManagerTasks managerTasks;

    public ApksFtpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String path = intent.getStringExtra(ApplicationConstant.PATH);
        if (path == null) {
            Log.e("ApksFtpService", "bad path for downloading!");
            return super.onStartCommand(intent, flags, startId);
        }
        final String remotePath = "/app/" + path;
        managerTasks = FtpManagerTasks.getInstance();
        if (!managerTasks.isConnected()) {
            managerTasks.loginFtpServer(new FtpConnectStateListener() {
                @Override
                public void onLoginSucc(boolean success) {
                    if (success) {
                        downloadApkFiles(remotePath);
                    }
                }
            });
        } else {
            downloadApkFiles(remotePath);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void downloadApkFiles(String path) {
        managerTasks.getFtpFileByPathTask(ApplicationConstant.LOCAL_PATH, path, new FtpDownLoadListener() {
            @Override
            public void onDownProgress(int progress) {

            }

            @Override
            public void onDownloadSucc(String localPath) {
                File file = new File(localPath);
                if (file.exists()) {
                    ApplicationHelper.clientInstallTask(localPath, new ApplicationHelper.Callback() {
                        @Override
                        public void finish(boolean success) {
                            System.out.println("Install Success");
                        }
                    });
                }
            }

            @Override
            public void onDownloadFail() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
