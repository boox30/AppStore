package com.yunarm.appstore.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.yunarm.appstore.api.GetAppTypeList;
import com.yunarm.appstore.bean.AppTypeInfo;
import com.yunarm.appstore.bean.PostResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppListHelper {
    ArrayList<AppTypeInfo.MessageBean.ChildrenBeanX> types;
    Map<String, ArrayList<AppTypeInfo.MessageBean.ChildrenBeanX>> allAppTypeMap = new HashMap<>();
    List<String> bigTypes = new ArrayList<>();
    private static AppListHelper helperInstance;
    public static final String DATA_LIST_TAG = "data";

    private AppListHelper() {
    }

    public static AppListHelper getInstance() {
        if (helperInstance == null) {
            helperInstance = new AppListHelper();
        }
        return helperInstance;
    }

    @SuppressLint("CheckResult")
    public void getAppTypeData(Context context, final LoadFinishCallback callback) {
        if (bigTypes != null && bigTypes.size() > 0 && allAppTypeMap != null && allAppTypeMap.size() > 0) {
            callback.onLoadDataFinish();
        }
        bigTypes.clear();
        allAppTypeMap.clear();
        GetAppTypeList listService = HttpUtils.createAppTypeListService(context);
        listService
                .getApplistResult(String.valueOf(2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostResult>() {
                    @Override
                    public void accept(PostResult postResult) throws Exception {

                        String str = "{\"status\":" + String.valueOf(postResult.isStatus()) + ",\"message\":" + postResult.getMessage() + "}";
                        Gson gson = new Gson();
                        AppTypeInfo appTypeInfo = gson.fromJson(str, AppTypeInfo.class);
                        List<AppTypeInfo.MessageBean> message = appTypeInfo.getMessage();
                        types = new ArrayList<>();
                        for (int i = 0; i < message.size(); i++) {
                            AppTypeInfo.MessageBean messageBean = message.get(i);
                            String name = messageBean.getName();
                            bigTypes.add(name);
                            types.clear();
                            Log.d("tag", "=====name====" + name);
                            for (int j = 0; j < messageBean.getChildren().size(); j++) {
                                Log.d("tag", "==========children name=" + messageBean.getChildren().get(j).getName());
                                types.add(messageBean.getChildren().get(j));
                            }
                            allAppTypeMap.put(name, types);
                        }
                        callback.onLoadDataFinish();
                    }
                });
    }

    public List<String> getBigTypes() {
        return bigTypes;
    }

    public ArrayList<AppTypeInfo.MessageBean.ChildrenBeanX> getTypes(String type) {
        return allAppTypeMap.get(type);
    }
}
