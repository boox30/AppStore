package com.yunarm.appstore.interfaces;

public interface FtpDownLoadListener {
    void onDownProgress(long progress);
    void onDownloadSucc(String localPath);
    void onDownloadFail( );
}
