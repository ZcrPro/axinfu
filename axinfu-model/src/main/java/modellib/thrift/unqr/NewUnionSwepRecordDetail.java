package modellib.thrift.unqr;

import java.io.Serializable;

/**
 * Created by Vincent on 2017/12/13.
 */

public class NewUnionSwepRecordDetail implements Serializable{
    public String qr_code;
    public String status;
    public String amount;
    public String merchant_id;
    public String merchant_name;
    public String orig_amt;
    public String order_no;
    public String order_time;
    public String voucher_num;
    public String order_type;
    public String mode;

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
}
