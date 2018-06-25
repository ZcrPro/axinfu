package com.axinfu.modellib.thrift.fee;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Vincent on 2016/12/1.
 */

public class FeeNotCheckCacheItem extends RealmObject implements Serializable{
    @PrimaryKey
    public String time;
    public String fee_number;
}
