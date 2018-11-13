package com.yunarm.appstore.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.ViewPagerFragmentAdapter;
import com.yunarm.appstore.fragments.CommonAppsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener vpListener;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        vpListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        initFragments();
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), initFragments());
        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(vpListener);
    }


    private List<BaseLazyFragment> initFragments() {
        ArrayList<BaseLazyFragment> list = new ArrayList<>();
        CommonAppsFragment a = new CommonAppsFragment("aaaa");
        CommonAppsFragment b = new CommonAppsFragment("bbbb");
        CommonAppsFragment c = new CommonAppsFragment("cccc");
        CommonAppsFragment d = new CommonAppsFragment("dddd");

        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);

        return list;

    }

}
