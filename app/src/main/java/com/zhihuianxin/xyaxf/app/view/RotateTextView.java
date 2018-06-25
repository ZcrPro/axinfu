package com.zhihuianxin.xyaxf.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Vincent on 2018/1/13.
 */

@SuppressLint("AppCompatCustomView")
public class RotateTextView extends TextView{
    public RotateTextView(Context context) {
        super(context);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //倾斜度90,上下左右居中
        canvas.rotate(90, getMeasuredWidth()/2, getMeasuredHeight()/2);
        super.onDraw(canvas);
    }
}
