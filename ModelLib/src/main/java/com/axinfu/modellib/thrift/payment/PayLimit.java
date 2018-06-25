package com.axinfu.modellib.thrift.payment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhengzheng on 16/7/23.
 */
public class PayLimit  implements Serializable {
    public String trade_limit_amt;
    public ArrayList<BankLimit> bank_infos;
    public String out_of_limit_notice;
    public String other_bank_notice;
}

