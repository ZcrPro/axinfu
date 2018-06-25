package com.zhihuianxin.xyaxf.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by Vincent on 2017/12/4.
 */

public class BarCodeTextView extends android.support.v7.widget.AppCompatTextView{
    public BarCodeTextView(Context context) {
        super(context);
    }

    public BarCodeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(90);
        super.onDraw(canvas);
    }
}
