package com.axinfu.basetools.banner;

import android.content.Context;
import android.widget.Scroller;

/**
 * Created by zcrpro on 2016/10/24.
 */
public class XBannerScroller extends Scroller {

    private int mDuration = 800;//值越大，滑动越慢

    public XBannerScroller(Context context, int duration) {
        super(context);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
