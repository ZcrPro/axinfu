package com.axinfu.modellib.thrift.customer;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by zcrpro on 2018/1/17.
 */

public class Protocol extends RealmObject implements Serializable {
    @PrimaryKey
    public String serial_no;            // 协议唯一标识
    public String protocol_no;            // 协议编号
    public String name;                    // 协议名称
    public String content;                // 协议内容
}
