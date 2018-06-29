package com.zhihuianxin.xyaxf.app.unionqr_pay.entity;

import modellib.thrift.unqr.UPBankCard;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vincent on 2017/12/4.
 */

public class UnionSweptEntity implements Serializable{
    private ArrayList<UPBankCard> bankCards;

    public ArrayList<UPBankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(ArrayList<UPBankCard> bankCards) {
        this.bankCards = bankCards;
    }
}
