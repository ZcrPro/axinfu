package modellib.thrift.app;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2016/12/21.
 */

public class LastVersion extends RealmObject{
    @PrimaryKey
    public String appName;
    public String versionName;
}
