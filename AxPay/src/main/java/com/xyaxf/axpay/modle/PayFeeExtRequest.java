package com.xyaxf.axpay.modle;

import java.io.Serializable;

/**
 * Created by zcrpro on 2016/11/30.
 */
public class PayFeeExtRequest implements Serializable{
    public String id;            // ID
    public String title ;
    public String amount;    // 待缴金额
    public String pay_amount;// 缴纳金额
}
