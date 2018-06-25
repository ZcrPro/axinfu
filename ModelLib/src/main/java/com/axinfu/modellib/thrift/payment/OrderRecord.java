package com.axinfu.modellib.thrift.payment;

import com.axinfu.modellib.thrift.base.PayChannel;

/**
 * Created by zcrprozcrpro on 2017/5/8.
 */

public class OrderRecord {
    public PayChannel pay_channel;  // required
    public String payfor;  // required
    public String order_no;  // optional
    public String pay_amt;  // optional
    public String trade_summary;  // required
    public String order_time;  // required
}
