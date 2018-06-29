package modellib.thrift.fee;

import android.annotation.SuppressLint;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
@SuppressLint("ParcelCreator")
public class SubFee extends RealmObject implements Serializable {
    @PrimaryKey
    public String id;  // required
    public String title;
    public String amount;
    public String min_pay_amount;
    public boolean is_priority;
    public String deduct_amount;	// 减免金额
    public String paid_amount;	// 已支付金额, 仅在应缴费列表中有效
    public String refund_amount;	// 退款金额
    public String business_channel;	// 业务渠道
    public String year;		// 学年
    public String total_amount;		// 总金额
    public String loan_amt;		// 贷款金额

    /**
     * 本地扩展一个存储在缴费列表是否选中的标志
     */
    @Ignore
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}