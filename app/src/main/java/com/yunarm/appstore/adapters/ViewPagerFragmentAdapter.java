package com.yunarm.appstore.adapters;

import android.support.v4.app.FragmentManager;

import com.william.androidsdk.baseui.BaseFragmentPagerAdapter;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.fragments.CommonAppsFragment;

import java.util.List;

public class ViewPagerFragmentAdapter extends BaseFragmentPagerAdapter<BaseLazyFragment> {

    public ViewPagerFragmentAdapter(FragmentManager fm, List<BaseLazyFragment> list) {
        super(fm, list);
    }
}
