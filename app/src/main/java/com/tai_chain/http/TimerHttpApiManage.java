package com.tai_chain.http;

import android.content.Context;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.WorkerThread;

import com.tai_chain.UI.tools.threads.TITExecutor;
import com.tai_chain.app.MyApp;
import com.tai_chain.base.BaseUrl;
import com.tai_chain.bean.CurrencyEntity;
import com.tai_chain.bean.ETHRatesBean;
import com.tai_chain.bean.TitRateBean;
import com.tai_chain.bean.ResponseBean;
import com.tai_chain.blockchain.TitWalletManager;
import com.tai_chain.http.callback.JsonCallback;
import com.tai_chain.sqlite.RatesDataSource;
import com.tai_chain.utils.ActivityUtils;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.SharedPrefsUitls;
import com.tai_chain.utils.Util;
import com.lzy.okgo.model.Response;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class TimerHttpApiManage {

    private static TimerHttpApiManage instance;
    private Timer timer;

    private TimerTask timerTask;

    private Handler handler;


    private TimerHttpApiManage() {
        handler = new Handler();
    }

    public static TimerHttpApiManage getInstance() {

        if (instance == null) {
            instance = new TimerHttpApiManage();
        }
        return instance;
    }

    /**
     * 法币相对于Btc的汇率
     *
     * @param context
     */
    @WorkerThread
    private void updateRates(Context context) {
        if (ActivityUtils.isMainThread()) {
            throw new NetworkOnMainThreadException();
        }
        HttpRequets.getRequets(BaseUrl.HTTP_UPDATE_RATES, getClass(), new HashMap<String, String>(), new JsonCallback<ResponseBean<List<CurrencyEntity>>>() {

            @Override
            public void onSuccess(com.lzy.okgo.model.Response<ResponseBean<List<CurrencyEntity>>> response) {
                if (response.body() == null) return;
                List<CurrencyEntity> list = response.body().data;
                Set<CurrencyEntity> set = new LinkedHashSet<>();
                for (CurrencyEntity ce : list) {
//                    if (ce.code.equalsIgnoreCase("USD")) {
//                        String code = "BTC";
//                        String name = "SeekChain";
//                        String rate = (1/Float.valueOf(ce.rate))* Float.valueOf(seekusdt) + "";
//                        MyLog.i("updateErc20Rates==" + rate);
//                        MyLog.i("updateErc20Rates==" + seekusdt);
//                        String iso = "TIT";
//                        CurrencyEntity TIT = new CurrencyEntity(code, name, Float.valueOf(rate), iso);
//                        set.add(TIT);
//                    }
                    set.add(ce);
                }
                if (set.size() > 0)
                    RatesDataSource.getInstance(context).putCurrencies(context, set);
            }
        });


    }


    private void initializeTimerTask(final Context context) {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                            @Override
                            public void run() {
                                updateData(context);
                            }
                        });
                    }
                });
            }
        };
    }

    @WorkerThread
    private void updateData(final Context context) {

        if (MyApp.isAppInBackground(context)) {
            MyLog.e("doInBackground: Stopping timer, no activity on.");
            stopTimerTask();
            return;
        }
        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                updateETZRates(context);
                updateErc20Rates(context);
                updateCurrentBalances();
            }
        });

        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                //get each wallet's rates
                updateRates(context);

            }
        });

    }

    /**
     * TIT相对于Btc汇率
     *
     * @param context
     */
    @WorkerThread
    private synchronized void updateETZRates(Context context) {

        HttpRequets.getRequets(BaseUrl.HTTP_SEEK_RATE, getClass(), new HashMap<String, String>(), new JsonCallback<ResponseBean<TitRateBean>>() {
            @Override
            public void onSuccess(Response<ResponseBean<TitRateBean>> response) {
                MyLog.i(response.toString());
                if (response.body() != null) {
                    Set<CurrencyEntity> tmp = new LinkedHashSet<>();
                    TitRateBean bean = response.body().data;
                    float tr = 10000f / Float.valueOf(bean.BTCPrice) * Float.valueOf(bean.TITPrice) / 10000f;
                    String code = "BTC";
                    String name = "TIT";
                    String rate = tr + "";
                    String iso = "TIT";
                    CurrencyEntity tit = new CurrencyEntity(code, name, Float.valueOf(rate), iso);
                    tmp.add(tit);
                    RatesDataSource.getInstance(context).putCurrencies(context, tmp);
                }

            }

            @Override
            public void onError(Response<ResponseBean<TitRateBean>> response) {
            }
        });
    }

    /***
     * 代币包括EASH相对于Btc汇率
     * @param context
     */
    @WorkerThread
    private synchronized void updateErc20Rates(Context context) {

//        if (Util.isNullOrEmpty(seekusdt)) return;
//        HttpRequets.getRequets(BaseUrl.HTTP_ETH_RATES, getClass(), new HashMap<String, String>(), new JsonCallback<List<ETHRatesBean>>() {
//
//            @Override
//            public void onSuccess(Response<List<ETHRatesBean>> response) {
//                List<ETHRatesBean> list = response.body();
//                Set<CurrencyEntity> tmp = new LinkedHashSet<>();
//                for (ETHRatesBean eb : list) {
//                    if (eb.symbol.equalsIgnoreCase("USDT")) {
//                        String code = "BTC";
//                        String name = "TIT";
//                        String rate = Float.valueOf(eb.price_btc) * Float.valueOf(seekusdt) + "";
////                        MyLog.i("updateErc20Rates==" + eb.price_btc);
////                        MyLog.i("updateErc20Rates==" + seekusdt);
//                        String iso = "TIT";
//                        CurrencyEntity tit = new CurrencyEntity(code, name, Float.valueOf(rate), iso);
//                        tmp.add(tit);
//                    }
//                }
//                RatesDataSource.getInstance(context).putCurrencies(context, tmp);
//            }
//        });

    }

    @WorkerThread
    private synchronized void updateCurrentBalances() {
        //获取当前钱包
        String wid = SharedPrefsUitls.getInstance().getCurrentWallet();
        if (Util.isNullOrEmpty(wid)) return;
        TitWalletManager.getInstance().setTokenList(SharedPrefsUitls.getInstance().getWalletTokenList(wid));
        TitWalletManager.getInstance().updateBalance(wid);

    }

    public void startTimer(Context context) {
        //set a new Timer
        if (timer != null) return;
        timer = new Timer();
        MyLog.e("startTimer: started...");
        //initialize the TimerTask's job
        initializeTimerTask(context);

        timer.schedule(timerTask, 1000, 15000);
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


}
