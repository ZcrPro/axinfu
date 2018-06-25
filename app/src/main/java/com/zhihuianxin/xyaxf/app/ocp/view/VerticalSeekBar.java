package com.zhihuianxin.xyaxf.app.ocp.view;

/**
 * Created by zcrpro on 2018/1/9.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jaywei.PureVerticalSeekBar;

/**
 * Class to create a vertical slider
 */
public class VerticalSeekBar extends PureVerticalSeekBar {

    private OnChangeListener onChangeListener;
    private float x;
    private float y;
    private float progress;
    private float sWidth;
    private float sHeight;


    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        this.y = event.getY();
        this.progress = (this.sHeight - this.y) / this.sHeight * 100.0F;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(this.onChangeListener != null) {
                    this.onChangeListener.OnChangeingListener(this, progress);
                }
            case MotionEvent.ACTION_MOVE:
                if(this.onChangeListener != null) {
                    this.onChangeListener.onChangedListener(this, progress);
                }
            case MotionEvent.ACTION_UP:

                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public interface OnChangeListener {
        void OnChangeingListener(View var1, float var2);
        void onChangedListener(View var1, float var2);
    }

    public void setOnSlideChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }
}