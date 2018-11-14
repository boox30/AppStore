package com.yunarm.appstore;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.gson.Gson;
import com.william.androidsdk.utils.FileUtils;
import com.yunarm.appstore.api.GetAppTypeListService;
import com.yunarm.appstore.bean.AppTypeBean;
import com.yunarm.appstore.bean.PostResult;
import com.yunarm.appstore.ftp.FTPManager;
import com.yunarm.appstore.ftp.FtpConnectStateListener;
import com.yunarm.appstore.ftp.FtpDownLoadListener;
import com.yunarm.appstore.ftp.FtpManagerTasks;
import com.yunarm.appstore.http.HttpUtils;
import com.yunarm.appstore.utils.FtpConfigUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppStoreTest {

    private static FTPManager ftpManager;
    private String TAG = "AppStoreTest";

    static {
        ftpManager = new FTPManager();
        appContext = InstrumentationRegistry.getTargetContext();
    }

    private ArrayList<String> list;
    private static Context appContext;

    @Test
    public void testLoginFtp() {

        try {
            ftpManager.connectByConfig(new FtpConnectStateListener() {
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
                    Log.d("tag", "===" + progress);
                }

                @Override
                public void onDownloadSucc(String path) {
                    Log.d("tag", "===onDownloadSucc path:" + path);
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

    @Test
    public void testFtpConfig() {
        HashMap<String, String> map = FtpConfigUtils.readContentOfFile(new File("/data/local/one/manage.conf"));
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> e : entries) {
            Log.d("tag", "=============key: " + e.getKey() + " value: " + e.getValue());
        }
        assertNotNull(map);
        assertTrue(map.size() == 4);
        assertTrue(map.get(FtpConfigUtils.USER_NAME).equals("ysjUserName"));
    }


    @Test
    public void testDownloadTask() throws InterruptedException {
        list = new ArrayList<>();
        list.add("/app/1003/com.sdu.didi.gui_3.0.7.apk");
//        list.add("/app/1001/com.newdadabus_3.4.3.apk");
//        list.add("/app/1000/com.meituan.qcs.r.android_2.2.31.apk");
//        list.add("/app/1012/com.edcsc.wbus_3.7.7.apk");


        final CountDownLatch downLatch = new CountDownLatch(list.size() + 1);
        final FtpManagerTasks instance = FtpManagerTasks.getInstance();
        instance.loginFtpServer(new FtpConnectStateListener() {
            @Override
            public void onLoginSucc(boolean success) {
                downLatch.countDown();


                //for (int i = 0; i < list.size(); i++) {
                downLoadSimpleFile(instance, list.get(0), downLatch);
//                    downLoadSimpleFile(instance, list.get(1), downLatch);
//                    downLoadSimpleFile(instance, list.get(2), downLatch);
                //}

            }
        });
        downLatch.await();
    }

    private void downLoadSimpleFile(FtpManagerTasks instance, String remoteFile, final CountDownLatch downLatch) {
        instance.getFtpFileByPathTask("/sdcard/", remoteFile, new FtpDownLoadListener() {
            @Override
            public void onDownProgress(long progress) {

                Log.d(TAG, "==testDownloadTask=====" + progress + " thread: " + Thread.currentThread());
                assertTrue(progress >= 0 && progress <= 100);
            }

            @Override
            public void onDownloadSucc(String localPath) {
                Log.d(TAG, "==onDownloadSucc=====localPath: " + localPath + " thread: " + Thread.currentThread());
//                String name = localPath.substring(localPath.lastIndexOf("/") + 1);
                assertTrue(localPath.equals("/sdcard/com.sdu.didi.gui_3.0.7.apk"));
                downLatch.countDown();
            }

            @Override
            public void onDownloadFail() {
                Log.d(TAG, "==testDownloadTask===onDownloadFail==" + " thread: " + Thread.currentThread());
                int a = 1;
                assertTrue(a != 1);
                downLatch.countDown();
            }
        });
    }

    @Test
    public void testRequestAppTypes() {
        GetAppTypeListService listService = HttpUtils.createBigTypeAppListService(appContext);
        listService
                .getAppTypeList(String.valueOf(2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostResult>() {
                    @Override
                    public void accept(PostResult postResult) throws Exception {
                        assertNotNull(postResult);
                        assertEquals(postResult.isStatus(), true);

                        String str = "{\"status\":true,\"message\":" + postResult.getMessage() + "}";
                        Gson gson = new Gson();
                        AppTypeBean appTypeInfo = gson.fromJson(str, AppTypeBean.class);
                        List<AppTypeBean.MessageBean> message = appTypeInfo.getMessage();
                        assertNotNull(message);
                        assertTrue(message.size() >= 1);
                    }
                });

    }

    @Test
    public void testRequestSmallAppType() {
        GetAppTypeListService listService = HttpUtils.createSmallAppTypeListService(appContext);
        listService
                .getAppInfoList(null, 9+"", null, null, null, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostResult>() {
                    @Override
                    public void accept(PostResult postResult) throws Exception {
                        String str = "{\"status\":" + String.valueOf(postResult.isStatus()) + ",\"message\":" + postResult.getMessage() + "}";
                        String s = "/sdcard/message.txt";
                        File file = new File(s);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileUtils.saveContentToFile(str, file);
                    }
                });
    }



}
