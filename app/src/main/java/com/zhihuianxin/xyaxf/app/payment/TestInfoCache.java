package com.zhihuianxin.xyaxf.app.payment;

import android.content.Context;

import com.xyaxf.axpay.persistence.AbsSharedPreferencesData;

/**
 * Created by Vincent on 2016/12/27.
 */

public class TestInfoCache extends AbsSharedPreferencesData {
    public static TestInfoCache sInstance;
    public static void initialize(Context context) {
        if (sInstance == null) {
            sInstance = new TestInfoCache(context.getApplicationContext());
        }
    }

    public TestInfoCache(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return null;
    }
}
