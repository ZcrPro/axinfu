package com.axinfu.modellib.thrift.unqr;

import io.realm.RealmObject;

import java.io.Serializable;

/**
 * Created by Vincent on 2017/11/14.
 */

public class RealName extends RealmObject implements Serializable{
    public String name;
    public String id_card_no;
    public String status;
}
