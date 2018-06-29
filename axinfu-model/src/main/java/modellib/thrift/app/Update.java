package modellib.thrift.app;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Vincent on 2016/11/16.
 */

public class Update extends RealmObject implements Serializable{
    public String name;
    public String update_type;
    public String current_version;
    public String update_message;
    public String update_url;
}
