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
import com.yunarm.appstore.app.ApplicationHelper;
import com.yunarm.appstore.bean.AppInfoBean;
import com.yunarm.appstore.events.ClickEvent;

import java.util.List;

public class AppInfoRecyclerViewAdapter2 extends RecyclerView.Adapter<AppInfoRecyclerViewAdapter2.BaseViewHolder> {

    private List<AppInfoBean.MessageBean.DataBean> itemDatas;
    private int defaultLayout;
    private Context mContext;

    public AppInfoRecyclerViewAdapter2(int defaultLayout){
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
        AppInfoBean.MessageBean.DataBean dataBean = itemDatas.get(position);
        holder.binding.setVariable(BR.itemBean, itemDatas.get(position));
        holder.binding.setVariable(BR.clickEvent, new ClickEvent());
        if (ApplicationHelper.isApplicationAvilible(mContext.getPackageManager(), dataBean.getPackageX())) {
            itemDatas.get(position).setInstallState(mContext.getResources().getString(R.string.uninstall_app));
        } else {
            itemDatas.get(position).setInstallState(mContext.getResources().getString(R.string.install_app));
        }
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

    /**
     * 设置新的数据
     */
    public void setData(List<AppInfoBean.MessageBean.DataBean> data) {
        itemDatas = data;
        notifyDataSetChanged();
    }

    public void addDates(List<AppInfoBean.MessageBean.DataBean> data) {
        if (data == null || data.size() == 0) return;

        if (itemDatas == null || itemDatas.size() == 0) {
            setData(data);
        }else {
            itemDatas.addAll(itemDatas.size(), data);
            notifyItemRangeInserted(itemDatas.size() - data.size() + 1, data.size());
        }
    }

    public List<AppInfoBean.MessageBean.DataBean> getItemDatas() {
        return itemDatas;
    }
}