package com.yunarm.appstore.ftp;

public interface FtpDownLoadListener {
    void onDownProgress(int progress);
    void onDownloadSucc(String localPath);
    void onDownloadFail( );
}
