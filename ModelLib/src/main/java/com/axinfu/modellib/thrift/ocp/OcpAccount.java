package com.axinfu.modellib.thrift.ocp;

import com.axinfu.modellib.thrift.base.BaseResponse;

import java.io.Serializable;

/**
 * Created by zcrpro on 2017/11/16.
 */

public class OcpAccount implements Serializable {
    public BaseResponse resp;
    public AxfQRPayAccount account;

    @Override
    public String toString() {
        return "OcpAccount{" +
                "resp=" + resp +
                ", axfQRPayAccount=" + account +
                '}';
    }
}