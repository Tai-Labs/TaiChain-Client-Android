package com.tai_chain.UI.walletsetting.updatePassword;

import android.content.Context;
import android.text.TextUtils;

import com.tai_chain.R;
import com.tai_chain.base.BasePresent;
import com.tai_chain.utils.Md5Utils;
import com.tai_chain.utils.ToastUtils;
import com.tai_chain.utils.Util;

public class UpdatePwdPresenter extends BasePresent<UpdatePwdView> {

    public boolean verifyPassword(Context ctx,String oldPwd, String newPwd, String newPwdAgain, String walletPwd) {
        if (Util.isNullOrEmpty(oldPwd)) {
            ToastUtils.showLongToast(ctx,R.string.modify_password_alert3);
        } else if (Util.isNullOrEmpty(newPwd)||newPwd.length()<6) {
            ToastUtils.showLongToast(ctx,R.string.modify_password_alert1);
        } else if (Util.isNullOrEmpty(newPwdAgain)) {
            ToastUtils.showLongToast(ctx,R.string.modify_password_alert2);
        } else if (!TextUtils.equals(Md5Utils.md5(oldPwd), walletPwd)) {
            ToastUtils.showLongToast(ctx,R.string.modify_password_alert4);
            return false;
        } else if (!TextUtils.equals(newPwd, newPwdAgain)) {
            ToastUtils.showLongToast(ctx,R.string.modify_password_alert5);
            return false;
        }
        return true;
    }
}
