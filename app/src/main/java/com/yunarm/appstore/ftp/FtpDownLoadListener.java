package com.yunarm.appstore.ftp;

public interface FtpDownLoadListener {
    void onDownProgress(long progress);
    void onDownloadSucc(String localPath);
    void onDownloadFail( );
}
