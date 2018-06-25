package com.axinfu.modellib.thrift.payment;

import java.io.Serializable;

/**
 * Created by zcrpro on 2018/3/14.
 */

public class UPQRPayChannelRequest implements Serializable {

    public String pay_password;			// 支付密码, aes128加密
    public String bank_card_code;

}
