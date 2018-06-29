package modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Vincent on 2017/11/13.
 * 银联二维码相关
 */

public interface UPQRService {
    // 获取银行卡
    @FormUrlEncoded
    @POST("UPQRService/getUPBankCards/")
    Observable<String> getUPBankCards(@Field("json") String json, @Field("sign") String sign);

    // 添加银行卡
    @FormUrlEncoded
    @POST("UPQRService/applyAddBankCard/")
    Observable<String> applyAddBankCard(@Field("json") String json, @Field("sign") String sign);

    // 删除银行卡
    @FormUrlEncoded
    @POST("UPQRService/removeBankCard/")
    Observable<String> removeBankCard(@Field("json") String json, @Field("sign") String sign);

    // 获取订单信息
    @FormUrlEncoded
    @POST("UPQRService/getOrderInfo/")
    Observable<String> getOrderInfo(@Field("json") String json, @Field("sign") String sign);

    // 获取银行卡
    @FormUrlEncoded
    @POST("UPQRService/get_bank_cards/")
    Observable<String> getUPQRBankCards(@Field("json") String json, @Field("sign") String sign);

    // 提交实名认证信息
    @FormUrlEncoded
    @POST("UPQRService/commit_realname/")
    Observable<String> commitRealName(@Field("json") String json, @Field("sign") String sign);

    // 申请绑定银行卡
    @FormUrlEncoded
    @POST("UPQRService/apply_add_bank_card/")
    Observable<String> applyUPQRPayBankCard(@Field("json") String json, @Field("sign") String sign);

    // 申请绑定银行卡
    @FormUrlEncoded
    @POST("UPQRService/get_up_qr_order/")
    Observable<String> getUPQROrder(@Field("json") String json, @Field("sign") String sign);

    // 删除银行卡
    @FormUrlEncoded
    @POST("UPQRService/delete_bank_card/")
    Observable<String> delUPBankCard(@Field("json") String json, @Field("sign") String sign);

    // 获取优惠信息
    @FormUrlEncoded
    @POST("UPQRService/get_up_qr_coupon/")
    Observable<String> getUPCoupon(@Field("json") String json, @Field("sign") String sign);

    // 获取被扫二维码
    @FormUrlEncoded
    @POST("UPQRService/get_c2b_qr_code/")
    Observable<String> getC2BCode(@Field("json") String json, @Field("sign") String sign);

    // 被扫支付记录
    @FormUrlEncoded
    @POST("UPQRService/get_pay_records/")
    Observable<String> getUpqrSwepRecord(@Field("json") String json, @Field("sign") String sign);

    //C2B二维码支付验证支付密码
    @FormUrlEncoded
    @POST("UPQRService/c2b_qr_verify_payment_password/")
    Observable<String> c2bVerPwd(@Field("json") String json, @Field("sign") String sign);

    //获取支付结果详细记录
    @FormUrlEncoded
    @POST("UPQRService/get_pay_record_detail/")
    Observable<String> getUnionPayResultDetail(@Field("json") String json, @Field("sign") String sign);

    //获取支付结果详细记录
    @FormUrlEncoded
    @POST("UPQRService/get_pay_methods/")
    Observable<String> get_pay_methods(@Field("json") String json, @Field("sign") String sign);
}
