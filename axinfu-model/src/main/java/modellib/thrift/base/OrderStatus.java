package modellib.thrift.base;

import java.io.Serializable;

/**
 * Created by Vincent on 2016/11/29.
 */

public class OrderStatus implements Serializable{
    public static String unpay;		 // 未支付
    public static String paied;		 // 已支付
    public static String  success;	 // 已成功
    public static String  fail;		 // 处理失败
    public static String  refunding; // 退款中
    public static String refundsucc; // 已退款
    public static String refundfail; // 退款失败

    public static String getOrderStatus(String status){
        String str;
        if(status.equals("unpay")){
            str = "未支付";
        }else if(status.equals("paied")){
            str = "已支付";
        }else if(status.equals("success")){
            str = "已成功";
        }else if(status.equals("fail")){
            str = "处理失败";
        }else if(status.equals("refunding")){
            str = "退款中";
        }else if(status.equals("refundsucc")){
            str = "已退款";
        }else if(status.equals("refundfail")){
            str = "退款失败";
        }else if(status.equals("processing")){
            str = "处理中";
        }else {
            str = "";
        }
        return str;
    }
}
