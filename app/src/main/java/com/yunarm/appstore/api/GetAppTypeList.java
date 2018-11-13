package com.yunarm.appstore.api;

import com.yunarm.appstore.bean.PostResult;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetAppTypeList {
    @FormUrlEncoded
    @POST(".")
    Observable<PostResult> getApplistResult(@Field("type") String type);
}
