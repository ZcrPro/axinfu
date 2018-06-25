package com.axinfu.modellib.thrift.message;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Vincent on 2016/11/15.
 */

public class ImportantMessage extends RealmObject implements Serializable{
    @PrimaryKey
    public String id;
    public String title;
    public String content;
    public String time;
    public String timestamp;
    public String action;
}
