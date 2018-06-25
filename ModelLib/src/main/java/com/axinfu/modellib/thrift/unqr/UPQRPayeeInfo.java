package com.axinfu.modellib.thrift.unqr;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Vincent on 2017/11/14.
 */

public class UPQRPayeeInfo extends RealmObject implements Serializable{
    public String name;
    public String merchant_code;
    public String termId;
    @PrimaryKey
    public String id;
}
