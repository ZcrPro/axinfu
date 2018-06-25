package com.axinfu.modellib.thrift.ecard;

import com.axinfu.modellib.thrift.base.PayMethod;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;
import java.util.List;


/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */

public class ECard extends RealmObject implements Serializable {
    public String student_no;  // optional
    public String student_name;  // optional
    @PrimaryKey
    public String card_no;  // optional
    public String id_card_no;  // optional
    public String card_balance;  // optional
    public String losscard_type;  // required
    public String consumption_query_type;  // required
    @Ignore
    public List<PayMethod> pay_methods;  // required
    @Ignore
    public List<String> recharge_amts;  // optional
    public String status;
    public String status_desc;
    @Ignore
    public String charge_notice;

}