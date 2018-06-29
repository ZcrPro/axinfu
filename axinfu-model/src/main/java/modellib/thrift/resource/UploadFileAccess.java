package modellib.thrift.resource;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Vincent on 2016/11/12.
 */

public class UploadFileAccess extends RealmObject implements Serializable {
    public String uptoken;
    public String key;
    public String access_url;
}
