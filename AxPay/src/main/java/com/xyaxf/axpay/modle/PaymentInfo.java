package com.xyaxf.axpay.modle;


import java.io.Serializable;

import modellib.thrift.base.PayMethod;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class PaymentInfo implements Serializable{
	public PayMethod pay_method;  // required
	public String pay_info ;  // required
	public String channel_orderno ;  // optional
	public String payment_amt ;  // optional

	//收银台界面需要
	public String fee_name ;
	public String fee_time ;

}