package com.axinfu.modellib.thrift.base;

import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.UPBankCard;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class PayMethod extends RealmObject implements Serializable {
    public String channel;
    public String merchant_id;
    public String merchant_code;
    public String promotion_hint;
    public String assistance_hint;
    @Ignore
    public List<String> supported_banks;
    public boolean is_recommended;            // 是否推广
    public boolean is_default;                    // 是否默认
    public UPBankCard card;
    public PaymentConfig payment_config;
    @Ignore
    public String purpose;
    @Ignore
    public boolean isSelected =false;
    @Ignore
    public boolean enabled =false;  //是否可用

}
