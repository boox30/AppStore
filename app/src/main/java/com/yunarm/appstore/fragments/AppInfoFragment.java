package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.william.androidsdk.utils.ToastUtils;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.AppInfoRecyclerViewAdapter2;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;

import java.util.List;

@SuppressLint("ValidFragment")
public class AppInfoFragment extends BaseLazyFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;
    private TextView viewById;
    private String type;
    private final AppListHelper instance;
    private AppInfoRecyclerViewAdapter2 adapter;
    private TextView message;
    private int pageIndex = 1;
    private String id;
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
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.getDefaultRefreshHeaderView();
        recyclerView.setLoadingListener(this);
    }

    @Override
    protected void initData() {
        message.setText(R.string.loading);
        Bundle bundle = getArguments();
        id = bundle.getString(ApplicationConstant.ID);

        LoadFinishCallback callback = new LoadFinishCallback() {
            @Override
            public void onLoadDataFinish() {
                List<AppInfoBean.MessageBean.DataBean> dataList = instance.getAppInfoDataList();
                //adapter = new AppInfoRecyclerViewAdapter();
                //adapter.setData(dataList);
                if ((dataList == null || dataList.size() == 0)) {
                    message.setVisibility(View.VISIBLE);
                    message.setText(R.string.no_current_type_app);
                    return;
                }
                message.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new AppInfoRecyclerViewAdapter2(R.layout.item_app_info);
                adapter.setData(dataList);
                recyclerView.setAdapter(adapter);
            }
        };
        loadData(callback);
    }

    private void loadData(LoadFinishCallback callback) {
        instance.getAppInfoList(getSupportActivity(), id, String.valueOf(pageIndex), callback);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // any time,when you finish your activity or fragment,call this below
        if (recyclerView != null) {
            recyclerView.destroy(); // this will totally release XR's memory
            recyclerView = null;
        }
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        loadData(new LoadFinishCallback() {
            @Override
            public void onLoadDataFinish() {
                List<AppInfoBean.MessageBean.DataBean> dataList = instance.getAppInfoDataList();
                if (dataList.size() > 0) {
                    adapter.addDates(dataList);
                } else {
                    pageIndex--;
                    ToastUtils.showToast(getSupportActivity(), R.string.no_more);
                }
                recyclerView.loadMoreComplete();
            }
        });
    }
}
