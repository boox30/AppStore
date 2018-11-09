package com.yunarm.appstore.fragments;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;

public class CommonAppsFragment extends BaseLazyFragment {

    private XRecyclerView recyclerView;
    private TextView viewById;
    private String type;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common_apps;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.common_app_content);
        viewById = findViewById(R.id.ffff);
    }

    @Override
    protected void initData() {

    }

    public void setType(String str) {
        this.type = str;
        Log.d("tag", "=====settpe "+str);
        if (viewById != null) {
            viewById.setText(str);
        }
    }
}
