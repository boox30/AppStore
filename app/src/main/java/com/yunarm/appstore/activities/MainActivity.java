package com.yunarm.appstore.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.ViewPagerFragmentAdapter;
import com.yunarm.appstore.fragments.AppTypeFragment;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;

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
        stepFragmentsWithViewPager();

    }


    private void stepFragmentsWithViewPager() {
        final ArrayList<BaseLazyFragment> list = new ArrayList<>();
        final AppListHelper instance = AppListHelper.getInstance();
        instance.getAppTypeData(this, new LoadFinishCallback() {
            @Override
            public void onLoadDataFinish() {
                List<String> bigTypes = instance.getBigTypes();
                AppTypeFragment fragment;
                for (int i = 0; i < bigTypes.size(); i++) {
                    String type = bigTypes.get(i);
                    fragment = new AppTypeFragment(type);
                    Bundle args = new Bundle();
                    args.putParcelableArrayList(AppListHelper.DATA_LIST_TAG, instance.getTypes(type));
                    fragment.setArguments(args);
                    list.add(fragment);
                }

                ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), list);
                viewPager.setAdapter(adapter);
                tabs.setViewPager(viewPager);
                tabs.setOnPageChangeListener(vpListener);
            }
        });
    }

}
