package com.tai_chain.adapter.baseAdapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.baseAdapter.base.MyBaseViewHolder;
import com.tai_chain.bean.WalletBean;
import com.tai_chain.UI.walletsetting.WalletSetting;
import com.tai_chain.sqlite.BalanceDataSource;
import com.tai_chain.utils.CurrencyUtils;
import com.tai_chain.utils.Util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class WalletManageAdapter extends BaseQuickAdapter<WalletBean, MyBaseViewHolder> {

    public WalletManageAdapter(int layoutResId, @Nullable List<WalletBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(MyBaseViewHolder helper, WalletBean item) {
        Map<String, String> balances = BalanceDataSource.getInstance().getWalletTokensBalance(item.getId());
        helper.setText(R.id.wallet_item_name, item.getName());
        helper.setText(R.id.wallet_item_address, item.getAddress());

        if (balances.containsKey("TIT")) {
            String cryptoBalance = CurrencyUtils.getFormattedAmount(mContext, "TIT", new BigDecimal(Util.isNullOrEmpty(balances.get("TIT")) ? "0" : balances.get("TIT")));
            helper.setText(R.id.wallet_item_balance, cryptoBalance);
        }
        else {
            helper.setText(R.id.wallet_item_balance, "0 TIT");
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
