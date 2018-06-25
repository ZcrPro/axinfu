package com.zhihuianxin.xyaxf.app.fee.detail;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

/**
 * Created by zcrpro on 2017/1/7.
 */

public class InputFilterMinMax implements InputFilter {

    private float min, max;
    private Context context;

    public InputFilterMinMax(Context context, float min, float max) {
        this.min = min;
        this.max = max;
        this.context = context;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            float input = Float.parseFloat(dest.toString() + source.toString());
            if (isInRange(min, max, input)) {
                return null;
            } else {
//                Toast.makeText(context, "输入的金额不能超出应缴费金额", Toast.LENGTH_SHORT).show();
                showToast((Activity)context,"输入的金额不能超出应缴费金额",500);
            }
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    public static void showToast(final Activity activity, final String word, final long time){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_LONG);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, time);
            }
        });
    }
}
