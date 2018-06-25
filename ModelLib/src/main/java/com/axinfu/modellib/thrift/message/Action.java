package com.axinfu.modellib.thrift.message;

import io.realm.RealmObject;

import java.io.Serializable;

/**
 * Created by zcrpro on 2016/11/8.
 */
public class Action extends RealmObject implements Serializable{
    public String type;
    public String args;
}
