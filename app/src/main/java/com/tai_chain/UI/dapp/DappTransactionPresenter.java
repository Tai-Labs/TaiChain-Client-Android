package com.tai_chain.UI.dapp;

import android.content.Context;
import android.widget.TextView;

import com.tai_chain.R;
import com.tai_chain.base.BasePresent;
import com.tai_chain.base.BaseUrl;
import com.tai_chain.base.Constants;
import com.tai_chain.bean.ResponseGasBean;
import com.tai_chain.http.HttpRequets;
import com.tai_chain.http.callback.JsonCallback;
import com.tai_chain.UI.tools.threads.TITExecutor;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.Util;
import com.tai_chain.view.MDialog;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DappTransactionPresenter extends BasePresent<DappTransactionView> {
    public void getGasEstimate(Context ctx, final String to, final String amount, final String data, TextView btn) {
        btn.setEnabled(false);
        btn.setText(ctx.getResources().getString(R.string.gas_evaluation));
        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                final String ethUrl = BaseUrl.getEthereumRpcUrl();
                final JSONObject payload = new JSONObject();
                final JSONArray params = new JSONArray();

                try {
                    JSONObject json = new JSONObject();
                    json.put("from", getCurrentWallet().getAddress());
                    json.put("to", to);
                    if (Util.isNullOrEmpty(amount) || new BigDecimal(amount) == BigDecimal.ZERO) {
                        json.put("value", "0x0");
                    } else {
                        json.put("value", "0x" + new BigInteger(amount, 10).toString(16));
                    }

                    json.put("data", data);
                    MyLog.i("etzTransaction----address=" + data);
                    params.put(json);
                    payload.put(Constants.JSONRPC, "2.0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    payload.put(Constants.METHOD, Constants.ETH_ESTIMATE_GAS);
                    payload.put(Constants.PARAMS, params);
                    payload.put(Constants.ID, "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyLog.i("gasLimit=== " + payload.toString());
                HttpRequets.postRequest(ethUrl, getClass(), payload.toString(), new JsonCallback<ResponseGasBean>() {
                    @Override
                    public void onSuccess(Response<ResponseGasBean> response) {
                        btn.setEnabled(true);
                        btn.setText(ctx.getResources().getString(R.string.TransactionDetails_next));
                        if (!Util.isNullOrEmpty(response.body().result)&&view!=null) {
                            view.onGasEstimate(response.body().result);
                        } else {
                            MDialog.showSimpleDialog(ctx, ctx.getResources().getString(R.string.WipeWallet_failedTitle), response.body() == null ? ctx.getResources().getString(R.string.socket_exception) : response.body().error.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseGasBean> response) {
                        btn.setEnabled(true);
                        btn.setText(ctx.getResources().getString(R.string.TransactionDetails_next));
                        MDialog.showSimpleDialog(ctx, ctx.getResources().getString(R.string.WipeWallet_failedTitle), response.body() == null ? ctx.getResources().getString(R.string.socket_exception) : response.body().error.message);
                    }
                });
            }
        });
    }

    public void getGasPrice(Context ctx) {
        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                final String ethUrl = BaseUrl.getEthereumRpcUrl();
                MyLog.d("Making rpc request to -> " + ethUrl);
                final JSONObject payload = new JSONObject();
                final JSONArray params = new JSONArray();
                try {
                    payload.put(Constants.METHOD, Constants.ETH_GAS_PRICE);
                    payload.put(Constants.PARAMS, params);
                    payload.put(Constants.ID, "1");
                    payload.put(Constants.JSONRPC, "2.0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyLog.i("getGasPrice=== " + payload.toString());
                HttpRequets.postRequest(ethUrl, getClass(), payload.toString(), new JsonCallback<ResponseGasBean>() {
                    @Override
                    public void onSuccess(Response<ResponseGasBean> response) {
                        if (response.body() != null && !Util.isNullOrEmpty(response.body().result)&&view!=null)
                            view.onGasPrice(response.body().result);
                        else
                            MDialog.showSimpleDialog(ctx, ctx.getResources().getString(R.string.WipeWallet_failedTitle), response.body() == null ? ctx.getResources().getString(R.string.socket_exception) : response.body().error.message);
                    }

                    @Override
                    public void onError(Response<ResponseGasBean> response) {
                        MDialog.showSimpleDialog(ctx, ctx.getResources().getString(R.string.WipeWallet_failedTitle), response.body() == null ? ctx.getResources().getString(R.string.socket_exception) : response.body().error.message);
                    }
                });
            }

        });

    }

}
