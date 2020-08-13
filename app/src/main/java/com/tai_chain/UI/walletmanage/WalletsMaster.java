package com.tai_chain.UI.walletmanage;

import android.content.Context;

import com.tai_chain.bean.TokenInfo;
import com.tai_chain.blockchain.BaseWalletManager;
import com.tai_chain.blockchain.TitWalletManager;
import com.tai_chain.blockchain.TokenWalletManager;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.TokenUtil;
import com.tai_chain.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class WalletsMaster {
    private static WalletsMaster instance;

    public synchronized static WalletsMaster getInstance() {
        if (instance == null) {
            instance = new WalletsMaster();
        }
        return instance;
    }

    //return the needed wallet for the iso
    public BaseWalletManager getWalletByIso(Context app, String iso) {

        if (Util.isNullOrEmpty(iso)) {
            throw new RuntimeException("getWalletByIso with iso = null, Cannot happen!");
        } else if (iso.equalsIgnoreCase("TIT")) {
            return TitWalletManager.getInstance();
        } else if (isIsoErc20(app, iso)) {
            return TokenWalletManager.getInstance(app);
        }
        return null;
    }

    public boolean isIsoErc20(Context app, String iso) {
        if (Util.isNullOrEmpty(iso)) return false;
        try {
            ArrayList<TokenInfo> tokens = TokenUtil.getTokenItems(app);
            for (TokenInfo token : tokens) {
                if ((token != null && !Util.isNullOrEmpty(token.symbol)) && iso.equals(token.symbol)) {
                    return true;
                }
            }
            for (TokenInfo token : TokenUtil.getEthTokens(app)) {
                if ((token != null && !Util.isNullOrEmpty(token.symbol)) && iso.equals(token.symbol)) {
                    return true;
                }
            }

        } catch (Exception e) {
            MyLog.e("isIsoErc20出错");
        }
        return false;
    }

    public boolean isIsoCrypto(Context app, String iso) {
//        MyLog.i( "isIsoCrypto: ios===="+iso);
        List<TokenInfo> list = new ArrayList<>(TokenUtil.getTokenItems(app));
        for (TokenInfo tokenInfo : list) {
            if (tokenInfo.symbol.equalsIgnoreCase(iso)) {
                return true;
            }
        }
        if (iso.equalsIgnoreCase("TIT") || iso.equalsIgnoreCase("ETH")) return true;
        return false;
    }
}
