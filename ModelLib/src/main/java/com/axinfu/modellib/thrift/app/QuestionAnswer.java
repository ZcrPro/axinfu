package com.axinfu.modellib.thrift.app;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

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
