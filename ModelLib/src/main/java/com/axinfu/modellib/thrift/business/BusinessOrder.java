package com.axinfu.modellib.thrift.business;

import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2017/1/8.
 */

public class BusinessOrder {//extends RealmObject{
    @PrimaryKey
    public Business business;
    public String order;
}
