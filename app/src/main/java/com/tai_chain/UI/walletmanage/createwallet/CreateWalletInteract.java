package com.tai_chain.UI.walletmanage.createwallet;

import com.tai_chain.bean.WalletBean;
import com.tai_chain.blockchain.walletutils.WalletUtils;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateWalletInteract {

    public Single<WalletBean> create(final String name, final String pwd, String confirmPwd) {
        return Single.fromCallable(() -> {
            WalletBean ethWallet = WalletUtils.generateMnemonic(name, pwd);
            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<WalletBean> loadWalletByKeystore(final String keystore, final String pwd, final String walletName, final String wallettype) {
        return Single.fromCallable(() -> {
            WalletBean ethWallet = WalletUtils.loadWalletByKeystore(keystore, pwd, walletName, wallettype);
            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<WalletBean> loadWalletByPrivateKey(final String privateKey, final String pwd, final String walletName, final String wallettype) {
        return Single.fromCallable(() -> WalletUtils.loadWalletByPrivateKey(privateKey, pwd, walletName, wallettype)
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<WalletBean> loadWalletByMnemonic(final String bipPath, final List<String> mnemonic, final String pwd, final String walletName, final String wallettype) {
        return Single.fromCallable(() -> WalletUtils.importMnemonic(bipPath, mnemonic, pwd, walletName, wallettype)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


    }

}
