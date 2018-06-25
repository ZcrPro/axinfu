package com.zhihuianxin.xyaxf.app.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

/**
 * Created by John on 2014/8/1.
 */
public class LoadingDialog extends Dialog {
    public static IOnKeyDownBackDismiss iOnKeyDownBackDismiss;

    private boolean isNotCanCanceld;

    public interface IOnKeyDownBackDismiss {
        void onKeyDownDisMiss();
    }

    public static void setOnKeyDownBackDismissInterface(IOnKeyDownBackDismiss onKeyDownBackDismissInterface) {
        iOnKeyDownBackDismiss = onKeyDownBackDismissInterface;
    }

    private TextView mText;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog);
        setContentView(R.layout.loading_dialog);
        mText = (TextView) findViewById(R.id.text);
        setCanceledOnTouchOutside(false);
        GifView gifView = (GifView) findViewById(R.id.gif_view);
        gifView.setMovieResource(R.raw.gif_loading);
    }


    public LoadingDialog(Context context, boolean isNotCanCanceld) {
        super(context, R.style.loading_dialog);
        setContentView(R.layout.loading_dialog);
        mText = (TextView) findViewById(R.id.text);
        setCanceledOnTouchOutside(false);
        GifView gifView = (GifView) findViewById(R.id.gif_view);
        gifView.setMovieResource(R.raw.gif_loading);
        this.isNotCanCanceld = isNotCanCanceld;
    }

    public void setMessage(String message) {
        if (mText != null) {
            mText.setText(message);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isNotCanCanceld) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                return true;
            }
        }else {
            if (keyCode == KeyEvent.KEYCODE_BACK && iOnKeyDownBackDismiss != null) {
                iOnKeyDownBackDismiss.onKeyDownDisMiss();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
