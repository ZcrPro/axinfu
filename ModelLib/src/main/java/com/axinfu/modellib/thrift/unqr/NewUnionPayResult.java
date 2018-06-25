package com.axinfu.modellib.thrift.unqr;

import com.axinfu.modellib.thrift.message.PricingStrategy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vincent on 2017/12/13.
 */

public class NewUnionPayResult implements Serializable {
    public String resp_code;
    public String resp_desc;
    public String qr_code;
    public String status;
    public String amount;
    public String merchant_id;
    public String merchant_name;
    public String trade_summary;
    public String ori_amt;
    public String order_no;
    public String pay_time;

    public String couponType;
    public String coupunSpnsrId;
    public String couponOffstAmt;
    public String couponId;
    public String couponDesc;
    public String couponAddnInfo;

    public String bankid;
    public String bank_name;
    public String card_type_name;
    public String bankcard_no;
    public String iss_ins_code;
    public String iss_ins_name;
    public String iss_ins_icon;
    public List<PricingStrategy> strategy;
}
