package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.william.androidsdk.baseui.BaseLazyFragment;
import com.william.androidsdk.baseui.listener.OnItemClickListener;
import com.william.androidsdk.utils.ToastUtils;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.activities.AppInfoListActivity;
import com.yunarm.appstore.adapters.AppTypeRecyclerViewAdapter;
import com.yunarm.appstore.bean.AppTypeBean;
import com.yunarm.appstore.http.AppListHelper;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class AppTypeFragment extends BaseLazyFragment {
    private String type;
    private RecyclerView recyclerView;
    private AppTypeRecyclerViewAdapter adapter;
    private ArrayList<AppTypeBean.MessageBean.ChildrenBeanX> list;

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
        list = arguments.getParcelableArrayList(AppListHelper.DATA_LIST_TAG);
        adapter.setData(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                ToastUtils.showToast(itemView.getContext(), "position:" + position + " "+list.get(position).getName());
                Intent intent = new Intent();
                intent.setClass(getSupportActivity(), AppInfoListActivity.class);
                intent.putExtra(ApplicationConstant.ID, list.get(position).getId());
            }
        });

    }
}
