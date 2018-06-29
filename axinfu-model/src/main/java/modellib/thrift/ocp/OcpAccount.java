package modellib.thrift.ocp;


import java.io.Serializable;

import modellib.thrift.base.BaseResponse;

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