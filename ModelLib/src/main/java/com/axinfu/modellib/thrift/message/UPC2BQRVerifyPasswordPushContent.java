package com.axinfu.modellib.thrift.message;

import com.axinfu.modellib.thrift.unqr.UPBankCard;

import java.io.Serializable;

/**
 * Created by Vincent on 2017/12/7.
 */

public class UPC2BQRVerifyPasswordPushContent extends BasePushContent implements Serializable{
    private String resp_code;
    private String resp_desc;
    private String qr_code;
    private String amount;
    private String merchant_code;
    private String merchant_name;
    private String acq_code;
    private UPBankCard card_info;

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

    public UPBankCard getCard_info() {
        return card_info;
    }

    public void setCard_info(UPBankCard card_info) {
        this.card_info = card_info;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchant_code() {
        return merchant_code;
    }

    public void setMerchant_code(String merchant_code) {
        this.merchant_code = merchant_code;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getAcq_code() {
        return acq_code;
    }

    public void setAcq_code(String acq_code) {
        this.acq_code = acq_code;
    }
}
