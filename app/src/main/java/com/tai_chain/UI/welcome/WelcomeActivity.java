package com.tai_chain.UI.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.tai_chain.R;
import com.tai_chain.base.BaseActivity;
import com.tai_chain.UI.createrecovery.CreateRecoveryActivity;
import com.tai_chain.UI.main.MainActivity;
import com.tai_chain.UI.normalvp.NormalPresenter;
import com.tai_chain.UI.normalvp.NormalView;
import com.tai_chain.utils.SharedPrefsUitls;
import com.tai_chain.utils.Util;

public class WelcomeActivity extends BaseActivity<NormalView,NormalPresenter> {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public NormalPresenter initPresenter() {
        return new NormalPresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        new TimeCount(1000, 100).start();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            String wid= SharedPrefsUitls.getInstance().getCurrentWallet();
            if (!Util.isNullOrEmpty(wid)){
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

            }else {
                startActivity(new Intent(WelcomeActivity.this, CreateRecoveryActivity.class));
            }
            finish();

        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示

        }
    }
}
