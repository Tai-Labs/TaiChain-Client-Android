package com.tai_chain.UI.dapp;

import com.tai_chain.base.BaseView;

public interface DappTransactionView extends BaseView{

    void onGasEstimate(String gas);
    void onGasPrice(String price);
    void onError(String err);
}
