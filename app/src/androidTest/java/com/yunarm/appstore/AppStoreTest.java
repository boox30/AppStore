package com.yunarm.appstore;


import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.yunarm.appstore.ftp.FtpConnectStateListener;
import com.yunarm.appstore.ftp.FtpDownLoadListener;
import com.yunarm.appstore.ftp.FTPManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppStoreTest {

    private FTPManager ftpManager;

    @Test
    public void testLoginFtp() {
        ftpManager = new FTPManager();
        try {
            ftpManager.connect("183.6.117.87", 20002, "ysjUserName", "ysjPassword", new FtpConnectStateListener() {
                @Override
                public void onLoginSucc(boolean success) {
                    assertTrue(success);
                    if (success) {
                        download();
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void download() {
        try {
            ftpManager.downloadFile("/sdcard/", "/app/1001/com.newdadabus_3.4.3.apk", new FtpDownLoadListener() {
                @Override
                public void onDownProgress(long progress) {
                    Log.d("tag", "==="+progress);
                }

                @Override
                public void onDownloadSucc(String path) {
                    Log.d("tag", "===onDownloadSucc path:"+path);
                    assertNotNull(path);

                }

                @Override
                public void onDownloadFail() {

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
