package com.axinfu.modellib.thrift.bankcard;

/**
 * Created by zcrpro on 2018/3/19.
 */

public class UPCardType {
    public static final String Unknown = "00";        // 未知
    public static final String Debit = "01";                        // 借记账户
    public static final String Credit = "02";                        // 贷记账户
    public static final String QuasiCredit = "03";                    // 准贷记账户
    public static final String DebitCredit = "04";                    // 借贷合一账户
    public static final String Prepaid = "05";                    // 预付费账户
    public static final String SemiOpenPrepaid = "06";            // 半开放预付费账户
}
