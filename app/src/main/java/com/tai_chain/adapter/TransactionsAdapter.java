package com.tai_chain.adapter;

import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.baseAdapter.base.MyBaseViewHolder;
import com.tai_chain.base.Constants;
import com.tai_chain.bean.TokenInfo;
import com.tai_chain.bean.TransactionRecords;
import com.tai_chain.utils.CurrencyUtils;
import com.tai_chain.utils.DateUtil;
import com.tai_chain.utils.Util;

import java.math.BigDecimal;
import java.util.List;

public class TransactionsAdapter extends BaseQuickAdapter<TransactionRecords, MyBaseViewHolder> {

    private TokenInfo tokenInfo;

    public TransactionsAdapter(int layoutResId, @Nullable List<TransactionRecords> data, TokenInfo tokenInfo) {
        super(layoutResId, data);
        this.tokenInfo = tokenInfo;
    }

    @Override
    protected void convert(MyBaseViewHolder helper, TransactionRecords item) {
        String commentString = "";
        boolean received = item.isReceived;
        if (Util.isNullOrEmpty(item.blockNumber)) {
            helper.setBackgroundRes(R.id.tx_status_icon, R.mipmap.asset_log_verify);
            helper.setText(R.id.transaction_status, mContext.getString(R.string.TransactionDetails_confirming));
        } else if (item.status.equalsIgnoreCase("0x1")) {

            if (received) {
                helper.setBackgroundRes(R.id.tx_status_icon, R.mipmap.asset_log_zhuanru);
            } else {
                helper.setBackgroundRes(R.id.tx_status_icon, R.mipmap.asset_log_zhuanchu);
            }
            helper.setText(R.id.transaction_status, mContext.getString(R.string.transfer_ok));

        } else {
            helper.setBackgroundRes(R.id.tx_status_icon, R.mipmap.asset_log_wrong);
            helper.setText(R.id.transaction_status, mContext.getString(R.string.transfer_fail));
        }

        BigDecimal cryptoAmount = new BigDecimal(item.value).abs();
        String formattedAmount = CurrencyUtils.getFormattedAmount(mContext, tokenInfo.symbol, cryptoAmount, Constants.MAX_DECIMAL_PLACES_FOR_UI);
        helper.setText(R.id.tx_amount, item.value + "TIT");
        helper.setText(R.id.tx_description, !commentString.isEmpty() ? commentString : (!received ? item.to : item.from));
        //if it's 0 we use the current time.
        long timeStamp = Long.valueOf(item.date) == 0 ? System.currentTimeMillis() : Long.valueOf(item.date) * DateUtils.SECOND_IN_MILLIS;

        String shortDate = DateUtil.getShortDate(timeStamp);

        helper.setText(R.id.tx_date, shortDate);


    }

    public void setTokens(List<TransactionRecords> tokens) {
        setNewData(tokens);
    }
}
