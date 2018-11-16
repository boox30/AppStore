package com.yunarm.appstore.api;

import com.yunarm.appstore.bean.PostResult;

import io.reactivex.Observable;
import io.reactivex.annotations.Nullable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetAppTypeListService {
    @FormUrlEncoded
    @POST(".")
    Observable<PostResult> getAppTypeList(@Field("type") String type);

    @FormUrlEncoded
    @POST(".")
    Observable<PostResult> getAppInfoList(@Nullable @Field("type") String type,
                                          @Nullable @Field("category") String category,
                                          @Nullable @Field("page_size") String pageSize,
                                          @Nullable @Field("page_index") String pageIndex,
                                          @Nullable @Field("sort") String sort,
                                          @Nullable @Field("keyword") String keyword);
}
