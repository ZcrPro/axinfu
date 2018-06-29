package modellib.thrift.unqr;


import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import modellib.thrift.base.PayMethod;

/**
 * Created by Vincent on 2017/11/14.
 */

public class UPQROrder extends RealmObject implements Serializable{
    public String amt;
    public int valid_time;
    @PrimaryKey
    public String tn;
    public String order_time;
    public String payee_comments;
    public String order_type;
    public UPQRPayeeInfo payee_info;
    @Ignore
    public List<PayMethod> pay_methods;
}
