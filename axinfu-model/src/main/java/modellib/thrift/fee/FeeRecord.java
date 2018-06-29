package modellib.thrift.fee;


import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class FeeRecord extends RealmObject{
    public String fee_id;  // required
    public String title;  // required
    public String amount;  // required
    public RealmList<PayFeeRecord> pay_records;  // required
    public Boolean settled = false;
}