package com.zhihuianxin.xyaxf.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2017/11/15.
 */

public class UnionpayRemarkDialog extends Dialog{

    private String mark;

    public interface OnNextListener {
        void onNext(String data);
    }

    public void setOnNextListener(OnNextListener listener){
        onNextListener = listener;
    }

    private OnNextListener onNextListener;
    private Button nextBtn;
    private EditText remarkEdit;
    private TextView mCancelText;

    public UnionpayRemarkDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
    }

    public UnionpayRemarkDialog(Context context,String mark) {
        super(context, R.style.UnionPayErrorDialog);
        this.mark = mark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unionpay_inputmark);
        mCancelText = (TextView) findViewById(R.id.cancelremark);
        nextBtn = (Button) findViewById(R.id.next);
        remarkEdit = (EditText) findViewById(R.id.inputEdit);

//        remarkEdit.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                int index = remarkEdit.getSelectionStart() - 1;
//                if (index > 0) {
//                    if (isEmojiCharacter(s.charAt(index))) {
//                        Editable edit = remarkEdit.getText();
//                        edit.delete(s.length() - 2, s.length());
//                        return;
//                    }
//                }
//
//            }
//        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClick(remarkEdit.getText().toString().trim());
            }
        });
        mCancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (mark!=null){
            remarkEdit.setText(mark);
        }
    }

    private void onBtnClick(String str) {
        if(onNextListener != null){
            onNextListener.onNext(str);
        }
        dismiss();
    }

    public void setRemark(String str){
        remarkEdit.setText(str);
    }


    public static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
}
