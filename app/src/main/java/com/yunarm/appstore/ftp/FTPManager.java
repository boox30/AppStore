package com.yunarm.appstore.ftp;

import android.util.Log;

import com.yunarm.appstore.utils.FtpConfigUtils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FTPManager {
    FTPClient ftpClient = null;
    private static final String TAG = "FTPManager";
    private String config = "/data/local/one/manage.conf";

    public FTPManager() {
        ftpClient = new FTPClient();
    }

    public boolean connectByConfig(FtpConnectStateListener ftpListener) {
        HashMap<String, String> map = FtpConfigUtils.readContentOfFile(new File(config));
        try {
            return this.connect(map.get(FtpConfigUtils.IP), Integer.parseInt(map.get(FtpConfigUtils.PORT)), map.get(FtpConfigUtils.USER_NAME), map.get(FtpConfigUtils.PWD), ftpListener);
        } catch (Exception e) {
            Log.e(TAG, "error!!", e);
        }
        return false;
    }

    // 连接到ftp服务器
    public synchronized boolean connect(String ip, int port, String user, String pwd, FtpConnectStateListener ftpListener) throws Exception {
        HashMap<String, String> stringStringHashMap = FtpConfigUtils.readContentOfFile(new File("/data/local/one/manage.conf"));
        boolean bool = false;
        if (ftpClient.isConnected()) {
//            return true;
            ftpClient.disconnect();
        }
        ftpClient.setConnectTimeout(10000);//设置连接超时时间
        ftpClient.setControlEncoding("utf-8");
        ftpClient.connect(ip, port);
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (ftpClient.login(user, pwd)) {
                bool = true;
                Log.d(TAG, "ftp连接成功");
                ftpListener.onLoginSucc(true);
            } else {
                Log.d(TAG, "ftp 不成功");
                ftpListener.onLoginSucc(false);
            }
        }
        return bool;
    }

    public boolean isConnected() {
        return ftpClient.isConnected();
    }

    public void disConnectFtp() {
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 实现下载文件功能，可实现断点下载
    public synchronized boolean downloadFile(String localPath, String serverPath, FtpDownLoadListener listener)
            throws Exception {
        // 先判断服务器文件是否存在
        File serverFile = new File(serverPath);
        ftpClient.changeWorkingDirectory(serverFile.getParentFile().getAbsolutePath());
        ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setConnectTimeout(3000);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        FTPFile[] files = ftpClient.listFiles();
        if (files.length == 0) {
            Log.d(TAG, "服务器文件不存在");
            return false;
        }
        FTPFile ftpFile = null;
        for (int i = 0; i < files.length; i++) {
            String name = serverFile.getName();
            if (files[i].getName().equals(name)) {
                ftpFile = files[i];
                break;
            }
        }
        if (ftpFile == null) {
            Log.e(TAG, "========ftpFile is null");
            return false;
        }
        Log.d(TAG, "远程文件存在,名字为：" + ftpFile.getName());

        //创建本地文件夹
        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }

        //本地文件名
        String localFilePath = localPath + ftpFile.getName();

        // 接着判断下载的文件是否能断点下载
        long serverSize = ftpFile.getSize(); // 获取远程文件的长度
        File localFile = new File(localFilePath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            Log.d(TAG, "=======本地文件存在=====localSize: " + localSize + " serverSize:" + serverSize);
            if (localSize >= serverSize) {
                Log.d(TAG, "文件已经下载完了");
                listener.onDownProgress(100);
                listener.onDownloadSucc(localFile.getAbsolutePath());
                return true;
            }
        }
        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = localSize;
        // 开始准备下载文件
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 10 == 0) {
                    listener.onDownProgress(process);
                    Log.d(TAG, " ====file name:" + ftpFile.getName() + "===下载进度：" + process);
                }
            }
        }
        out.flush();
        out.close();
        input.close();
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            Log.d(TAG, "文件下载成功");
            listener.onDownloadSucc(localFile.getAbsolutePath());
            return true;
        } else {
            Log.d(TAG, "文件下载失败");
            listener.onDownloadFail();
            return false;
        }
    }

    // 实现下载文件功能，可实现断点下载
    public synchronized boolean downloadFileByMutiThread(String localPath, String serverPath, FtpDownLoadListener listener)
            throws Exception {
        // 先判断服务器文件是否存在
        File serverFile = new File(serverPath);
        ftpClient.changeWorkingDirectory(serverFile.getParentFile().getAbsolutePath());
        ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        FTPFile[] files = ftpClient.listFiles();
        if (files.length == 0) {
            Log.d(TAG, "服务器文件不存在");
            return false;
        }
        FTPFile ftpFile = null;
        for (int i = 0; i < files.length; i++) {
            String name = serverFile.getName();
            if (files[i].getName().equals(name)) {
                ftpFile = files[i];
                break;
            }
        }
        Log.d(TAG, "远程文件存在,名字为：" + ftpFile.getName());

        //创建本地文件夹
        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }

        //本地文件名
        String localFilePath = localPath + ftpFile.getName();

        // 接着判断下载的文件是否能断点下载
        long serverSize = ftpFile.getSize(); // 获取远程文件的长度
        File localFile = new File(localFilePath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            Log.d(TAG, "=======本地文件存在=====localSize: " + localSize + " serverSize:" + serverSize);
            if (localSize >= serverSize) {
                Log.d(TAG, "文件已经下载完了");
                listener.onDownProgress(100);
                listener.onDownloadSucc(localFile.getAbsolutePath());
                return true;
            }
        }
        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = localSize;
        // 开始准备下载文件
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 10 == 0) {
                    listener.onDownProgress(process);
                    Log.d(TAG, "下载进度：" + process);
                }
            }
        }
        out.flush();
        out.close();
        input.close();
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            Log.d(TAG, "文件下载成功");
            listener.onDownloadSucc(localFile.getAbsolutePath());
            return true;
        } else {
            Log.d(TAG, "文件下载失败");
            listener.onDownloadFail();
            return false;
        }
    }


    // 创建文件夹

    public boolean createDirectory(String path) throws Exception {
        boolean bool = false;
        String directory = path.substring(0, path.lastIndexOf("/") + 1);
        int start = 0;
        int end = 0;
        if (directory.startsWith("/")) {
            start = 1;
        }
        end = directory.indexOf("/", start);
        while (true) {
            String subDirectory = directory.substring(start, end);
            if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                ftpClient.makeDirectory(subDirectory);
                ftpClient.changeWorkingDirectory(subDirectory);
                bool = true;
            }
            start = end + 1;
            end = directory.indexOf("/", start);
            if (end == -1) {
                break;
            }
        }
        return bool;
    }
    // 实现上传文件的功能

    public synchronized boolean uploadFile(String localPath, String serverPath)
            throws Exception {
        // 上传文件之前，先判断本地文件是否存在
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            Log.d("tag", "本地文件不存在");
            return false;
        }
        Log.d("tag", "本地文件存在，名称为：" + localFile.getName());
        createDirectory(serverPath); // 如果文件夹不存在，创建文件夹
        Log.d("tag", "服务器文件存放路径：" + serverPath + localFile.getName());
        String fileName = localFile.getName();
        // 如果本地文件存在，服务器文件也在，上传文件，这个方法中也包括了断点上传
        long localSize = localFile.length(); // 本地文件的长度
        FTPFile[] files = ftpClient.listFiles(fileName);
        long serverSize = 0;
        if (files.length == 0) {
            Log.d("tag", "服务器文件不存在");
            serverSize = 0;
        } else {
            serverSize = files[0].getSize(); // 服务器文件的长度
        }
        if (localSize <= serverSize) {
            if (ftpClient.deleteFile(fileName)) {
                Log.d("tag", "服务器文件存在,删除文件,开始重新上传");
                serverSize = 0;
            }
        }
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        // 进度
        long step = localSize / 100;
        long process = 0;
        long currentSize = 0;
        // 好了，正式开始上传文件
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setRestartOffset(serverSize);
        raf.seek(serverSize);
        OutputStream output = ftpClient.appendFileStream(fileName);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = raf.read(b)) != -1) {
            output.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 10 == 0) {
                    Log.d("tag", "上传进度：" + process);
                }
            }
        }
        output.flush();
        output.close();
        raf.close();
        if (ftpClient.completePendingCommand()) {
            Log.d("tag", "文件上传成功");
            return true;
        } else {
            Log.d("tag", "文件上传失败");
            return false;
        }
    }

    // 如果ftp上传打开，就关闭掉
    public void closeFTP() throws Exception {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    public List<FTPFile> listFiles(String remotePath) throws IOException {
        List<FTPFile> list = new ArrayList<>();
        // 获取文件
        ftpClient.changeWorkingDirectory(remotePath);
        FTPFile[] files = ftpClient.listFiles();
        // 遍历并且添加到集合
        Collections.addAll(list, files);
        return list;
    }


    /**
     * 下载单个文件,此时ftpFile必须在ftp工作目录下
     *
     * @param localFile 本地目录
     * @param ftpFile   FTP文件
     * @return true下载成功, false下载失败
     * @throws IOException
     */
    private boolean downloadSingle(File localFile, FTPFile ftpFile) throws IOException {
        boolean flag;
        // 创建输出流
        OutputStream outputStream = new FileOutputStream(localFile);
        // 下载单个文件
        flag = ftpClient.retrieveFile(ftpFile.getName(), outputStream);
        // 关闭文件流
        outputStream.close();
        return flag;
    }
}
