package com.yunarm.appstore.adapters;

import android.support.v4.app.FragmentManager;

import com.william.androidsdk.baseui.BaseFragmentPagerAdapter;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.fragments.CommonAppsFragment;

import java.util.List;

public class ViewPagerFragmentAdapter extends BaseFragmentPagerAdapter<BaseLazyFragment> {

    public ViewPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    protected void init(FragmentManager fm, List<BaseLazyFragment> lists) {
        CommonAppsFragment a = new CommonAppsFragment();
        a.setType("aaa");
        CommonAppsFragment b = new CommonAppsFragment();
        b.setType("bbb");
        CommonAppsFragment c = new CommonAppsFragment();
        c.setType("ccc");
        lists.add(a);
        lists.add(b);
        lists.add(c);
    }
}
