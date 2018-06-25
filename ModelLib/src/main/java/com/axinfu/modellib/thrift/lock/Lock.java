package com.axinfu.modellib.thrift.lock;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by zcrpro on 2018/2/6.
 */

public class Lock extends RealmObject implements Serializable {
    @PrimaryKey
    public String mobile;
    public String gesturePassword;
    public boolean gestureStatus = false;
    public boolean fingerStatus = false;
    public boolean laterStatus = false;
    public boolean remindStatus = false;

}
