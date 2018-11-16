package com.yunarm.appstore.adapters;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.william.androidsdk.baseui.BaseRecyclerViewAdapter;
import com.yunarm.appstore.ApplicationConstant;
import com.yunarm.appstore.R;
import com.yunarm.appstore.bean.AppTypeBean.MessageBean.ChildrenBeanX;
import com.yunarm.appstore.http.AppListHelper;
import com.yunarm.appstore.http.LoadFinishCallback;
import com.yunarm.appstore.http.LoadIconFinishCallback;

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
    public void onBindViewHolder(@NonNull final AppTypeRecyclerViewAdapter.AppTypeViewHolder appTypeViewHolder, final int i) {
        if (dataList == null) {
            dataList = getData();
        }
        appTypeViewHolder.setText(R.id.app_type_name, dataList.get(i).getName());
        List<ChildrenBeanX.ChildrenBean> children = dataList.get(i).getChildren();
        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < children.size(); j++) {
            stringBuilder.append(children.get(j).getName() + " ");
        }
        appTypeViewHolder.setText(R.id.app_type_des, stringBuilder.toString());

        AppListHelper.getInstance().getTypeIconId(getContext(), dataList.get(i).getId(), new LoadIconFinishCallback() {
            @Override
            public void onLoadDataFinish(String id) {
                String url = ApplicationConstant.HOST + "/app/" + id + "/icon.png";
                final ImageView imageIcon = (ImageView) appTypeViewHolder.findView(R.id.app_type_icon);
                Picasso.with(getContext()).load(url).placeholder(R.mipmap.app_store_icon).error(R.mipmap.app_store_icon).into(imageIcon);
            }
        });

    }

    public class AppTypeViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        public AppTypeViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }
    }
}
