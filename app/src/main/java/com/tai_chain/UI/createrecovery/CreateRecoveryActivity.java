package com.tai_chain.UI.createrecovery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tai_chain.R;
import com.tai_chain.base.BaseActivity;
import com.tai_chain.base.Constants;
import com.tai_chain.UI.main.MainActivity;
import com.tai_chain.UI.normalvp.NormalPresenter;
import com.tai_chain.UI.normalvp.NormalView;
import com.tai_chain.UI.walletmanage.createwallet.CreateWallet;
import com.tai_chain.UI.walletmanage.importwallet.ImportWallet;
import com.tai_chain.utils.SharedPrefsUitls;
import com.tai_chain.utils.Util;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateRecoveryActivity extends BaseActivity<NormalView, NormalPresenter> implements NormalView {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_recovery;
    }

    @Override
    public NormalPresenter initPresenter() {
        return new NormalPresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        String wid=SharedPrefsUitls.getInstance().getCurrentWallet();
        if (!Util.isNullOrEmpty(wid)){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
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


    @OnClick({R.id.button_new_wallet, R.id.button_recover_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_new_wallet:
                startActivity(new Intent(this, CreateWallet.class).putExtra("from",1));
                break;
            case R.id.button_recover_wallet:
                Intent intent=new Intent(this, ImportWallet.class);
                intent.putExtra(Constants.WALLET_TYPE,"TIT");
                intent.putExtra("from",1);
                startActivity(intent);
                break;
        }
    }
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
