package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.AppTypeRecyclerViewAdapter;
import com.yunarm.appstore.bean.AppTypeInfo;
import com.yunarm.appstore.http.AppListHelper;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class AppTypeFragment extends BaseLazyFragment {
    private String type;
    private RecyclerView recyclerView;
    private AppTypeRecyclerViewAdapter adapter;

    public AppTypeFragment(String type) {
        this.type = type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_type;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.app_type_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(getSupportActivity()));
        adapter = new AppTypeRecyclerViewAdapter();

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        Bundle arguments = getArguments();
        ArrayList<AppTypeInfo.MessageBean.ChildrenBeanX> list = arguments.getParcelableArrayList(AppListHelper.DATA_LIST_TAG);
        adapter.setData(list);
        recyclerView.setAdapter(adapter);
    }
}
