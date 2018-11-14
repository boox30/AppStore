package com.yunarm.appstore.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.astuetz.PagerSlidingTabStrip;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.ViewPagerFragmentAdapter;
import com.yunarm.appstore.fragments.AppTypeFragment;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;

import java.util.ArrayList;
import java.util.List;

public class AppInfoListActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_type_list);

    }

    private void initView() {
        tabs = findViewById(R.id.small_type_tabs);
        viewPager = findViewById(R.id.small_type_view_pager);
        stepFragmentsWithViewPager();
    }

    private void stepFragmentsWithViewPager() {
        final ArrayList<BaseLazyFragment> list = new ArrayList<>();
        final AppListHelper instance = AppListHelper.getInstance();
        Intent intent = getIntent();
        String id = intent.getStringExtra(ApplicationConstant.ID);

        instance.getAppTypeList(this, new LoadFinishCallback() {
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
                adapter.setTitles(bigTypes);
                viewPager.setAdapter(adapter);
                tabs.setViewPager(viewPager);
            }
        });
    }
}
