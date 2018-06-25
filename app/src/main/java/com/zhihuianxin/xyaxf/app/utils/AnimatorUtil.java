package com.zhihuianxin.xyaxf.app.utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by zhengzheng on 16/7/26.
 */
public class AnimatorUtil {
    // 高度渐变的动画；
    public static void animHeightToView(final View v, final int start, final int end, final boolean isToShow,
                                        long duration) {

        ValueAnimator va = ValueAnimator.ofInt(start, end);
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = Integer.parseInt(animation.getAnimatedValue().toString());
                layoutParams.height = h;
                v.setLayoutParams(layoutParams);
                v.requestLayout();
            }
        });

        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isToShow) {
                    v.setVisibility(View.VISIBLE);
                }
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isToShow) {
                    v.setVisibility(View.GONE);
                }
            }
        });
        va.setDuration(duration);
        va.start();
    }

    public static void animHeightToView(final Activity context, final View v, final boolean isToShow, final long duration) {

        if (isToShow) {
            // 显示：通过上下文对象context获取可见度属性为 gone 的 view 的高度；
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            v.measure(size.x, size.y);
            int end = v.getMeasuredHeight();
            animHeightToView(v,v.getLayoutParams().height, end, isToShow, duration);
        } else {
            // 隐藏：从当前高度变化到0，最后设置不可见；
            animHeightToView(v, v.getLayoutParams().height, 0, isToShow, duration);
        }
    }
}
