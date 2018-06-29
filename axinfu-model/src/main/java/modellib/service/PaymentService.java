package modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrpro on 2016/11/29.
 */
public interface PaymentService {

    //支付下单
    @FormUrlEncoded
    @POST("PaymentService/pay_order/")
    Observable<String> payOder(@Field("json") String json, @Field("sign") String sign);

    //支付下单
    @FormUrlEncoded
    @POST("PaymentService/get_pay_limit/")
    Observable<String> bankLimit(@Field("json") String json, @Field("sign") String sign);

    //支付下单
    @FormUrlEncoded
    @POST("PaymentService/get_pay_result/")
    Observable<String> getPayResult(@Field("json") String json, @Field("sign") String sign);

    //扫码支付备份订单
    @FormUrlEncoded
    @POST("PaymentService/commit_order_record/")
    Observable<String> CommitOrder(@Field("json") String json, @Field("sign") String sign);

    //设置支付密码
    @FormUrlEncoded
    @POST("PaymentService/set_payment_password/")
    Observable<String> setPayPwd(@Field("json") String json, @Field("sign") String sign);

    //获取支付密码配置信息
    @FormUrlEncoded
    @POST("PaymentService/get_payment_config/")
    Observable<String> getPaymentConfig(@Field("json") String json, @Field("sign") String sign);

    //验证支付密码信息
    @FormUrlEncoded
    @POST("PaymentService/verify_payment_password/")
    Observable<String> verifyPaymentPwd(@Field("json") String json, @Field("sign") String sign);

    //修改支付密码
    @FormUrlEncoded
    @POST("PaymentService/modify_payment_password/")
    Observable<String> modifyPaymentPwd(@Field("json") String json, @Field("sign") String sign);

    //清除支付密码
    @FormUrlEncoded
    @POST("PaymentService/clear_payment_password/")
    Observable<String> clearPaymentPwd(@Field("json") String json, @Field("sign") String sign);

    //设置小额免密
    @FormUrlEncoded
    @POST("PaymentService/set_pin_free/")
    Observable<String> setPinFree(@Field("json") String json, @Field("sign") String sign);

    //支付下单
    @FormUrlEncoded
    @POST("PaymentService/query_order_pay_record/")
    Observable<String> query_order_pay_record(@Field("json") String json, @Field("sign") String sign);
}
