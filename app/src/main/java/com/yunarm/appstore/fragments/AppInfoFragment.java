package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.AppInfoRecyclerViewAdapter;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;

import java.util.List;

@SuppressLint("ValidFragment")
public class AppInfoFragment extends BaseLazyFragment {

    private XRecyclerView recyclerView;
    private TextView viewById;
    private String type;
    private final AppListHelper instance;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getSupportActivity()));

    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        String id = bundle.getString(ApplicationConstant.ID);
        instance.getAppInfoList(getSupportActivity(), id, new LoadFinishCallback() {
            @Override
            public void onLoadDataFinish() {
                List<AppInfoBean.MessageBean.DataBean> dataList = instance.getAppInfoDataList();
                AppInfoRecyclerViewAdapter adapter = new AppInfoRecyclerViewAdapter();
                adapter.setData(dataList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

}
