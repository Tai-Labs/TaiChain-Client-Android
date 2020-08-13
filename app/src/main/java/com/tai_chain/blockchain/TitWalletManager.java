package com.tai_chain.blockchain;

import android.content.Context;

import com.tai_chain.app.MyApp;
import com.tai_chain.base.BaseUrl;
import com.tai_chain.base.Constants;
import com.tai_chain.bean.BalanceEntity;
import com.tai_chain.bean.CurrencyEntity;
import com.tai_chain.bean.RPCBalanceBean;
import com.tai_chain.bean.ResponseResultBean;
import com.tai_chain.bean.TokenInfo;
import com.tai_chain.bean.WalletBean;
import com.tai_chain.http.HttpRequets;
import com.tai_chain.http.callback.JsonCallback;
import com.tai_chain.UI.tools.threads.TITExecutor;
import com.tai_chain.sqlite.BalanceDataSource;
import com.tai_chain.sqlite.RatesDataSource;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.SharedPrefsUitls;
import com.tai_chain.utils.TokenUtil;
import com.tai_chain.utils.Util;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

public class TitWalletManager implements BaseWalletManager {
    private WalletBean etzWallet;

    //1ETH = 1000000000000000000 WEI
    public static final String ETHER_WEI = "1000000000000000000";
    //Max amount in ether
    public static final String MAX_ETH = "90000000";
    private final BigDecimal MAX_WEI = new BigDecimal(MAX_ETH).multiply(new BigDecimal(ETHER_WEI)); // 90m ETH * 18 (WEI)
    private final BigDecimal ONE_ETH = new BigDecimal(ETHER_WEI);
    private static TitWalletManager etzInstance;
    private static String mAddress;
    private List<TokenInfo> tokenList;

    public static synchronized TitWalletManager getInstance() {
        if (etzInstance == null) {
            etzInstance = new TitWalletManager();
            mAddress = SharedPrefsUitls.getInstance().getCurrentWalletAddress();
        }
        return etzInstance;
    }

    @Override
    public void updateBalance(String wid) {
        getEtherBalance(wid, mAddress);
        for (TokenInfo token : tokenList) {
            getTokenBalance(wid, token.address, mAddress, token.symbol);
        }
    }

    @Override
    public void setmAddress(String address) {
        mAddress = address;
    }


    @Override
    public BigDecimal getCryptoForSmallestCrypto(Context app, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return amount.divide(ONE_ETH, 5, BigDecimal.ROUND_DOWN);
    }

    @Override
    public int getMaxDecimalPlaces(Context app, String iso) {
        return Constants.MAX_DECIMAL_PLACES;
    }

    public String getmAddress() {
        return mAddress;
    }

