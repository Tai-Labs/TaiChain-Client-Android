package com.tai_chain.UI.main.property;

import com.tai_chain.app.MyApp;
import com.tai_chain.base.BasePresent;
import com.tai_chain.base.BaseUrl;
import com.tai_chain.bean.MinerMoreBean;
import com.tai_chain.bean.Token;
import com.tai_chain.bean.TokenInfo;
import com.tai_chain.bean.WalletBean;
import com.tai_chain.http.HttpRequets;
import com.tai_chain.http.callback.JsonCallback;
import com.tai_chain.UI.walletmanage.WalletsMaster;
import com.tai_chain.sqlite.BalanceDataSource;
import com.tai_chain.sqlite.WalletDataStore;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.SharedPrefsUitls;
import com.tai_chain.utils.TokenUtil;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyPresenter extends BasePresent<PropertyView> {


    public List<WalletBean> getWalletList() {
        return WalletDataStore.getInstance().queryAllWallets();
    }

    public List<Token> getTokens(WalletBean wallet) {
        List<Token> tokens = new ArrayList<>();
        Map<String, String> balances = BalanceDataSource.getInstance().getWalletTokensBalance(wallet.getId());
        MyLog.i("------------------1" + balances.toString());
        Token token;
        List<String> tokenList;
        List<TokenInfo> tokenInfos = null;
        if (wallet.getId().startsWith("BTC")) {
            tokenInfos = TokenUtil.getTokenItems(MyApp.getmInstance());
            token = new Token(new TokenInfo(wallet.getAddress(), "Bitcoin", "BTC", "", wallet.getStartColor(), wallet.getEndColor(), wallet.getDecimals()), balances.get("BTC"));
        } else if (wallet.getId().startsWith("ETH")) {
            tokenInfos = TokenUtil.getEthTokens(MyApp.getmInstance());
            token = new Token(new TokenInfo(wallet.getAddress(), "Ethereum", "ETH", "", wallet.getStartColor(), wallet.getEndColor(), wallet.getDecimals()), balances.get("ETH"));
        } else {
            tokenInfos = TokenUtil.getTokenItems(MyApp.getmInstance());
            token = new Token(new TokenInfo(wallet.getAddress(), "TIT", "TIT", "", wallet.getStartColor(), wallet.getEndColor(), wallet.getDecimals()), balances.get("TIT"));
        }
        tokens.add(token);
        MyLog.i("------------------2" + balances.toString());
        tokenList = SharedPrefsUitls.getInstance().getWalletTokenList(wallet.getId());
        if (tokenList == null) return tokens;
        for (String str : tokenList) {
            for (TokenInfo tokenInfo : tokenInfos) {
                MyLog.i("------------------3" + str + "****" + tokenInfo.symbol);
                if (str.equals(tokenInfo.symbol)) {
                    tokens.add(new Token(tokenInfo, balances.get(str)));
                    MyLog.i("------------------4" + balances.get(str));

                }
            }
        }
        return tokens;

    }

    public BigDecimal totalAssets(List<Token> tokens) {
        BigDecimal assets = new BigDecimal("0");

        for (Token token : tokens) {
            String iso = token.tokenInfo.symbol;
            assets = assets.add(WalletsMaster.getInstance().getWalletByIso(MyApp.getBreadContext(), iso).getFiatBalance(iso, token.balance));
        }
        return assets;
    }

    public void getMinerMore(String address) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("address", address);
        HttpRequets.postRequest(BaseUrl.getMiner_more(address), getClass(), hashMap, new JsonCallback<MinerMore>() {

            @Override
            public MinerMore convertResponse(okhttp3.Response response) throws Throwable {
                String minerJson = response.body().string();
                SharedPrefsUitls.getInstance().setMinerInfo(minerJson);
                Gson gson=new Gson();
                //序列化
                MinerMore user=gson.fromJson(minerJson,MinerMore.class);
                return user;
            }

            @Override
            public void onSuccess(Response<MinerMore> response) {
                if (view == null) return;
                if (response.body() != null && response.body().resp != null&&response.body().resp.getMiner()!=null) {
                    String vip = response.body().resp.getMiner().getLevel();
                    MyLog.i("getMinerMore====" + vip);
                    view.setMinerMore(vip);
                } else {
                    view.setMinerMore("");
                }

            }

            @Override
            public void onError(Response<MinerMore> response) {
                if (view == null) return;
                view.setMinerMore("");
                super.onError(response);
            }
        });


    }

    class MinerMore {
        MinerMoreBean resp;
    }
}
