package modellib.thrift.unqr;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2017/12/7.
 */

public class UPQRPayRecord extends RealmObject implements Serializable{
    @PrimaryKey
    public String qr_code;
    public String status;
    public String amount;// 支付金额
    public String merchant_id;
    public String merchant_name;
    public RealmList<UPCoupon> couponInfo;
//    public String couponInfo;
    public String orig_amt;// 原金额
    public String order_no;
    public String order_time;
    public String voucher_num;
    public String order_type;
    public UPBankCard card;
    public String mode;
}
