package modellib.thrift.payment;

import java.io.Serializable;

/**
 * Created by zcrprozcrpro on 2017/8/2.
 */

public class QuickPayChannelRequest implements Serializable {
    public String bank_card_no;    // 银行卡号, aes128加密
    public String name;            // 姓名, aes128加密
    public String id_card_no;        // 身份证号, aes128加密
    public String mobile;        // 手机号, aes128加密
}
