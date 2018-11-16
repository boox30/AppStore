package com.yunarm.appstore.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.william.androidsdk.baseui.BaseFragment;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.ViewPagerFragmentAdapter;
import com.yunarm.appstore.bean.AppTypeBean;
import com.yunarm.appstore.fragments.AppInfoFragment;

import java.util.ArrayList;
import java.util.List;

public class AppInfoListActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_type_list);
        initView();
    }

    private void initView() {
        tabs = findViewById(R.id.small_type_tabs);
        viewPager = findViewById(R.id.small_type_view_pager);
        stepFragmentsWithViewPager();
        String title = getIntent().getStringExtra(ApplicationConstant.TITLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void stepFragmentsWithViewPager() {
        ArrayList<BaseFragment> list = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        Bundle bundle = getIntent().getBundleExtra(ApplicationConstant.BUNDLE);
        ArrayList<AppTypeBean.MessageBean.ChildrenBeanX.ChildrenBean> arrayList = bundle.getParcelableArrayList(ApplicationConstant.CHILDREN);
        AppInfoFragment fragment;
        for (int i = 0; i < arrayList.size(); i++) {
            AppTypeBean.MessageBean.ChildrenBeanX.ChildrenBean childrenBean = arrayList.get(i);
            String name = childrenBean.getName();
            fragment = new AppInfoFragment(name);
            Bundle args = new Bundle();
            args.putString(ApplicationConstant.ID, childrenBean.getId() + "");
            fragment.setArguments(args);
            list.add(fragment);
            titles.add(name);
        }

        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), list);
        adapter.setTitles(titles);
        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);
    }
}
