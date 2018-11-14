package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;

@SuppressLint("ValidFragment")
public class AppInfoFragment extends BaseLazyFragment {

    private XRecyclerView recyclerView;
    private TextView viewById;
    private String type;

    public AppInfoFragment(String type) {
        this.type = type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_info;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.app_info_content);
    }

    @Override
    protected void initData() {

    }

}
