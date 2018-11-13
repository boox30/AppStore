package com.yunarm.appstore.fragments;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.AppTypeRecyclerViewAdapter;
import com.yunarm.appstore.api.GetAppTypeList;
import com.yunarm.appstore.bean.AppTypeInfo;
import com.yunarm.appstore.bean.PostResult;
import com.yunarm.appstore.http.HttpUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
        GetAppTypeList listService = HttpUtils.createAppTypeListService(getSupportActivity());
        listService
                .getApplistResult(String.valueOf(2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostResult>() {
                    @Override
                    public void accept(PostResult postResult) throws Exception {

                        String str = "{\"status\":true,\"message\":" + postResult.getMessage() + "}";
                        Gson gson = new Gson();
                        AppTypeInfo appTypeInfo = gson.fromJson(str, AppTypeInfo.class);
                        List<AppTypeInfo.MessageBean> message = appTypeInfo.getMessage();
                        for (int i = 0; i < message.size(); i++) {
                            AppTypeInfo.MessageBean messageBean = message.get(i);
                            if (type.equals(messageBean.getName())) {
                                adapter.setData(messageBean.getChildren());
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }
                });

    }
}
