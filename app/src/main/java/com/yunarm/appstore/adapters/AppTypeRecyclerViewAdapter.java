package com.yunarm.appstore.adapters;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.william.androidsdk.baseui.BaseRecyclerViewAdapter;
import com.yunarm.appstore.R;
import com.yunarm.appstore.bean.AppTypeBean.MessageBean.ChildrenBeanX;

import java.util.List;

public class AppTypeRecyclerViewAdapter extends BaseRecyclerViewAdapter<ChildrenBeanX, AppTypeRecyclerViewAdapter.AppTypeViewHolder> {

    private List<ChildrenBeanX> dataList;

    @NonNull
    @Override
    public AppTypeRecyclerViewAdapter.AppTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AppTypeViewHolder viewHolder = new AppTypeViewHolder(viewGroup, R.layout.item_app_type);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppTypeRecyclerViewAdapter.AppTypeViewHolder appTypeViewHolder, int i) {
        if (dataList == null) {
            dataList = getData();
        }
        appTypeViewHolder.setImage(R.id.app_type_icon, R.mipmap.ic_launcher);
        appTypeViewHolder.setText(R.id.app_type_name, dataList.get(i).getName());
        List<ChildrenBeanX.ChildrenBean> children = dataList.get(i).getChildren();
        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < children.size(); j++) {
            stringBuilder.append(children.get(j).getName() +" ");
        }
        appTypeViewHolder.setText(R.id.app_type_des, stringBuilder.toString());

    }

    public class AppTypeViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        public AppTypeViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }
    }
}
