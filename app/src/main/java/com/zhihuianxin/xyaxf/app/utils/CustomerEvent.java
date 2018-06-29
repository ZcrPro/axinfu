package com.zhihuianxin.xyaxf.app.utils;

import modellib.thrift.customer.Customer;

/**
 * Created by zcrpro on 2016/11/23.
 */
public class CustomerEvent {

    public CustomerEvent(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public String id;
    public Customer customer;
}
