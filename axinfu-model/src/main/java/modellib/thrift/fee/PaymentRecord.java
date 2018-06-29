package modellib.thrift.fee;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import modellib.thrift.base.PayMethod;
import modellib.thrift.payment.AxfCouponInfo;

/**
 * Created by Vincent on 2016/11/11.
 */

public class PaymentRecord extends RealmObject implements Serializable {
    @PrimaryKey
    public String order_no;
    public String order_time;
    public String payfor;
    public String order_status;
    public String order_status_desc;
    public String pay_amt;
    public String pay_channel;
    public String trade_summary;
    public String pay_time;
    public String pay_remark;
    public String orig_amt;
    public PayMethod pay_method;

    @Ignore
    public String refund_time;
    @Ignore// 退款时间
    public String cancel_time;                // 订单取消（冲正或者撤销）时间

    @Ignore
    public String voucher_num;
    @Ignore
    public String ecard_account;
    @Ignore
    public RecordFee fee;
    @Ignore
    public List<PricingStrategy> strategy = null;
    @Ignore
    public List<AxfCouponInfo> coupon_info = null;
    @Ignore
    public String merchant_name;
    @Ignore
    public String shop_name;
    @Ignore
    public String trade_type;
}
