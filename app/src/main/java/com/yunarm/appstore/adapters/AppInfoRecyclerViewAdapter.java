package com.yunarm.appstore.adapters;

import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.william.androidsdk.baseui.BaseRecyclerViewAdapter;
import com.william.androidsdk.utils.ToastUtils;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.app.AppInstallManager;
import com.yunarm.appstore.app.AppInstallTask;
import com.yunarm.appstore.app.AppInstallTaskManagerThread;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.ftp.FtpConnectStateListener;
import com.yunarm.appstore.ftp.FtpDownLoadListener;
import com.yunarm.appstore.ftp.FtpManagerTasks;
import com.yunarm.appstore.views.progressButton.ProgressButton;

import java.util.List;

public class AppInfoRecyclerViewAdapter extends BaseRecyclerViewAdapter<AppInfoBean.MessageBean.DataBean, AppInfoRecyclerViewAdapter.AppInfoViewHolder> {

    private final AppInstallManager installManager;
    private List<AppInfoBean.MessageBean.DataBean> dataList;
    private final FtpManagerTasks managerTasks;
    private boolean mLoginSucc = false;

    public AppInfoRecyclerViewAdapter() {
        managerTasks = ConnectToFtpServer();
        installManager = AppInstallManager.getInstance();
        new Thread(new AppInstallTaskManagerThread()).start();
    }

    private FtpManagerTasks ConnectToFtpServer() {
        FtpManagerTasks managerTasks;
        managerTasks = FtpManagerTasks.getInstance();
        managerTasks.loginFtpServer(new FtpConnectStateListener() {
            @Override
            public void onLoginSucc(boolean success) {
                mLoginSucc = success;
            }
        });
        return managerTasks;
    }

    @NonNull
    @Override
    public AppInfoRecyclerViewAdapter.AppInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AppInfoViewHolder(viewGroup, R.layout.item_app_info);
    }

    @Override
    public void onBindViewHolder(@NonNull AppInfoRecyclerViewAdapter.AppInfoViewHolder appInfoViewHolder, int i) {
        if (dataList == null) {
            dataList = getData();
        }
        AppInfoBean.MessageBean.DataBean dataBean = dataList.get(i);
        String url = ApplicationConstant.HOST + "/app/" + dataBean.getId() + "/icon.png";
        appInfoViewHolder.setText(R.id.app_info_name, dataBean.getName());
        ImageView icon = (ImageView) appInfoViewHolder.findView(R.id.app_info_icon);
        final ProgressButton downLoadButton = (ProgressButton) appInfoViewHolder.findView(R.id.app_info_download);
        stepWithProgressButton(downLoadButton, i);
        Picasso.with(getContext()).load(url).error(R.mipmap.app_store_icon).into(icon);
    }

    private void stepWithProgressButton(final ProgressButton downLoadButton, final int position) {
        downLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadButton.setText(getContext().getResources().getString(R.string.wait_download));
                if (!(mLoginSucc && managerTasks.isConnected())) {
                    //ftp disconnect reconnect again
                    ConnectToFtpServer();
                    SystemClock.sleep(500);
                }
                String remotePath = "/app/" + dataList.get(position).getPath();
                ToastUtils.showToast(getContext(), "path: " + dataList.get(position).getPath() + " name:" + dataList.get(position).getName() + " id:" + dataList.get(position).getId());
                managerTasks.getFtpFileByPathTask("/sdcard/", remotePath, new FtpDownLoadListener() {
                    @Override
                    public void onDownProgress(int progress) {
                        downLoadButton.setText(getContext().getResources().getString(R.string.downloading));
                        downLoadButton.setProgress(progress);
                    }

                    @Override
                    public void onDownloadSucc(String localPath) {
                        downLoadButton.setText(getContext().getResources().getString(R.string.installing));
                        startInstallApk(localPath);
                        Log.d("tag", "====onDownloadSucc=========" + localPath);
                    }

                    @Override
                    public void onDownloadFail() {

                    }
                });

            }
        });
    }

    private void startInstallApk(String localPath) {
        AppInstallTask task = new AppInstallTask(localPath);
        installManager.addAppInstallTask(task);
    }

    public void setAppInstallFinish() {

    }

    public class AppInfoViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        public AppInfoViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }
    }
}
