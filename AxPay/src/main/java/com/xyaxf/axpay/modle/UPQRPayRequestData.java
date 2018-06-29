package com.xyaxf.axpay.modle;


import java.util.ArrayList;

import modellib.thrift.unqr.UPCoupon;

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
