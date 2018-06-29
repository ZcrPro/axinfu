package modellib.thrift.unqr;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Vincent on 2017/11/14.
 */

public class RealName extends RealmObject implements Serializable{
    public String name;
    public String id_card_no;
    public String status;
}
