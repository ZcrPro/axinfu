package com.zhihuianxin.xyaxf.app.view;

import android.content.Context;
import android.view.View;

/**
 * Created by Vincent on 2017/11/8.
 */

public class TestView extends View{
    public TestView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
