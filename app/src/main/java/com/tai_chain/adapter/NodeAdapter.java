package com.tai_chain.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.baseAdapter.base.MyBaseViewHolder;
import com.tai_chain.bean.NodeEntity;
import com.tai_chain.utils.SharedPrefsUitls;

import java.util.List;

public class NodeAdapter extends BaseQuickAdapter<NodeEntity, MyBaseViewHolder> {

    public NodeAdapter(int layoutResId, @Nullable List<NodeEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(MyBaseViewHolder helper, NodeEntity item) {
        helper.setText(R.id.node_item_address, item.getNodeAddress());
        helper.setText(R.id.node_item_node, item.getNode());
        String node = SharedPrefsUitls.getInstance().getCurrentNode();
        if (item.getNode().equalsIgnoreCase(node)) {
            helper.getView(R.id.node_item_img).setBackgroundResource(R.mipmap.checked_icon);
            helper.setTextColor(R.id.node_item_address, mContext.getResources().getColor(R.color.zt_lu));
        } else {
            helper.getView(R.id.node_item_img).setBackgroundResource(R.mipmap.unchecked_icon);
            helper.setTextColor(R.id.node_item_address, mContext.getResources().getColor(R.color.zt_fff));
        }
    }

    public void setItems(List<NodeEntity> items) {
        setNewData(items);
    }
}
