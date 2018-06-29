package modellib.thrift.fee;

import java.io.Serializable;

import io.realm.RealmObject;

public class PricingStrategy extends RealmObject implements Serializable {
    public String id;                          // ID
    public String name;                        // 计价策略名称
    public String float_amt;                   // 该项计价策略浮动金额
}
