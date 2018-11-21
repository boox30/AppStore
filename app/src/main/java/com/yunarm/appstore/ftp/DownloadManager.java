package com.yunarm.appstore.ftp;

import com.yunarm.appstore.bean.AppInfoBean;

import java.util.HashMap;
import java.util.Map;

public class DownloadManager {
    private static DownloadManager manager;
    Map<String, AppInfoBean.MessageBean.DataBean> taskMap;

    private DownloadManager() {
        taskMap = new HashMap<>();
    }

    public static DownloadManager getInstance() {
        if (manager == null) {
            manager = new DownloadManager();
        }
        return manager;
    }

    public void addToDownloadList(AppInfoBean.MessageBean.DataBean bean) {
        synchronized (taskMap) {
            if (!taskMap.containsKey(bean.getPath())) {
                taskMap.put(bean.getPath(), bean);
            }
        }
    }

    public void removeFromList(AppInfoBean.MessageBean.DataBean bean) {
        synchronized (taskMap) {
            String path = bean.getPath();
            if (taskMap.containsKey(path)) {
                taskMap.remove(path);
            }
        }
    }

    public boolean isInTaskList(AppInfoBean.MessageBean.DataBean bean) {
        return taskMap.containsKey(bean.getPath());
    }

    public AppInfoBean.MessageBean.DataBean getDownloadBean(String path) {
        if (taskMap.containsKey(path)) {
            return taskMap.get(path);
        }
        return null;
    }
}
