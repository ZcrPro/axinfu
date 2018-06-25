package com.axinfu.modellib.thrift.fee;

import java.io.Serializable;

/**
 * Created by Vincent on 2016/11/29.
 */

public class PayFor implements Serializable{
    public static String RechargeECard;
    public static String  SchoolFee;
    public static String  ScanPay;
    public static String  GatewayPay;
    public static String  RechargeMobile;
    public static String  RechargeFlow;
    public static String  TdtcFee;

    public static String getPayFor(String payfor){
        String str;
        if(payfor.equals("RechargeECard")){
            str = "一卡通充值";
        } else if(payfor.equals("SchoolFee")){
            str = "缴费";
        } else if(payfor.equals("ScanPay")){
            str = "";
        } else if(payfor.equals("GatewayPay")){
            str = "";
        } else if(payfor.equals("RechargeMobile")){
            str = "话费充值";
        } else if(payfor.equals("RechargeFlow")){
            str = "流量充值";
        } else if(payfor.equals("TdtcFee")){
            str = "天大天财缴费";
        } else{
            str = "";
        }
        return str;
    }
}
