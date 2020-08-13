package com.tai_chain.UI.walletmanage.createwallet;

import com.tai_chain.base.BaseView;

public interface CreateWalletView extends BaseView {


    void getWalletSuccess(String address);
    void getWalletFail(String msg);


}
