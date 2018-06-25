package com.axinfu.modellib.thrift.resource;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2016/10/31.
 */

public class City extends RealmObject{
    @PrimaryKey
    public String code ;  // required
    public String name ;  // optional
    public String quanpin ;  // optional
}