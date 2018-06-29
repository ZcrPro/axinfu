package com.zhihuianxin.xyaxf.basetools.banner.transformers;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by zcrpro on 2016/10/24.
 */
public class FlipPageTransformer extends BasePageTransformer {
    private static final float ROTATION = 180.0f;

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        ViewHelper.setTranslationX(view, -view.getWidth() * position);
        float rotation = (ROTATION * position);
        ViewHelper.setRotationY(view, rotation);

        if (position > -0.5) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewHelper.setTranslationX(view, -view.getWidth() * position);
        float rotation = (ROTATION * position);
        ViewHelper.setRotationY(view, rotation);

        if (position < 0.5) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

}