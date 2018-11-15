package com.yunarm.appstore.app;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class AppInstallManager {
    private LinkedList<AppInstallTask> tasks;
    private Set<String> taskIdSet;

    private static AppInstallManager installManager;

    private AppInstallManager() {
        taskIdSet = new HashSet<>();
        tasks = new LinkedList<>();
    }

    public static AppInstallManager getInstance() {
        if (installManager == null) {
            installManager = new AppInstallManager();
        }
        return installManager;
    }

    public void addAppInstallTask(AppInstallTask task) {
        synchronized (tasks) {
            if (!isTaskRepeat(task.getApkFileId())) {
                tasks.add(task);
            }
        }
    }

    public boolean isTaskRepeat(String fileId) {
        synchronized (taskIdSet) {
            if (taskIdSet.contains(fileId)) {
                return true;
            } else {
                taskIdSet.add(fileId);
                return false;
            }
        }
    }


    public AppInstallTask getAppInstallTak() {
        synchronized (tasks) {
            if (tasks.size() > 0) {
                AppInstallTask task = tasks.removeFirst();
                return task;
            }
            return null;
        }
    }
}