    public List<TokenInfo> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<String> list) {
        tokenList = TokenUtil.getCurrentEtzTokens(list);
    }

    protected void getEtherBalance(final String wid, final String address) {
        if (MyApp.isAppInBackground(MyApp.getBreadContext())) {
            return;
        }
        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                final String ethRpcUrl = BaseUrl.getEthereumRpcUrl();
                final JSONObject payload = new JSONObject();
                final JSONArray params = new JSONArray();
                try {
                    payload.put(Constants.JSONRPC, "2.0");
                    payload.put(Constants.METHOD, Constants.ETH_BALANCE);
                    params.put(address);
                    params.put(Constants.LATEST);
                    payload.put(Constants.PARAMS, params);
                    payload.put(Constants.ID, wid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyLog.i("getETZBalanceurl=" + ethRpcUrl);
                MyLog.i("getETZBalanceurl=" + payload.toString());
                HttpRequets.postRequest(ethRpcUrl, getClass(), payload.toString(), new JsonCallback<RPCBalanceBean>() {
                    @Override
                    public void onSuccess(Response<RPCBalanceBean> response) {
                        String balance = response.body().result;
                        MyLog.i("getETZBalanceurl=" + response.body().result);
                        if (Util.isNullOrEmpty(balance)) return;
                        String bigb;
                        if (balance.startsWith("0x")) {
                            balance = balance.substring(2, balance.length());
                            bigb = new BigInteger(balance, 16).toString();
                            MyLog.i("++++++++++++" + balance);
                            MyLog.i("++++++++++++" + bigb);
                        } else {
                            bigb = new BigInteger(balance, 16).toString();
                        }
                        BalanceDataSource.getInstance().insertTokenBalance(new BalanceEntity(wid + "TIT", wid, "TIT", bigb));

                    }

                    @Override
                    public void onError(Response<RPCBalanceBean> response) {
                        MyLog.d("getEtherBalance-----onError:" + response.getException().getMessage());
                        super.onError(response);
                    }
                });
            }
        });
    }

    protected void getTokenBalance(final String wid, final String contractAddress, final String address, final String symbol) {
        if (MyApp.isAppInBackground(MyApp.getBreadContext())) {
            MyLog.e("getTokenBalance: App in background!");
            return;
        }
        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {

                String ethRpcUrl = BaseUrl.getEtzTokenBalance(address, contractAddress);

                MyLog.i("run: token balance url==" + ethRpcUrl);
                HttpRequets.getRequets(ethRpcUrl, getClass(), new HashMap<String, String>(), new JsonCallback<ResponseResultBean<String>>() {
                    @Override
                    public void onSuccess(Response<ResponseResultBean<String>> response) {
                        String balance = response.body().result;
                        BalanceDataSource.getInstance().insertTokenBalance(new BalanceEntity(wid + symbol, wid, symbol, balance));
                    }

                });
            }
        });
    }

    @Override
    public BigDecimal getFiatBalance(String iso, String balance) {

        return getFiatForSmallestCrypto(iso, new BigDecimal(Util.isNullOrEmpty(balance) ? "0" : balance), null);
    }

    @Override
    public BigDecimal getFiatForSmallestCrypto(String iso, BigDecimal amount, CurrencyEntity ent) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        String fiatIso = SharedPrefsUitls.getInstance().getPreferredFiatIso();
        if (ent != null) {
            //passed in a custom CurrencyEntity
            //get crypto amount
            BigDecimal cryptoAmount = amount.divide(ONE_ETH, 8, Constants.ROUNDING_MODE);
            //multiply by fiat rate
            return cryptoAmount.multiply(new BigDecimal(ent.rate));
        }
        //get crypto amount
        BigDecimal cryptoAmount = amount.divide(ONE_ETH, 8, Constants.ROUNDING_MODE);
        MyLog.i("fiatIso=="+fiatIso);
        BigDecimal fiatData = getFiatForEth(iso, cryptoAmount, fiatIso);
        if (fiatData == null) return BigDecimal.ZERO;
        return fiatData;
    }

    @Override
    public BigDecimal getFiatExchangeRate(Context app, String iso) {
        BigDecimal fiatData = getFiatForEth("TIT", new BigDecimal(1), SharedPrefsUitls.getInstance().getPreferredFiatIso());
        if (fiatData == null) return new BigDecimal("0");
        return fiatData; //dollars
    }

    //pass in a eth amount and return the specified amount in fiat
    //ETH rates are in BTC (thus this math)
    private BigDecimal getFiatForEth(String iso, BigDecimal ethAmount, String code) {
        //fiat rate for btc
        CurrencyEntity btcRate = RatesDataSource.getInstance(MyApp.getmInstance()).getCurrencyByCode(MyApp.getmInstance(), "BTC", code);
        //Btc rate for ether
        CurrencyEntity ethBtcRate = RatesDataSource.getInstance(MyApp.getmInstance()).getCurrencyByCode(MyApp.getmInstance(), "TIT", "BTC");
        if (btcRate == null) {
            MyLog.e("getUsdFromBtc: No USD rates for BTC");
            return BigDecimal.ZERO;
        }
        if (ethBtcRate == null) {
            MyLog.e("getUsdFromBtc: No BTC rates for ETH");
            return BigDecimal.ZERO;
        }

        return ethAmount.multiply(new BigDecimal(ethBtcRate.rate)).multiply(new BigDecimal(btcRate.rate));
//        return ethAmount.multiply(new BigDecimal("1")).multiply(new BigDecimal("1"));
    }

}
