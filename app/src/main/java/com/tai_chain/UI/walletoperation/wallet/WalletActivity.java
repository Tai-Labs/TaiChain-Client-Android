package com.tai_chain.UI.walletoperation.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.TransactionsAdapter;
import com.tai_chain.base.BaseActivity;
import com.tai_chain.base.Constants;
import com.tai_chain.bean.BalanceEntity;
import com.tai_chain.bean.CryptoRequest;
import com.tai_chain.bean.Token;
import com.tai_chain.bean.TokenInfo;
import com.tai_chain.bean.TransactionRecords;
import com.tai_chain.bean.WalletBean;
import com.tai_chain.blockchain.BaseWalletManager;
import com.tai_chain.UI.tools.threads.TITExecutor;
import com.tai_chain.UI.walletmanage.WalletsMaster;
import com.tai_chain.UI.walletoperation.receive.ReceiveQrCodeActivity;
import com.tai_chain.UI.walletoperation.send.SendActivity;
import com.tai_chain.sqlite.BalanceDataSource;
import com.tai_chain.utils.CryptoUriParser;
import com.tai_chain.utils.CurrencyUtils;
import com.tai_chain.utils.TITAnimator;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.SharedPrefsUitls;
import com.tai_chain.utils.Util;
import com.tai_chain.view.LoadingDialog;
import com.tai_chain.view.MText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletActivity extends BaseActivity<WalletView, WalletPresenter> implements WalletView {


    @BindView(R.id.balance_primary)
    MText mBalancePrimary;
    @BindView(R.id.balance_secondary)
    MText mBalanceSecondary;
    @BindView(R.id.tx_list)
    RecyclerView txList;
    @BindView(R.id.recycler_layout)
    LinearLayout recyclerLayout;
    @BindView(R.id.send_button)
    MText mSendButton;
    @BindView(R.id.receive_button)
    MText mReceiveButton;
    @BindView(R.id.bottom_toolbar_layout1)
    LinearLayout bottomToolbarLayout1;
    @BindView(R.id.log_all)
    MText logAll;
    @BindView(R.id.log_receive)
    MText logReceive;
    @BindView(R.id.log_send)
    MText logSend;


    private static final String SYNCED_THROUGH_DATE_FORMAT = "MM/dd/yy HH:mm";
    private static final float SYNC_PROGRESS_LAYOUT_ANIMATION_ALPHA = 0.0f;
    public static final String EXTRA_URL = "com.etzwallet.EXTRA_URL";

    private static final float PRIMARY_TEXT_SIZE = 30;
    private static final float SECONDARY_TEXT_SIZE = 16;

    private static final boolean RUN_LOGGER = false;

    private String mCurrentWalletIso;
    private String mCurrentWalletId;
    private Token token = null;
    private TokenInfo wallet = null;

    private BaseWalletManager wm;
    private WalletBean walletBean;
    TransactionsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private List<TransactionRecords> allList = new ArrayList<>();
    private List<TransactionRecords> sentList = new ArrayList<>();
    private List<TransactionRecords> receiveList = new ArrayList<>();
//    private SendPopupWindow sendPW;
    private LoadingDialog loadingDialog;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    public WalletPresenter initPresenter() {
        return new WalletPresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        token = getIntent().getParcelableExtra("item");
        handler = new Handler();
        wallet = token.tokenInfo;
        setCenterTitle(wallet.symbol);
        wm = WalletsMaster.getInstance().getWalletByIso(this, wallet.symbol);
//        ImmersionBar.with(this)
//                .transparentStatusBar()
//                .navigationBarColor(wallet.mEndColor)
//                .init();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        txList.setLayoutManager(linearLayoutManager);
        adapter = new TransactionsAdapter(R.layout.logs_item, new ArrayList<>(), wallet);
        txList.setAdapter(adapter);
        isClickLL(true, false, false);
        loadingDialog = new LoadingDialog(WalletActivity.this);
        loadingDialog.show();
        walletBean = presenter.getCurrentWallet();
//        sendPW = new SendPopupWindow(WalletActivity.this, walletBean, wallet);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void initEvent() {
//        sendPW.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                recoveryAlpha();
//            }
//        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter badapter, View view, int position) {
                TransactionRecords transactionItem = adapter.getItem(position);
                if (!Util.isNullOrEmpty(transactionItem.blockNumber))
                    TITAnimator.showTransactionDetails(WalletActivity.this, transactionItem, wallet.symbol);
            }
        });
    }

    private void recoveryAlpha() {
        WindowManager.LayoutParams lp = this.getWindow()
                .getAttributes();
        lp.alpha = 1f;
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setAttributes(lp);
    }

    public void resetFlipper() {
//        mBarFlipper.setDisplayedChild(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.send_button, R.id.receive_button, R.id.balance_primary, R.id.balance_secondary, R.id.log_all, R.id.log_receive, R.id.log_send})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.send_button:
                intent = new Intent(WalletActivity.this, SendActivity.class);
                intent.putExtra("iso", wallet.symbol);
                startActivity(intent);
                break;
            case R.id.receive_button:
                intent = new Intent(this, ReceiveQrCodeActivity.class);
                intent.putExtra("tokenInfo", wallet);
                intent.putExtra("rAddress", walletBean.getAddress());
                startActivity(intent);
                break;
            case R.id.log_all:
                isClickLL(true, false, false);
                adapter.setTokens(allList);
                break;
            case R.id.log_receive:
                isClickLL(false, true, false);
                adapter.setTokens(receiveList);
                break;
            case R.id.log_send:
                isClickLL(false, false, true);
                adapter.setTokens(sentList);
                break;
        }
    }

    public void isClickLL(boolean tab01, boolean tab02, boolean tab03) {
        logAll.setSelected(tab01);
        logReceive.setSelected(tab02);
        logSend.setSelected(tab03);
    }

    private void updateUi() {
        BalanceEntity be = BalanceDataSource.getInstance().getTokenBalance(wallet.symbol, SharedPrefsUitls.getInstance().getCurrentWallet());
        String balance = be == null ? "0" : be.money;
        String fiatBalance = CurrencyUtils.getFormattedAmount(this, SharedPrefsUitls.getInstance().getPreferredFiatIso(), wm.getFiatBalance(wallet.symbol, balance));
        String cryptoBalance = CurrencyUtils.getFormattedAmountnotlabe(this, wallet.symbol, new BigDecimal(balance), Constants.MAX_DECIMAL_PLACES_FOR_UI);

        mBalancePrimary.setText(fiatBalance);
        mBalanceSecondary.setText(cryptoBalance);
    }


    @Override
    public void walletTransactions(List<TransactionRecords> list) {
        adapter.setTokens(list);
        allList.addAll(list);
        ListClassify(list);
        loadingDialog.dismiss();

    }

    @Override
    public void showError(String err) {
        loadingDialog.dismiss();
    }

    private void ListClassify(List<TransactionRecords> list) {
        for (TransactionRecords item : list) {
            if (item.isReceived) {
                receiveList.add(item);
            } else {
                sentList.add(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        switch (requestCode) {
            case Constants.SCANNER_REQUEST:
                String result = data.getStringExtra("result");
                MyLog.i("**************" + result);
                CryptoRequest cryptoRequest = CryptoUriParser.parseRequest(WalletActivity.this, result);
//                sendPW.scanResultSetText(cryptoRequest.address);
                break;
            case Constants.CONTACTS_REQUEST:
                String address = data.getStringExtra("result");
                MyLog.i("**************" + address);
//                sendPW.scanResultSetText(address);
                break;
        }
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
                                initDatas();
                            }
                        });
                    }
                });
            }
        };
    }

    private void initDatas() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUi();
                if (WalletsMaster.getInstance().isIsoErc20(WalletActivity.this, wallet.symbol)) {
                    presenter.getLogs(activity, walletBean.getId(), walletBean.address, wallet.address.toLowerCase());
                } else {
                    presenter.getTransactions(activity, walletBean.getId(), walletBean.address);
                }
            }
        });

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

    @Override
    protected void onStop() {
        MyLog.i("**********************onStop");
        stopTimerTask();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("**********************onResume");
        startTimer(WalletActivity.this);
    }
}
