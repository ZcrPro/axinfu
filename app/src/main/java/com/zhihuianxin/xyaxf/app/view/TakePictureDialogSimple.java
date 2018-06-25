package com.zhihuianxin.xyaxf.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zhihuianxin.xyaxf.R;

/**
 * Created by John on 2015/3/6.
 */
public class TakePictureDialogSimple extends Dialog {
    public interface OnSelectedListener {
        void onFromCameraSelected();
        void onFromGallerySelected();
    }

    View mBtnFromCamera;
    View mBtnFromGallery;
    View mBtnCancel;

    private OnSelectedListener mOnSelectedListener;

    public TakePictureDialogSimple(Context context) {
        super(context, R.style.BottomDialogSimple);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.take_picture_simple);

        mBtnFromCamera = findViewById(R.id.btn_from_camera);
        mBtnFromGallery = findViewById(R.id.btn_from_gallery);
        mBtnCancel = findViewById(R.id.btn_cancel);

        mBtnFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnFromCameraClick(v);
            }
        });

        mBtnFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnFromGalleryClick(v);
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnCancelClick(v);
            }
        });
    }

    private void onBtnCancelClick(View v) {
        cancel();
    }

    private void onBtnFromGalleryClick(View v) {
        if(mOnSelectedListener != null){
            mOnSelectedListener.onFromGallerySelected();
        }

        dismiss();
    }

    private void onBtnFromCameraClick(View v) {
        if(mOnSelectedListener != null){
            mOnSelectedListener.onFromCameraSelected();
        }

        dismiss();
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.mOnSelectedListener = listener;
    }
}
