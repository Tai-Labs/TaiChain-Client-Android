package com.tai_chain.UI.walletsetting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tai_chain.R;
import com.tai_chain.base.BaseActivity;
import com.tai_chain.bean.WalletBean;
import com.tai_chain.blockchain.walletutils.WalletUtils;
import com.tai_chain.UI.createrecovery.CreateRecoveryActivity;
import com.tai_chain.UI.normalvp.NormalPresenter;
import com.tai_chain.UI.normalvp.NormalView;
import com.tai_chain.UI.walletsetting.BackupMnemonic.MnemonicBackupActivity;
import com.tai_chain.UI.walletsetting.exportKeystore.ExportKeystoreActivity;
import com.tai_chain.UI.walletsetting.updatePassword.UpdatePasswordActivity;
import com.tai_chain.sqlite.BalanceDataSource;
import com.tai_chain.sqlite.WalletDataStore;
import com.tai_chain.utils.AddressUtils;
import com.tai_chain.utils.CurrencyUtils;
import com.tai_chain.utils.TITKeyStore;
import com.tai_chain.utils.Md5Utils;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.SharedPrefsUitls;
import com.tai_chain.utils.ToastUtils;
import com.tai_chain.utils.Util;
import com.tai_chain.view.DeleteWalletDialog;
import com.tai_chain.view.InputPwdDialog;
import com.tai_chain.view.MEdit;
import com.tai_chain.view.MText;
import com.tai_chain.view.PrivateKeyDerivetDialog;

