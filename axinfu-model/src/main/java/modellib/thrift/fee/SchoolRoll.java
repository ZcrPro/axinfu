package modellib.thrift.fee;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by zcrprozcrpro on 2017/5/15.
 */

public class SchoolRoll extends RealmObject implements Serializable {

    @PrimaryKey
    public String mobile;
    public String district;
    public String academy;
    public String grade;
    public String clazz;
    public String major;

}
