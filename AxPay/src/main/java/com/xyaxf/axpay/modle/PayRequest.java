package com.xyaxf.axpay.modle;

import com.axinfu.modellib.thrift.base.PayMethod;

import java.io.Serializable;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class PayRequest<T> implements Serializable {
	public PayMethod pay_method;  // required
	public PayFor pay_for ;  // required
	public String amount ;  // required
	public T request_data ;  // required
	public T channel_request_data ;  // required
}