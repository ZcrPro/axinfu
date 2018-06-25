package com.zhihuianxin.xyaxf.app.unionqr_pay.entity;

import java.io.Serializable;

/**
 * Created by Vincent on 2017/11/9.
 */

public class UnionScanDataEntity implements Serializable{
    private String name;
    private String price;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
