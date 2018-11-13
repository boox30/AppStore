package com.yunarm.appstore.ftp;

import android.annotation.SuppressLint;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FtpManagerTasks {

    private FTPManager ftpManager;
    private static FtpManagerTasks downloadTask;

    private FtpManagerTasks() {
        ftpManager = new FTPManager();
    }

    public static FtpManagerTasks getInstance() {
        if (downloadTask == null) {
            downloadTask = new FtpManagerTasks();
        }
        return downloadTask;
    }

    @SuppressLint("CheckResult")
    public void loginFtpServer(final FtpConnectStateListener listener) {
        Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(final ObservableEmitter<Boolean> emitter) throws Exception {
                        ftpManager.connectByConfig(new FtpConnectStateListener() {
                            @Override
                            public void onLoginSucc(boolean success) {
                                emitter.onNext(success);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        listener.onLoginSucc(aBoolean);
                    }
                });
    }


    /**
     * you need login the ftp first
     *
     * @param localPath
     * @param remotePath
     * @param listener
     */
    @SuppressLint("CheckResult")
    public void getFtpFileByPathTask(final String localPath, final String remotePath, final FtpDownLoadListener listener) {
        if (!ftpManager.isConnected()) {
            Log.d("tag", "=====ftpManager.isConnected() false======");
            listener.onDownloadFail();
            return;
        }
        final FtpDownloadResult result = new FtpDownloadResult();
        Observable
                .create(new ObservableOnSubscribe<FtpDownloadResult>() {
                    @Override
                    public void subscribe(final ObservableEmitter<FtpDownloadResult> emitter) throws Exception {
                        Log.d("tag", "===task=========thread: " + Thread.currentThread());
                        try {
                            ftpManager.downloadFile(localPath, remotePath, new FtpDownLoadListener() {
                                @Override
                                public void onDownProgress(long progress) {
                                    result.setProgress((int) progress);
                                    emitter.onNext(result);
                                }

                                @Override
                                public void onDownloadSucc(String localPath) {
                                    result.setSuccess(1);
                                    result.setLocalFilePath(localPath);
                                    emitter.onNext(result);

                                }

                                @Override
                                public void onDownloadFail() {
                                    result.setSuccess(0);
                                    emitter.onNext(result);

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FtpDownloadResult>() {
                    @Override
                    public void accept(FtpDownloadResult result) throws Exception {
                        listener.onDownProgress(result.getProgress());
                        if (result.getProgress() == 100 && result.getSuccess() == 1) {
                            listener.onDownloadSucc(result.getLocalFilePath());
                        }
                        if (result.getSuccess() == 0) {
                            listener.onDownloadFail();
                        }
                    }
                });
    }
}
