package com.axinfu.modellib.thrift.unqr;

/**
 * Created by Vincent on 2017/11/15.
 */

public enum UPQROrderType {
    NormalConsumption(10),
    RestrictCreditConsumption(11),
    MiniMerchantConsumption(12),
    ATMEnchashment(13),
    Transfer(14);

    private int code;

    private UPQROrderType(int code) {

        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
