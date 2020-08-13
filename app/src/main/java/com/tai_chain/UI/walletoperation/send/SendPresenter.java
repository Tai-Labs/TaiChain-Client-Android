package com.tai_chain.UI.walletoperation.send;

import android.content.Context;

import com.tai_chain.R;
import com.tai_chain.UI.tools.threads.TITExecutor;
import com.tai_chain.base.BasePresent;
import com.tai_chain.base.BaseUrl;
import com.tai_chain.base.Constants;
import com.tai_chain.bean.ResponseGasBean;
import com.tai_chain.http.HttpRequets;
import com.tai_chain.http.callback.JsonCallback;
import com.tai_chain.utils.MyLog;
import com.tai_chain.view.MDialog;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigInteger;


public class SendPresenter extends BasePresent<SendView> {
    public void getGasEstimate(Context ctx, final String to, String from, final String data) {
        TITExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                final String ethUrl = BaseUrl.getEthereumRpcUrl();
                final JSONObject payload = new JSONObject();
                final JSONArray params = new JSONArray();
                try {
                    JSONObject json = new JSONObject();
                    json.put("from", from);
                    json.put("to", to);
                    json.put("value", "0x0");
                    json.put("data", data);
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

                        if (response.body().result != null&&view!=null) {
                            String gas = response.body().result;
                            MyLog.i("getGasEstimate: gasLimit==" + gas);
                            String gasl = new BigInteger(gas.substring(2, gas.length()), 16).toString(10);
                            view.sendGasLimitSuccess(to, gasl, data);

                        } else if (response.body().error != null) {
                            MDialog.showSimpleDialog(ctx, ctx.getResources().getString(R.string.WipeWallet_failedTitle), response.body() == null ? ctx.getResources().getString(R.string.socket_exception) : response.body().error.message);
                        }

                    }

                    @Override
                    public void onError(Response<ResponseGasBean> response) {
                        super.onError(response);
                        if (response.body() != null&&response.body().error != null) {
                            MDialog.showSimpleDialog(ctx, ctx.getResources().getString(R.string.WipeWallet_failedTitle), response.body() == null ? ctx.getResources().getString(R.string.socket_exception) : response.body().error.message);
                        } else {
                            MDialog.showSimpleDialog(ctx, ctx.getResources().getString(R.string.WipeWallet_failedTitle), response.body() == null ? ctx.getResources().getString(R.string.socket_exception) : ctx.getResources().getString(R.string.socket_exception));
                        }

                    }
                });


            }
        });
    }

    protected void getGasPrice(Context ctx) {
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

                HttpRequets.postRequest(ethUrl, getClass(), payload.toString(), new JsonCallback<ResponseGasBean>() {
                    @Override
                    public void onSuccess(Response<ResponseGasBean> response) {
                        if (response.body() != null && response.body().result != null&&view!=null) {
                            String gas = response.body().result;
                            MyLog.i("getGasEstimate: gasLimit==" + gas);
                            String gasp = new BigInteger(gas.substring(2, gas.length()), 16).divide(new BigInteger("1000000000")).toString();
                            view.sendGasPriceSuccess(gasp);
                            MyLog.i("getGasPrice=" + gasp);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseGasBean> response) {
                        super.onError(response);
                    }
                });

            }

        });

    }

}
