package com.yunarm.appstore.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.william.androidsdk.baseui.BaseLazyFragment;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.ViewPagerFragmentAdapter;
import com.yunarm.appstore.bean.AppTypeBean;
import com.yunarm.appstore.fragments.AppTypeFragment;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        stepFragmentsWithViewPager();
    }


    private void stepFragmentsWithViewPager() {
        final ArrayList<BaseLazyFragment> list = new ArrayList<>();
        final AppListHelper instance = AppListHelper.getInstance();
        instance.getAppTypeList(this, new LoadFinishCallback() {
            @Override
            public void onLoadDataFinish() {
                List<String> bigTypes = instance.getBigTypes();
                AppTypeFragment fragment;
                String type;
                for (int i = 0; i < bigTypes.size(); i++) {
                    type = bigTypes.get(i);
                    fragment = new AppTypeFragment(type);
                    Bundle args = new Bundle();
                    ArrayList<AppTypeBean.MessageBean.ChildrenBeanX> types = instance.getTypes(type);
                    args.putParcelableArrayList(AppListHelper.DATA_LIST_TAG, types);
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
