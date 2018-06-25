package com.axinfu.modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrpro on 2016/11/8.
 */
public interface CustomerService {

    //获取Customer
    @FormUrlEncoded
    @POST("CustomerService/get_customer/")
    Observable<String> getCustomer(@Field("json") String json, @Field("sign")String sign);

    // 更新个推ID
    @FormUrlEncoded
    @POST("CustomerService/update_pushid/")
    Observable<String> updateGeTuiId(@Field("json") String json, @Field("sign")String sign);

    // 这只支付密码
    @FormUrlEncoded
    @POST("CustomerService/setPayPassword/")
    Observable<String> setPayPassword(@Field("json") String json, @Field("sign")String sign);

    // 修改支付密码
    @FormUrlEncoded
    @POST("CustomerService/modifyPayPassword/")
    Observable<String> modifyPayPassword(@Field("json") String json, @Field("sign")String sign);

    // 重置支付密码
    @FormUrlEncoded
    @POST("CustomerService/resetPayPassword/")
    Observable<String> resetPayPassword(@Field("json") String json, @Field("sign")String sign);

    // 提交实名信息
    @FormUrlEncoded
    @POST("CustomerService/commitRealName/")
    Observable<String> commitRealName(@Field("json") String json, @Field("sign")String sign);

    // 获取姓名
    @FormUrlEncoded
    @POST("CustomerService/getRealName/")
    Observable<String> getRealName(@Field("json") String json, @Field("sign")String sign);

    // 获取实名认证信息
    @FormUrlEncoded
    @POST("CustomerService/get_realname/")
    Observable<String> getRealNameQR(@Field("json") String json, @Field("sign")String sign);

    // 验证实名信息（忘记支付密码）
    @FormUrlEncoded
    @POST("CustomerService/verify_realname/")
    Observable<String> verityPwd(@Field("json") String json, @Field("sign") String sign);

    // 获取验证信息（修改密码）
    @FormUrlEncoded
    @POST("CustomerService/get_reset_pwd_verify_info/")
    Observable<String> modifyPwdGetVerMsg(@Field("json") String json, @Field("sign") String sign);

    // 重置登录密码(新，不需要短信验证码)
    @FormUrlEncoded
    @POST("CustomerService/reset_password_by_verify_info/")
    Observable<String> resetPwdByField(@Field("json") String json, @Field("sign") String sign);

    // 校验修改密码的校验信息
    @FormUrlEncoded
    @POST("CustomerService/verify_customer_info/")
    Observable<String> verfieldMsg(@Field("json") String json, @Field("sign") String sign);

    // 注销账户
    @FormUrlEncoded
    @POST("CustomerService/cancel/")
    Observable<String> cancelAccount(@Field("json") String json, @Field("sign") String sign);

    // 注销账户
    @FormUrlEncoded
    @POST("CustomerService/commit_protocol/")
    Observable<String> commit_protocol(@Field("json") String json, @Field("sign") String sign);

    // 注销账户
    @FormUrlEncoded
    @POST("CustomerService/get_protocol_by_no/")
    Observable<String> get_protocol_by_no(@Field("json") String json, @Field("sign") String sign);

    // 注销账户
    @FormUrlEncoded
    @POST("CustomerService/verify_login_password/")
    Observable<String> verifyLoginPwd(@Field("json") String json, @Field("sign") String sign);

}

