package com.tai_chain.UI.walletoperation.wallet;

import com.tai_chain.base.BaseView;
import com.tai_chain.bean.TransactionRecords;

import java.util.List;

public interface WalletView extends BaseView {

    void walletTransactions(List<TransactionRecords> list);
    void showError(String err);

}
