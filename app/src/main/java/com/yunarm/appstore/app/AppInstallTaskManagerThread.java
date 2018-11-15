package com.yunarm.appstore.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppInstallTaskManagerThread implements Runnable {

    private final AppInstallManager installManager;
    // 线程池大小
    private final int POOL_SIZE = 5;
    // 轮询时间
    private final int SLEEP_TIME = 3000;
    private final ExecutorService executorService;
    // 是否停止
    private boolean isStop = false;

    public AppInstallTaskManagerThread() {
        installManager = AppInstallManager.getInstance();
        executorService = Executors.newFixedThreadPool(POOL_SIZE);
    }

    @Override
    public void run() {
        while (!isStop) {
            AppInstallTask appInstallTak = installManager.getAppInstallTak();
            if (appInstallTak != null) {
                executorService.execute(appInstallTak);
            }else {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        if (isStop) {
            executorService.shutdown();
        }
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
