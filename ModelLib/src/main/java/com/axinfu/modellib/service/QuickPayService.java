package com.axinfu.modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrprozcrpro on 2017/5/18.
 */

public interface QuickPayService {

    //签约申请
    @FormUrlEncoded
    @POST("QuickPayService/apply_contract/")
    Observable<String> apply_contract(@Field("json") String json, @Field("sign") String sign);

    //验证短信重发
    @FormUrlEncoded
    @POST("QuickPayService/resend_sms_code/")
    Observable<String> resend_sms_code(@Field("json") String json, @Field("sign") String sign);

    //签约确认
    @FormUrlEncoded
    @POST("QuickPayService/confirm_contract/")
    Observable<String> confirm_contract(@Field("json") String json, @Field("sign") String sign);

}
