package com.axinfu.modellib.thrift.customer;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2018/1/11.
 */

public class UserGesture extends RealmObject implements Serializable{
    @PrimaryKey
    public String mobile;
    public String gesturePwd;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGesturePwd() {
        return gesturePwd;
    }

    public void setGesturePwd(String gesturePwd) {
        this.gesturePwd = gesturePwd;
    }
}
