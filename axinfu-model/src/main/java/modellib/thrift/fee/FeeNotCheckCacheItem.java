package modellib.thrift.fee;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2016/12/1.
 */

public class FeeNotCheckCacheItem extends RealmObject implements Serializable{
    @PrimaryKey
    public String time;
    public String fee_number;
}
