package com.zhihuianxin.xyaxf.basetools.banner.transformers;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by zcrpro on 2016/10/24.
 */
public class AccordionPageTransformer extends BasePageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        ViewHelper.setPivotX(view, view.getWidth());
        ViewHelper.setScaleX(view, 1.0f + position);
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewHelper.setPivotX(view, 0);
        ViewHelper.setScaleX(view, 1.0f - position);
        ViewHelper.setAlpha(view, 1);
    }

}