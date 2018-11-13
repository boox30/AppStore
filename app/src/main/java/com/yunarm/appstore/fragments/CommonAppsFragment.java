package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;

@SuppressLint("ValidFragment")
public class CommonAppsFragment extends BaseLazyFragment {

    private XRecyclerView recyclerView;
    private TextView viewById;
    private String type;

    public CommonAppsFragment(String type) {
        this.type = type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common_apps;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.common_app_content);
    }

    @Override
    protected void initData() {

    }

}
