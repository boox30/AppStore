package com.yunarm.appstore.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.william.androidsdk.baseui.BaseFragment;
import com.william.androidsdk.utils.InputMethodUtils;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.adapters.ViewPagerFragmentAdapter;
import com.yunarm.appstore.bean.AppTypeBean;
import com.yunarm.appstore.fragments.AppInfoFragment;
import com.yunarm.appstore.fragments.AppTypeFragment;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private String TAG = "MainActivity";
    private FrameLayout contentLayout;
    private AppInfoFragment searchFragment;
    private boolean searchIsshowing =  false;

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
        contentLayout = findViewById(R.id.fragment_content);
        stepFragmentsWithViewPager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//指定Toolbar上的视图文件
        MenuItem searchItem = menu.findItem(R.id.ab_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //设置搜索栏的默认提示
        searchView.setQueryHint(getResources().getString(R.string.search));
        //默认刚进去就打开搜索栏
        searchView.setIconified(true);
        SearchView.SearchAutoComplete et = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        //设置搜索栏的默认提示，作用和setQueryHint相同
        et.setHint(getResources().getString(R.string.input_name_or_pkg));
        //设置提示文本的颜色
        et.setHintTextColor(Color.BLACK);
        //设置输入文本的颜色
        et.setTextColor(Color.BLACK);

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (InputMethodUtils.isSoftShowing(this)) {
            InputMethodUtils.showOrHide(this);
        } else if (searchIsshowing) {
            hideSearchFragment();
        } else {
            super.onBackPressed();
        }
    }

    private void hideSearchFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(searchFragment);
        transaction.commit();
        viewPager.setVisibility(View.VISIBLE);
        tabs.setVisibility(View.VISIBLE);
        searchIsshowing = false;
        searchFragment = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchIsshowing = false;
    }

    private void stepFragmentsWithViewPager() {
        final ArrayList<BaseFragment> list = new ArrayList<>();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("tag", "==========query======" + query);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (searchFragment != null && transaction.isAddToBackStackAllowed()) {
            transaction.remove(searchFragment);
        }
        searchFragment = new AppInfoFragment();
        Bundle args = new Bundle();
        args.putString(ApplicationConstant.SEARCH, query);
        searchFragment.setArguments(args);
        transaction.add(R.id.fragment_content, searchFragment);
        transaction.commit();
        searchIsshowing = true;
        viewPager.setVisibility(View.INVISIBLE);
        tabs.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
