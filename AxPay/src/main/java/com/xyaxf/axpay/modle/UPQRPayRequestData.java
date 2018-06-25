package com.xyaxf.axpay.modle;

import com.axinfu.modellib.thrift.unqr.UPCoupon;

import java.util.ArrayList;

/**
 * Created by Vincent on 2017/11/16.
 */

public class UPQRPayRequestData {
    public String tn;
    public String amt;
    public String orig_amt;
    public ArrayList<UPCoupon> coupons;
    public String bank_card_id;
    public String payer_comments;
    public String qr_code;
}
