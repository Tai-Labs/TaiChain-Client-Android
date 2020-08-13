package com.tai_chain.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.baseAdapter.base.MyBaseViewHolder;
import com.tai_chain.bean.ContactsEntity;

import java.util.List;

public class ContactsAdapter extends BaseQuickAdapter<ContactsEntity, MyBaseViewHolder> {
    public ContactsAdapter(int layoutResId, @Nullable List<ContactsEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(MyBaseViewHolder helper, ContactsEntity item) {

        helper.setText(R.id.contacts_item_name,item.getCname());
        helper.setText(R.id.contacts_item_address,item.getWalletAddress());

    }
    public void setItems(List<ContactsEntity> items) {
        setNewData(items);
    }
}
