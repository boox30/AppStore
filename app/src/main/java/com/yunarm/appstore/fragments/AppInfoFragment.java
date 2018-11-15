package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.AppInfoRecyclerViewAdapter2;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.events.AppInstallFinishEvent;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

@SuppressLint("ValidFragment")
public class AppInfoFragment extends BaseLazyFragment {

    private XRecyclerView recyclerView;
    private TextView viewById;
    private String type;
    private final AppListHelper instance;
    private AppInfoRecyclerViewAdapter2 adapter;
    private TextView message;
    //    private AppInfoRecyclerViewAdapter adapter;

    public AppInfoFragment(String type) {
        this.type = type;
        instance = AppListHelper.getInstance();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_info;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.app_info_content);
        message = findViewById(R.id.message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getSupportActivity()));
        RecyclerViewDivider divider = new RecyclerViewDivider(getSupportActivity(), LinearLayoutManager.HORIZONTAL, R.drawable.item_divider_revycler_view);

        recyclerView.addItemDecoration(divider);
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        String id = bundle.getString(ApplicationConstant.ID);
        Log.d("tag", "====initData======id==" + id);
        message.setText(R.string.loading);
        instance.getAppInfoList(getSupportActivity(), id, new LoadFinishCallback() {
            @Override
            public void onLoadDataFinish() {
                List<AppInfoBean.MessageBean.DataBean> dataList = instance.getAppInfoDataList();
                //adapter = new AppInfoRecyclerViewAdapter();
                //adapter.setData(dataList);
                if (dataList == null || dataList.size() == 0) {
                    message.setVisibility(View.VISIBLE);
                    message.setText(R.string.no_current_type_app);
                    return;
                }
                message.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new AppInfoRecyclerViewAdapter2(dataList, R.layout.item_app_info);
                recyclerView.setAdapter(adapter);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void onAppInstallFinish(AppInstallFinishEvent event) {
        if (adapter != null) {

        }
    }
}
