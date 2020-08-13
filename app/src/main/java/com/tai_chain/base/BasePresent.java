package com.tai_chain.base;

import com.tai_chain.bean.WalletBean;
import com.tai_chain.sqlite.WalletDataStore;
import com.tai_chain.utils.SharedPrefsUitls;

public abstract class BasePresent<T>{
    public T view;

    public void attach(T view){
        this.view = view;
    }

    public void detach(){
        this.view = null;
    }

    public WalletBean getCurrentWallet() {
        String wid = SharedPrefsUitls.getInstance().getCurrentWallet();
        return WalletDataStore.getInstance().queryWallet(wid);
    }
}