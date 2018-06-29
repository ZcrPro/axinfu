package modellib.thrift.message;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by zcrpro on 2016/11/8.
 */
public class Action extends RealmObject implements Serializable{
    public String type;
    public String args;
}
