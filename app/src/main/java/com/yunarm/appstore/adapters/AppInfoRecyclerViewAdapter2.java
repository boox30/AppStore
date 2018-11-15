package com.yunarm.appstore.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yunarm.appstore.BR;
import com.yunarm.appstore.R;
import com.yunarm.appstore.app.AppInstallTaskManagerThread;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.events.ClickEvent;

import java.util.List;

public class AppInfoRecyclerViewAdapter2 extends RecyclerView.Adapter<AppInfoRecyclerViewAdapter2.BaseViewHolder> {

    private List<AppInfoBean.MessageBean.DataBean> itemDatas;
    private int defaultLayout;
    private Context mContext;

    public AppInfoRecyclerViewAdapter2(List<AppInfoBean.MessageBean.DataBean> itemDatas, int defaultLayout){
        this.itemDatas = itemDatas;
        this.defaultLayout = defaultLayout;
        new Thread(new AppInstallTaskManagerThread()).start();
    }

    public int getItemLayout(AppInfoBean.MessageBean.DataBean itemData){
        return defaultLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemLayout(itemDatas.get(position));
    }

    @Override
    public AppInfoRecyclerViewAdapter2.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), defaultLayout, parent, false);
        return new BaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AppInfoRecyclerViewAdapter2.BaseViewHolder holder, int position) {
        holder.binding.setVariable(BR.itemBean, itemDatas.get(position));
        holder.binding.setVariable(BR.clickEvent, new ClickEvent());
        itemDatas.get(position).setInstallState(mContext.getResources().getString(R.string.install_app));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return itemDatas == null ? 0 : itemDatas.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;

        BaseViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}