package com.axinfu.modellib.thrift.message;

import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.axinfu.modellib.thrift.unqr.UPCoupon;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vincent on 2017/12/7.
 */

public class UPC2BQRPayResultPushContent extends BasePushContent implements Serializable{
    private String resp_code;
    private String resp_desc;
    private String qr_code;
    private String status;
    private String amount;
    private String merchant_id;
    private String merchant_name;
    private String trade_summary;
    private List<UPCoupon> couponInfo;
    private String orig_amt;
    private String order_no;
    private String pay_time;
    private String refund_time;
    private String pay_amt;
    private UPBankCard card_info;
    public String payfor;

    public List<com.axinfu.modellib.thrift.message.PricingStrategy> getStrategy() {
        return strategy;
    }

    public void setStrategy(List<com.axinfu.modellib.thrift.message.PricingStrategy> strategy) {
        this.strategy = strategy;
    }

    public String getTrade_summary() {
        return trade_summary;
    }

    public void setTrade_summary(String trade_summary) {
        this.trade_summary = trade_summary;
    }

    private List<com.axinfu.modellib.thrift.message.PricingStrategy> strategy;	// 一码通计价策略;

    public String getPayfor() {
        return payfor;
    }

    public void setPayfor(String payfor) {
        this.payfor = payfor;
    }

    public String getRefund_time() {
        return refund_time;
    }

    public void setRefund_time(String refund_time) {
        this.refund_time = refund_time;
    }

    public String getPay_amt() {
        return pay_amt;
    }

    public void setPay_amt(String pay_amt) {
        this.pay_amt = pay_amt;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getResp_code() {
        return resp_code;
    }

    public void setResp_code(String resp_code) {
        this.resp_code = resp_code;
    }

    public String getResp_desc() {
        return resp_desc;
    }

    public void setResp_desc(String resp_desc) {
        this.resp_desc = resp_desc;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public List<UPCoupon> getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(List<UPCoupon> couponInfo) {
        this.couponInfo = couponInfo;
    }

    public String getOrig_amt() {
        return orig_amt;
    }

    public void setOrig_amt(String orig_amt) {
        this.orig_amt = orig_amt;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public UPBankCard getCard_info() {
        return card_info;
    }

    public void setCard_info(UPBankCard card_info) {
        this.card_info = card_info;
    }
}
