package com.axinfu.modellib.thrift.unqr;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Vincent on 2017/11/14.
 */

public class UPBankCard extends RealmObject implements Serializable{
    @PrimaryKey
    private String id;
    private String bank_name;
    private String card_type_name;
    private String card_no;
    private String iss_ins_code;
    private String iss_ins_name;
    private String iss_ins_icon;
    private String card_type;

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getIss_ins_icon() {
        return iss_ins_icon;
    }

    public void setIss_ins_icon(String iss_ins_icon) {
        this.iss_ins_icon = iss_ins_icon;
    }

    public String getIss_ins_name() {
        return iss_ins_name;
    }

    public void setIss_ins_name(String iss_ins_name) {
        this.iss_ins_name = iss_ins_name;
    }

    public String getIss_ins_code() {
        return iss_ins_code;
    }

    public void setIss_ins_code(String iss_ins_code) {
        this.iss_ins_code = iss_ins_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getCard_type_name() {
        return card_type_name;
    }

    public void setCard_type_name(String card_type_name) {
        this.card_type_name = card_type_name;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }
}

