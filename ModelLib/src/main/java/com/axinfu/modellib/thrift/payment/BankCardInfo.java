package com.axinfu.modellib.thrift.payment;

import java.io.Serializable;

/**
 * Created by zcrprozcrpro on 2017/8/1.
 */

public class BankCardInfo implements Serializable {
    public String bank_name;
    public String card_type;
    public String card_name;
    public String bank_icon;

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getBank_icon() {
        return bank_icon;
    }

    public void setBank_icon(String bank_icon) {
        this.bank_icon = bank_icon;
    }
}
