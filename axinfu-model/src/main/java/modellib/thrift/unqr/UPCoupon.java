package modellib.thrift.unqr;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2017/11/14.
 */

public class UPCoupon extends RealmObject implements Serializable{
    public String type;
    public String spnsr_id;
    public String offst_amt;
    @PrimaryKey
    public String id;
    public String desc;
    public String  addn_info;
}
