package modellib.thrift.unqr;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Vincent on 2017/11/14.
 */

public class PaymentConfig extends RealmObject implements Serializable{
    public boolean has_pay_password;
    public boolean pin_free;
    public String pin_free_amount;
    public String trade_limit_per_day;
}
