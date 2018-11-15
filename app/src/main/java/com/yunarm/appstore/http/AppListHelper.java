package com.yunarm.appstore.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.william.androidsdk.utils.FileUtils;
import com.yunarm.appstore.api.GetAppTypeListService;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.bean.AppTypeBean;
import com.yunarm.appstore.bean.PostResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppListHelper {
    ArrayList<AppTypeBean.MessageBean.ChildrenBeanX> types;
    Map<String, ArrayList<AppTypeBean.MessageBean.ChildrenBeanX>> allAppTypeMap = new HashMap<>();
    List<String> bigTypes = new ArrayList<>();
    private static AppListHelper helperInstance;
    public static final String DATA_LIST_TAG = "data";
    private List<AppInfoBean.MessageBean.DataBean> appInfoDataList;

    private AppListHelper() {
    }

    public static AppListHelper getInstance() {
        if (helperInstance == null) {
            helperInstance = new AppListHelper();
        }
        return helperInstance;
    }

    @SuppressLint("CheckResult")
    public void getAppTypeList(Context context, final LoadFinishCallback callback) {
        if (bigTypes != null && bigTypes.size() > 0 && allAppTypeMap != null && allAppTypeMap.size() > 0) {
            callback.onLoadDataFinish();
            return;
        }
        bigTypes.clear();
        allAppTypeMap.clear();
        GetAppTypeListService listService = HttpUtils.createBigTypeAppListService(context);
        listService
                .getAppTypeList(String.valueOf(2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostResult>() {
                    @Override
                    public void accept(PostResult postResult) throws Exception {
                        String str = "{\"status\":" + String.valueOf(postResult.isStatus()) + ",\"message\":" + postResult.getMessage() + "}";
                        Gson gson = new Gson();
                        AppTypeBean appTypeInfo = gson.fromJson(str, AppTypeBean.class);
                        List<AppTypeBean.MessageBean> message = appTypeInfo.getMessage();

                        for (int i = 0; i < message.size(); i++) {
                            AppTypeBean.MessageBean messageBean = message.get(i);
                            String name = messageBean.getName();
                            bigTypes.add(name);
                            types = new ArrayList<>();
                            for (int j = 0; j < messageBean.getChildren().size(); j++) {
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

    public ArrayList<AppTypeBean.MessageBean.ChildrenBeanX> getTypes(String type) {
        return allAppTypeMap.get(type);
    }


    @SuppressLint("CheckResult")
    public void getAppInfoList(Context context, String category, final LoadFinishCallback callback) {
        GetAppTypeListService listService = HttpUtils.createAppInfoListService(context);
        listService
                .getAppInfoList(null, category, String.valueOf(15), null, null, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostResult>() {
                    @Override
                    public void accept(PostResult postResult) throws Exception {
                        String message1 = postResult.getMessage();
                        File file = new File("/sdcard/message.txt");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileUtils.saveContentToFile(message1, file);
                        String str = "{\"status\":true,\"message\":" + message1 + "}";
                        Gson gson = new Gson();
                        AppInfoBean appTypeInfo = gson.fromJson(str, AppInfoBean.class);
                        AppInfoBean.MessageBean message = appTypeInfo.getMessage();
                        appInfoDataList = message.getData() ;
                        callback.onLoadDataFinish();

                    }
                });
    }

    public List<AppInfoBean.MessageBean.DataBean> getAppInfoDataList() {
        return appInfoDataList;
    }
}
