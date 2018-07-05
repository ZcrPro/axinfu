package com.paycenter;


import android.content.Context;
import android.widget.Toast;

/**
 * 每一次支付都是调用下单接口 该类负责统一处理下单之后的反馈
 */

public class CasherHelper  {

    public CasherHelper(Context context) {
        Toast.makeText(context, PayChannel.UnionPay,Toast.LENGTH_LONG).show();
    }
}
