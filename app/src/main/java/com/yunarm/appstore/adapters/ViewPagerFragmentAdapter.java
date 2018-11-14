package com.yunarm.appstore.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.william.androidsdk.baseui.BaseFragmentPagerAdapter;
import com.william.androidsdk.baseui.BaseLazyFragment;

import java.util.List;

public class ViewPagerFragmentAdapter extends BaseFragmentPagerAdapter<BaseLazyFragment> {

    private List<String> bigTypes;

    public ViewPagerFragmentAdapter(FragmentManager fm, List<BaseLazyFragment> list) {
        super(fm, list);

    }


    public void setTitles(List<String> bigTypes) {
        this.bigTypes = bigTypes;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (bigTypes == null || bigTypes.size() == 0) ? "" : bigTypes.get(position);
    }
}
