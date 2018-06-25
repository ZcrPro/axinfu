package com.axinfu.modellib.thrift.payment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zcrprozcrpro on 2017/8/1.
 */

public class QuickPayMethod implements Serializable {
    public String merchant_code;
    public String channel;
    public String promotion_hint;
    public String assistance_hint;
    public List<String> supported_banks;
}
