package com.tai_chain.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.baseAdapter.base.MyBaseViewHolder;
import com.tai_chain.bean.NodeEntity;
import com.tai_chain.bean.languageEntity;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.SPLUtil;
import com.tai_chain.utils.SharedPrefsUitls;

import java.util.List;
import java.util.Locale;

public class LanguageAdapter extends BaseQuickAdapter<languageEntity, MyBaseViewHolder> {

    public LanguageAdapter(int layoutResId, @Nullable List<languageEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(MyBaseViewHolder helper, languageEntity item) {
        helper.setText(R.id.node_item_address, item.getLanguage());


        int lid = SPLUtil.getInstance(mContext).getSelectLanguage();
        String lang = SPLUtil.getInstance(mContext).getSystemCurrentLocal().getLanguage();
        MyLog.i("*-*-*-*-*-*-*"+lang);
        MyLog.i("*-*-*-*-*-*-*"+lid);

        if (lid==0){
//            String lang = Locale.getDefault().getLanguage();
            if (lang.equalsIgnoreCase("zh")
                    &&item.getLid()==1) {
                helper.getView(R.id.node_item_img).setBackgroundResource(R.mipmap.checked_icon);
                helper.setTextColor(R.id.node_item_address,mContext.getResources().getColor(R.color.zt_lu));
            } else if (lang.equalsIgnoreCase("ko")&&item.getLid()==3) {
                helper.getView(R.id.node_item_img).setBackgroundResource(R.mipmap.checked_icon);
                helper.setTextColor(R.id.node_item_address,mContext.getResources().getColor(R.color.zt_lu));
            } else if(!lang.equalsIgnoreCase("zh")&&!lang.equalsIgnoreCase("ko")&&item.getLid()==2) {
                helper.getView(R.id.node_item_img).setBackgroundResource(R.mipmap.checked_icon);
                helper.setTextColor(R.id.node_item_address,mContext.getResources().getColor(R.color.zt_lu));
            }
        }else if (item.getLid()==lid) {
            helper.getView(R.id.node_item_img).setBackgroundResource(R.mipmap.checked_icon);
            helper.setTextColor(R.id.node_item_address,mContext.getResources().getColor(R.color.zt_lu));
        } else {
            helper.getView(R.id.node_item_img).setBackgroundResource(R.mipmap.unchecked_icon);
            helper.setTextColor(R.id.node_item_address,mContext.getResources().getColor(R.color.zt_fff));
        }
    }

    public void setItems(List<languageEntity> items) {
        setNewData(items);
    }
}
