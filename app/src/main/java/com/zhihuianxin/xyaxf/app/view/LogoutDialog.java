package com.zhihuianxin.xyaxf.app.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2016/11/14.
 */

public class LogoutDialog extends Dialog {
    private TextView mText;
    public LogoutDialog(Context context) {
        super(context, R.style.logoutDialog);
        setContentView(R.layout.logout_dialog);
        mText = (TextView) findViewById(R.id.text);
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(String message){
        if(mText != null){
            mText.setText(message);
        }
    }
}
