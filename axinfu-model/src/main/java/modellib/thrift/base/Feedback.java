package modellib.thrift.base;


import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import modellib.thrift.app.Appendix;

/**
 * Created by Vincent on 2016/11/10.
 */

public class Feedback extends RealmObject implements Serializable{
    @PrimaryKey
    public String id;
    public String question;
    public String date;
    public String answer;
    public RealmList<Appendix> appendices;
}
