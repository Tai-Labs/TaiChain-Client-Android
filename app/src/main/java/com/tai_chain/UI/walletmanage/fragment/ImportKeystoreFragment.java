package com.tai_chain.UI.walletmanage.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tai_chain.R;
import com.tai_chain.base.BaseFragment;
import com.tai_chain.base.Constants;
import com.tai_chain.bean.WalletBean;
import com.tai_chain.UI.main.MainActivity;
import com.tai_chain.UI.walletmanage.WalletsMaster;
import com.tai_chain.UI.walletmanage.createwallet.CreateWalletInteract;
import com.tai_chain.UI.walletmanage.createwallet.CreateWalletPresenter;
import com.tai_chain.UI.walletmanage.createwallet.CreateWalletView;
import com.tai_chain.utils.ToastUtils;
import com.tai_chain.utils.Util;
import com.tai_chain.view.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImportKeystoreFragment extends BaseFragment<CreateWalletView, CreateWalletPresenter> implements CreateWalletView {


    Unbinder unbinder;
    @BindView(R.id.et_keystore)
    EditText etKeystore;
    @BindView(R.id.ks_wallet_name)
    EditText ksWalletName;
    @BindView(R.id.ks_wallet_pwd)
    EditText ksWalletPwd;
    @BindView(R.id.ksbtn_load_wallet)
    TextView ksbtnLoadWallet;
    private String walletType;
    private CreateWalletInteract createWalletInteract;
    private LoadingDialog loadingDialog;

    @Override
    public CreateWalletPresenter initPresenter() {
        return new CreateWalletPresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        walletType = getActivity().getIntent().getStringExtra(Constants.WALLET_TYPE);
        createWalletInteract = new CreateWalletInteract();

    }

    @Override
    protected void initData() {

    }

    @Override
    public void initEvent() {
        etKeystore.addTextChangedListener(textWatcher);
        ksWalletName.addTextChangedListener(textWatcher);
        ksWalletPwd.addTextChangedListener(textWatcher);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_load_wallet_by_official_wallet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @SuppressLint("CheckResult")
    @OnClick({R.id.ksbtn_load_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ksbtn_load_wallet:
                String keyStore = etKeystore.getText().toString().trim();
                String walletName = ksWalletName.getText().toString().trim();
                String walletPwd = ksWalletPwd.getText().toString().trim();
                boolean verifyWalletInfo = presenter.verifyKeystoreToAddress(getActivity(), keyStore, walletPwd, walletName, walletType);
                if (!verifyWalletInfo) {
                    loadingDialog = new LoadingDialog(getActivity());
                    loadingDialog.show();
                    createWalletInteract.loadWalletByKeystore(keyStore, walletPwd, walletName, walletType).subscribe(this::jumpToWalletBackUp, this::showError);
                }
                break;
        }
    }

    public void showError(Throwable errorInfo) {
        loadingDialog.dismiss();
        ToastUtils.showLongToast(getActivity(), errorInfo.toString());
    }

    public void jumpToWalletBackUp(WalletBean wallet) {
        loadingDialog.dismiss();
        ToastUtils.showLongToast(getActivity(), getResources().getString(R.string.load_wallet_ok));
        String wid = wallet.getId();
        WalletsMaster.getInstance().getWalletByIso(getActivity(), wid.substring(0, wid.indexOf("-"))).setmAddress(wallet.getAddress());
        if (getActivity().getIntent().getIntExtra("from", 0) != 0) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
//        dismissDialog();
        getActivity().finish();
    }

    @Override
    public void getWalletSuccess(String address) {

    }

    @Override
    public void getWalletFail(String msg) {

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!Util.isNullOrEmpty(etKeystore.getText().toString())
                    && !Util.isNullOrEmpty(ksWalletName.getText().toString())
                    && !Util.isNullOrEmpty(ksWalletPwd.getText().toString())) {
                ksbtnLoadWallet.setEnabled(true);
            } else {
                ksbtnLoadWallet.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
