package com.yunarm.appstore.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;

public class DataBindingUtils {
    @BindingAdapter("bind:loadImage")
    public static void loadImageByIconID(ImageView view, String icon) {
        String url = ApplicationConstant.HOST + "/app/" + icon + "/icon.png";
        Picasso.with(view.getContext()).load(url).placeholder(R.mipmap.app_store_icon).error(R.mipmap.app_store_icon).into(view);
    }
}
