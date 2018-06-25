package com.axinfu.modellib.thrift.payment;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by zcrpro on 2018/3/13.
 */

public class AxfCouponInfo extends RealmObject implements Serializable {
    public String offst_amt;        // 优惠金额
    public String desc;                // 描述
}
