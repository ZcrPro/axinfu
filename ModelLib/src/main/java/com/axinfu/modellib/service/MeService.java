package com.axinfu.modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Vincent on 2016/11/7.
 */

public interface MeService {
    // 修改基础信息
    @FormUrlEncoded
    @POST("CustomerService/update_base_info/")
    Observable<String> modifyBaseInfo(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    //修改手机号
    @FormUrlEncoded
    @POST("CustomerService/modify_mobile/")
    Observable<String> modifyMoblie(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    //修改密码
    @FormUrlEncoded
    @POST("CustomerService/modify_password/")
    Observable<String> modifyPwd(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 更新一卡通账户信息
    @FormUrlEncoded
    @POST("ECardService/update_ecard_account/")
    Observable<String> updateECardAccount(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 更新缴费信息
    @FormUrlEncoded
    @POST("FeeService/update_fee_account/")
    Observable<String> updateFeeAccount(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 获取FeedBack信息
    @FormUrlEncoded
    @POST("AppService/get_feedback_list/")
    Observable<String> getFeedBack(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 获取FeedBack信息
    @FormUrlEncoded
    @POST("AppService/feedback/")
    Observable<String> feedBack(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 上传图片前
    // 获取FeedBack信息
    @FormUrlEncoded
    @POST("ResourceService/query_upload_access/")
    Observable<String> getQiNiuAccess(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 获取帮助
    @FormUrlEncoded
    @POST("AppService/get_question_list/")
    Observable<String> getHelpCenterData(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 获取关闭的缴费记录
    @FormUrlEncoded
    @POST("PaymentService/get_closed_payment_records/")
    Observable<String> getClosedPaymentRecords(@Field("json") String json, @Field("sign")String sign);

    // 获取缴费记录
    @FormUrlEncoded
    @POST("PaymentService/get_payment_records/")
    Observable<String> getPaymentRecords(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别


    // 获取应用更新情况
    @FormUrlEncoded
    @POST("AppService/check_update/")
    Observable<String> getCheckUpdate(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 获取Customer
    @FormUrlEncoded
    @POST("CustomerService/get_customer/")
    Observable<String> getCustomer(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别

    // 获取加载页的图片
    @FormUrlEncoded
    @POST("AppService/get_splash_image/")
    Observable<String> getSplashUrl(@Field("json") String json, @Field("sign")String sign); //个人资料中修改昵称,性别
}