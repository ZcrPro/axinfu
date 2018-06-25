package com.axinfu.modellib.thrift.ecard;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class ECardChargeRecord extends RealmObject {
    public String time;  // required
    public String amount;  // required
    @PrimaryKey
    public String order_no;  // required
    public String status;

}