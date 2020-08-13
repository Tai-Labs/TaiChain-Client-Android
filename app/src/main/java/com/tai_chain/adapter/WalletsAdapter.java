package com.tai_chain.adapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.baseAdapter.base.MyBaseViewHolder;
import com.tai_chain.bean.WalletBean;
import com.tai_chain.UI.walletsetting.WalletSetting;
import com.tai_chain.utils.SharedPrefsUitls;

import java.util.List;

public class WalletsAdapter extends BaseQuickAdapter<WalletBean, MyBaseViewHolder> {

    public WalletsAdapter(int layoutResId, @Nullable List<WalletBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(MyBaseViewHolder helper, WalletBean item) {

        helper.setText(R.id.wallets_name, item.getName());
        String currentWalletID = SharedPrefsUitls.getInstance().getCurrentWallet();
        if (item.getId().equals(currentWalletID)) {
            helper.setImageResource(R.id.wallwts_icon, R.mipmap.current_wallet_icon);
            helper.setTextColor(R.id.wallets_name, mContext.getResources().getColor(R.color.zt_main));
        } else {
            helper.setImageResource(R.id.wallwts_icon, R.mipmap.head_wallet_icon);
            helper.setTextColor(R.id.wallets_name, mContext.getResources().getColor(R.color.zt_fff));
        }

        helper.getView(R.id.wallet_manager_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WalletSetting.class);
                intent.putExtra("wallet", item);
                mContext.startActivity(intent);
            }
        });


    }

    public void setTokens(List<WalletBean> tokens) {
        setNewData(tokens);
    }
}
