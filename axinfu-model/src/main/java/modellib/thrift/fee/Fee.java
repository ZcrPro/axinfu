package modellib.thrift.fee;


import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import modellib.thrift.base.PayMethod;

;


/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class Fee extends RealmObject implements Serializable {
    @PrimaryKey
    public String id;  // required
    public String title;  // required
    public String amount;  // required
    public String min_pay_amount;  // required
    public String list;  // required
    public String info;  // optional
    public String start_date;  // optional
    public RealmList<SubFee> subfees;  // optional
    public String end_date;  // optional
    public String group_type;  // required
    public Boolean is_priority = false;  // optional
    public Boolean is_locked = false;  // optional
    @Ignore
    public List<PayMethod> pay_methods;  // optional
    public String business_channel;	// 业务渠道
    public String year;		// 学年

}