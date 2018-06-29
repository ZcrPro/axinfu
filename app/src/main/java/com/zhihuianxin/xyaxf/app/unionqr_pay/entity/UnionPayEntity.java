package com.zhihuianxin.xyaxf.app.unionqr_pay.entity;

import modellib.thrift.unqr.UPBankCard;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vincent on 2017/11/9.
 */

public class UnionPayEntity implements Serializable{
    private UPQROrder upqrOrder;
    private ArrayList<UPBankCard> bankCards;
    private UPCoupon upCoupon;

    public UPQROrder getUpqrOrder() {
        return upqrOrder;
    }

    public void setUpqrOrder(UPQROrder upqrOrder) {
        this.upqrOrder = upqrOrder;
    }

    public ArrayList<UPBankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(ArrayList<UPBankCard> bankCards) {
        this.bankCards = bankCards;
    }

    public UPCoupon getUpCoupon() {
        return upCoupon;
    }

    public void setUpCoupon(UPCoupon upCoupon) {
        this.upCoupon = upCoupon;
    }
}
