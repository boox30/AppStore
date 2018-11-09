package com.yunarm.appstore.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import com.william.androidsdk.utils.AppInfo;
import com.william.androidsdk.utils.AppInfoUtils;
import com.william.androidsdk.utils.ScanCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ApkFileScanUtils {
    public final static String SCAN_ROOT_DIR = "/sdcard";
    public final static String APK_EXTENTION = "apk";
    public static List<AppInfo> mApkFiles = new ArrayList<>();
    public static Context mContext;
    private static PackageManager packageManager;

    public static void scanFiles(Context context, String ext, ScanCallback callback) {
        mContext = context;
        fileScanTask(new File(SCAN_ROOT_DIR), ext, callback);
        packageManager = mContext.getPackageManager();
    }


    @SuppressLint("CheckResult")
    private static void fileScanTask(final File file, final String ext, final ScanCallback callback) {
        Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        boolean b = false;
                        if (ext != null && ext.equalsIgnoreCase(APK_EXTENTION)) {
                            mApkFiles.clear();
                            b = scanApkFiles(file);
                        }
                        emitter.onNext(b);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (ext != null && ext.equalsIgnoreCase(APK_EXTENTION)) {
                                callback.scanApksFinish(mApkFiles);
                            }
                        }
                    }
                });
    }




    private static boolean scanApkFiles(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File item : files) {
                if (item.isDirectory() && !item.isHidden()) {
                    scanApkFiles(item);
                } else {
                    String name = item.getName();
                    String substring = name.substring(name.lastIndexOf(".") + 1);
                    if (!item.isHidden() && substring.equalsIgnoreCase(APK_EXTENTION)) {
                        AppInfo info = AppInfoUtils.getAppInfoByFile(mContext.getPackageManager(), item);
                        if (info != null) {
                            mApkFiles.add(info);
                        }
                    }
                }
            }
        }
        return true;
    }

}
