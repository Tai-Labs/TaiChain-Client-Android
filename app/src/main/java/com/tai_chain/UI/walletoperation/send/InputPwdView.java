package com.tai_chain.UI.walletoperation.send;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tai_chain.R;
import com.tai_chain.view.MButton;


public class InputPwdView extends FrameLayout {
    private TextView conten;
    private EditText password;
    private MButton send_button;
    private onConfirmSend onConfirmSender;

    public interface onConfirmSend {
        void sendTransaction(String pwd);
    }

    public InputPwdView(@NonNull Context context,String con, onConfirmSend l) {
        super(context);
        onConfirmSender = l;

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_input_password, this, true);
        password = findViewById(R.id.password);
        conten=findViewById(R.id.tv_send_conten);
        conten.setText(con);

        findViewById(R.id.send_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmSender.sendTransaction(getPassword());
            }
        });
    }



    public String getPassword() {
        return password.getText().toString();
    }

    public void showKeyBoard() {
        password.requestFocus();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showKeyBoard();
    }
}
