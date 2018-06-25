package com.axinfu.modellib.thrift.message;

import java.io.Serializable;

/**
 * Created by zcrpro on 2016/11/7.
 */
public class Advertise implements Serializable{
    public String id;    // ID
    public String title;    // 标题
    public String image;            // 图片
    public String time;        // 创建时间
    public Action action;            // 动作
}