import java.math.BigDecimal;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletSetting extends BaseActivity<NormalView, NormalPresenter> {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    //    @BindView(R.id.set_wallet_icon)
//    CircleImageView setWalletIcon;
    @BindView(R.id.set_wallet_name)
    MText setWalletName;
    @BindView(R.id.set_wallet_address)
    MText setWalletAddress;
    @BindView(R.id.set_mnemonic)
    RelativeLayout setMemonic;

    WalletBean wallet;
    @BindView(R.id.set_keystore)
    RelativeLayout setKeystore;
    @BindView(R.id.set_nick)
    TextView setNick;
    @BindView(R.id.wallet_nick_tx)
    MText walletNickTx;
    @BindView(R.id.wallet_nick_et)
    MEdit walletNickEt;
    private InputPwdDialog inputPwdDialog;
    private ModifyWalletInteract modifyWalletInteract;
    private PrivateKeyDerivetDialog privateKeyDerivetDialog;
    private boolean isDone = false;
    String wid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_setting;
    }

    @Override
    public NormalPresenter initPresenter() {
        return new NormalPresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        wallet = getIntent().getParcelableExtra("wallet");
        if (wallet.getId().contains("BTC")) {
            setKeystore.setVisibility(View.GONE);
        }
        if (Util.isNullOrEmpty(wallet.getMnemonic())) {
            setMemonic.setVisibility(View.GONE);
        }
        tvTitle.setText(wallet.getName());
        walletNickTx.setText(wallet.getName());
//        int iconResourceId = getResources().getIdentifier(wallet.getId().substring(0, 3).toLowerCase(), "mipmap", getPackageName());
//        setWalletIcon.setImageResource(iconResourceId);
        Map<String, String> balances = BalanceDataSource.getInstance().getWalletTokensBalance(wallet.getId());
        if (balances.containsKey("TIT")) {
            String cryptoBalance = CurrencyUtils.getFormattedAmount(this, "TIT", new BigDecimal(Util.isNullOrEmpty(balances.get("TIT")) ? "0" : balances.get("TIT")));
            setWalletName.setText(cryptoBalance);
        } else {
            setWalletName.setText("0 TIT");
        }

        setWalletAddress.setText(AddressUtils.addr0X2TIT(wallet.getAddress()));
    }

    @Override
    protected void initData() {
        modifyWalletInteract = new ModifyWalletInteract();
        inputPwdDialog = new InputPwdDialog(this, R.style.MyDialog);
    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Util.isNullOrEmpty(wid)) {
            wallet = WalletDataStore.getInstance().queryWallet(wid);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.set_mnemonic, R.id.set_keystore, R.id.set_privatekey, R.id.iv_back, R.id.button_del_wallet, R.id.set_password, R.id.set_nick})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_nick:
                String nick = walletNickEt.getText().toString().trim();
                if (isDone) {

                    if (Util.isNullOrEmpty(nick) || nick.equals(wallet.getName()) || WalletUtils.modifyWalletNick(wallet.getId(), nick)) {
                        isDone = false;
                        walletNickTx.setText(Util.isNullOrEmpty(nick) ? wallet.getName() : nick);
                        walletNickTx.setVisibility(View.VISIBLE);
                        walletNickEt.setText("");
                        walletNickEt.setVisibility(View.GONE);
                        tvTitle.setText(Util.isNullOrEmpty(nick) ? wallet.getName() : nick);
                        setNick.setText(R.string.Wallet_set_updata);
                        setNick.setTextColor(getResources().getColor(R.color.zt_hui));
                    } else {
                        ToastUtils.showLongToast(activity, R.string.Wallet_set_nick_fail);
                    }

                } else {
                    isDone = true;
                    walletNickTx.setVisibility(View.GONE);
                    walletNickEt.setVisibility(View.VISIBLE);
                    setNick.setText(R.string.Wallet_set_done);
                    setNick.setTextColor(getResources().getColor(R.color.zt_lu));
                }

                break;
            case R.id.set_mnemonic:
                inputPwdDialog.show();
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.equals(wallet.getPassword(), Md5Utils.md5(pwd))) {
                            String phrase = "";
                            try {
                                phrase = TITKeyStore.decodetData(wallet.getMnemonic());
                                MyLog.i("DeterministicSeed=====" + phrase);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(WalletSetting.this, MnemonicBackupActivity.class);
                            intent.putExtra("walletId", wallet.getId());
                            intent.putExtra("walletMnemonic", phrase);
                            startActivity(intent);
                        } else {
                            ToastUtils.showLongToast(WalletSetting.this, R.string.wallet_detail_wrong_pwd);
                        }
                        inputPwdDialog.dismiss();
                    }
                });
                break;
            case R.id.set_keystore:
                inputPwdDialog.show();
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        inputPwdDialog.dismiss();
                        if (TextUtils.equals(wallet.getPassword(), Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deriving_wallet_tip));
                            modifyWalletInteract.deriveWalletKeystore(wallet.getId(), pwd).subscribe(WalletSetting.this::showDeriveKeystore);
                        } else {
                            ToastUtils.showLongToast(WalletSetting.this, R.string.wallet_detail_wrong_pwd);
                        }
                    }
                });
                break;
            case R.id.set_privatekey:
                inputPwdDialog.show();
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        inputPwdDialog.dismiss();
                        if (TextUtils.equals(wallet.getPassword(), Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deriving_wallet_tip));
                            modifyWalletInteract.deriveWalletPrivateKey(wallet.getId(), pwd).subscribe(WalletSetting.this::showDerivePrivateKeyDialog);

                        } else {
                            ToastUtils.showLongToast(WalletSetting.this, R.string.wallet_detail_wrong_pwd);
                        }
                    }
                });
                break;
            case R.id.set_password:
                wid = wallet.getId();
                startActivity(new Intent(WalletSetting.this, UpdatePasswordActivity.class).putExtra("wallet", wallet));
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.button_del_wallet:
                DeleteWalletDialog deleteWalletDialog = new DeleteWalletDialog(WalletSetting.this, R.style.MyDialog);
                deleteWalletDialog.show();
                deleteWalletDialog.setOnDeleteClickListener(new DeleteWalletDialog.OnDeleteClickListener() {
                    @Override
                    public void setOnDeleteClick() {
                        inputPwdDialog.show();
                        inputPwdDialog.setDeleteAlertVisibility(false);
                        inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                            @Override
                            public void onCancel() {
                                inputPwdDialog.dismiss();
                            }

                            @Override
                            public void onConfirm(String pwd) {

                                if (TextUtils.equals(wallet.getPassword(), Md5Utils.md5(pwd))) {

                                    if (WalletUtils.deleteWallet(wallet.getId())) {
                                        if (Util.isNullOrEmpty(SharedPrefsUitls.getInstance().getCurrentWallet())) {
                                            startActivity(new Intent(WalletSetting.this, CreateRecoveryActivity.class));
                                        }
                                        finish();
                                    }

                                } else {
                                    ToastUtils.showLongToast(WalletSetting.this, R.string.wallet_detail_wrong_pwd);
                                }
                                inputPwdDialog.dismiss();
                            }

                        });

                        deleteWalletDialog.dismiss();
                    }
                });

                break;
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    private void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void showDerivePrivateKeyDialog(String privateKey) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                dismissDialog();
            }
        }, 1000);
        privateKeyDerivetDialog = new PrivateKeyDerivetDialog(this, R.style.MyDialog);
        privateKeyDerivetDialog.show();
        privateKeyDerivetDialog.setPrivateKey(privateKey);
    }

    public void showDeriveKeystore(String keystore) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                dismissDialog();
            }
        }, 1000);
        Intent intent = new Intent(this, ExportKeystoreActivity.class);
        intent.putExtra("walletKeystore", keystore);
        startActivity(intent);
    }
}
