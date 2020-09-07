package com.tai_chain.UI.walletoperation.receive;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tai_chain.R;
import com.tai_chain.UI.tools.threads.TITExecutor;
import com.tai_chain.base.BaseActivity;
import com.tai_chain.bean.TokenInfo;
import com.tai_chain.utils.AddressUtils;
import com.tai_chain.utils.ClipboardManager;
import com.tai_chain.utils.MyLog;
import com.tai_chain.utils.QRUtils;
import com.tai_chain.utils.Util;
import com.tai_chain.view.MEdit;
import com.tai_chain.view.MText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiveQrCodeActivity extends BaseActivity<ReceiveQrCodeView, ReceiveQrCodePresenter> implements ReceiveQrCodeView {


//    @BindView(R.id.receive_wallet_name)
//    MText receiveWalletName;
    @BindView(R.id.tv_wallet_address)
    MText tvWalletAddress;
    @BindView(R.id.amount_edit)
    MEdit amountEdit;
    @BindView(R.id.iso_text)
    MText isoText;
    @BindView(R.id.amount_layout)
    RelativeLayout amountLayout;
    @BindView(R.id.iv_gathering_qrcode)
    ImageView ivGatheringQrcode;
//    @BindView(R.id.amount_layout_v)
//    View amountLayoutV;
    private TokenInfo token;
    String receiveAddress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_receive_qr_code;
    }

    @Override
    public ReceiveQrCodePresenter initPresenter() {
        return new ReceiveQrCodePresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setCenterTitle(getResources().getString(R.string.Button_receive));
        token = (TokenInfo) getIntent().getParcelableExtra("tokenInfo");
        receiveAddress = getIntent().getStringExtra("rAddress");
        MyLog.i("initViews==token=" + token.symbol);
//        if (token.symbol.equals("TIT") || token.symbol.equals("BTC") || token.symbol.equals("ETH")) {
//            amountLayout.setVisibility(View.VISIBLE);
//            amountLayoutV.setVisibility(View.VISIBLE);
//        } else {
//            amountLayout.setVisibility(View.GONE);
//            amountLayoutV.setVisibility(View.GONE);
//        }
        if (Util.isNullOrEmpty(receiveAddress)) receiveAddress = token.address;
        tvWalletAddress.setText(AddressUtils.addr0X2TIT(receiveAddress));
        isoText.setText(token.symbol);
//        ImmersionBar.with(this)
//                .transparentStatusBar()
//                .navigationBarColor(token.mStartColor)
//                .init();
//        GradientDrawable gd = new GradientDrawable(
//                GradientDrawable.Orientation.LEFT_RIGHT,
//                new int[]{Color.parseColor(token.mStartColor), Color.parseColor(token.mEndColor)});
//        gd.setCornerRadius(0f);
//        receiveLlBar.setBackground(gd);
        updateQr("0");
    }


    @Override
    protected void initData() {
        amountEdit.addTextChangedListener(tw);
    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable edt) {

            String temp = edt.toString();
            int posDot = temp.indexOf(".");
            int frist = temp.indexOf("0");
            if (frist == 0 && temp.length() > 1 && posDot != 1) {
                edt.delete(1, 2);
            }
            if (posDot == 0) {
                edt.delete(0, 1);
            }
            if (Util.isNullOrEmpty(temp)) {
                updateQr("0");
            } else {
                updateQr(temp);
            }

        }
    };

    @Override
    public void initEvent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({ R.id.tv_wallet_address, R.id.btn_copy_address})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.receive_back:
//                onBackPressed();
//                break;
//            case R.id.receive_share:
//                Uri cryptoUri = CryptoUriParser.createCryptoUrl(this, token.name, token.symbol, receiveAddress, BigDecimal.ZERO, null, null, null);
//                QRUtils.share(token.name, this, cryptoUri.toString());
//                break;
            case R.id.tv_wallet_address:
                copyText();
                break;
            case R.id.btn_copy_address:
                copyText();
                break;
        }
    }

    private void copyText() {
        ClipboardManager.putClipboard(this, tvWalletAddress.getText().toString());
    }

    private void updateQr(String amount) {
        TITExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
//                Uri uri = CryptoUriParser.createCryptoUrl(getApplication(), token.symbol, token.name, receiveAddress, new BigDecimal(amount), token.symbol, null, null);
                Uri uri =  Uri.parse(AddressUtils.addr0X2TIT(receiveAddress));
                boolean generated = QRUtils.generateQR(getApplication(), uri.toString(), ivGatheringQrcode);
                if (!generated)
                    throw new RuntimeException("failed to generate qr image for address");
            }
        });

    }
}
