package com.axinfu.modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrpro on 2016/11/10.
 */
public interface FeeService {

    // 开通缴费账户
    @FormUrlEncoded
    @POST("FeeService/open_fee_account/")
    Observable<String> openFeeAccount(@Field("json") String json, @Field("sign")String sign);

    // 获取缴费列表
    @FormUrlEncoded
    @POST("FeeService/get_fees/")
    Observable<String> getFees(@Field("json") String json, @Field("sign")String sign);

    // 获取缴费列表(临时入口)
    @FormUrlEncoded
    @POST("FeeService/get_other_fees/")
    Observable<String> getOtherFees(@Field("json") String json, @Field("sign")String sign);

    // 获取已经缴费的
    @FormUrlEncoded
    @POST("FeeService/get_fee_records/")
    Observable<String> getFeesRecord(@Field("json") String json, @Field("sign")String sign);

    // 获取已经缴费的(临时入口)
    @FormUrlEncoded
    @POST("FeeService/get_other_fee_records/")
    Observable<String> getOtherFeesRecord(@Field("json") String json, @Field("sign")String sign);

    // 获取已经缴费的(新生)
    @FormUrlEncoded
    @POST("FeeService/get_new_student_fee_records/")
    Observable<String> get_new_student_fee_records(@Field("json") String json, @Field("sign")String sign);

    // 获取新生缴费(临时入口)
    @FormUrlEncoded
    @POST("FeeService/get_new_student_fees/")
    Observable<String> getNewStudentFees(@Field("json") String json, @Field("sign")String sign);

    // 获取应缴费列表
    @FormUrlEncoded
    @POST("FeeService/get_required_fees/")
    Observable<String> get_required_fees(@Field("json") String json, @Field("sign")String sign);

    // 获取新生缴费记录详情
    @FormUrlEncoded
    @POST("FeeService/get_fee_record/")
    Observable<String> get_fee_record(@Field("json") String json, @Field("sign")String sign);


    // 获取新生缴费记录详情
    @FormUrlEncoded
    @POST("FeeService/get_fee_by_id/")
    Observable<String> get_fee_by_id(@Field("json") String json, @Field("sign")String sign);
}
