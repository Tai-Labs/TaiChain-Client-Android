package com.tai_chain.UI.walletoperation.send;

import com.tai_chain.base.BaseView;

public interface SendView extends BaseView{

    void sendGasLimitSuccess(String to,String gas,String data);
    void sendViewError(String str);
    void sendGasPriceSuccess(String price);
}
