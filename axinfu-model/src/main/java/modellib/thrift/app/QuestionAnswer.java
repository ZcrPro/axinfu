package modellib.thrift.app;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2016/11/11.
 */

public class QuestionAnswer extends RealmObject implements Serializable{
    @PrimaryKey
    public String id;
    public String type;
    public String question;
    public String content;
    public String create_time;
}
