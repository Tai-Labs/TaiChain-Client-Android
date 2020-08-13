package com.tai_chain.UI.walletmanage.selectwallettype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tai_chain.R;
import com.tai_chain.UI.normalvp.NormalPresenter;
import com.tai_chain.UI.normalvp.NormalView;
import com.tai_chain.UI.walletmanage.importwallet.ImportWallet;
import com.tai_chain.base.BaseActivity;
import com.tai_chain.base.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectWalletType extends BaseActivity<NormalView, NormalPresenter> implements NormalView {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_wallet_type;
    }

    @Override
    public NormalPresenter initPresenter() {
        return new NormalPresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setCenterTitle("选择钱包类型");

    }

    @Override
    protected void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.import_etz, R.id.import_btc, R.id.import_eth})
    public void onViewClicked(View view) {
        Intent intent=new Intent(SelectWalletType.this, ImportWallet.class);
        switch (view.getId()) {
            case R.id.import_etz:
                intent.putExtra(Constants.WALLET_TYPE,"TIT");
                startActivity(intent);
                break;
            case R.id.import_btc:
                intent.putExtra(Constants.WALLET_TYPE,"BTC");
                startActivity(intent);
                break;
            case R.id.import_eth:
                intent.putExtra(Constants.WALLET_TYPE,"ETH");
                startActivity(intent);
                break;
        }
    }
}
