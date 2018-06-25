package com.axinfu.modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrpro on 2017/10/31.
 */

public interface LoanService {

    // 获取已开通的支付渠道
    @FormUrlEncoded
    @POST("LoanService/get_account_info/")
    Observable<String> get_account_info(@Field("json") String json, @Field("sign") String sign);

    // 提交实名验证信息
    @FormUrlEncoded
    @POST("LoanService/commit_realname_info/")
    Observable<String> commit_realname_info(@Field("json") String json, @Field("sign") String sign);

    // 预授信校验接口
    @FormUrlEncoded
    @POST("LoanService/check_pre_approval/")
    Observable<String> check_pre_approval(@Field("json") String json, @Field("sign") String sign);

    // 提交预授信校验
    @FormUrlEncoded
    @POST("LoanService/pre_approval_register/")
    Observable<String> pre_approval_register(@Field("json") String json, @Field("sign") String sign);

    //贵阳银行支付成功通知
    @FormUrlEncoded
    @POST("LoanService/disburse_success_notify/")
    Observable<String> disburse_success_notify(@Field("json") String json, @Field("sign") String sign);

    //贵阳银行开户成功通知
    @FormUrlEncoded
    @POST("LoanService/open_account_success_notify/")
    Observable<String> open_account_success_notify(@Field("json") String json, @Field("sign") String sign);

    //贵阳银行开户成功通知
    @FormUrlEncoded
    @POST("LoanService/get_enum_data/")
    Observable<String> get_enum_data(@Field("json") String json, @Field("sign") String sign);

    //贵阳银行开户成功通知
    @FormUrlEncoded
    @POST("LoanService/apply_open_account/")
    Observable<String> apply_open_account(@Field("json") String json, @Field("sign") String sign);

    //获取账单头,最后一期已出账单
    @FormUrlEncoded
    @POST("LoanService/get_bills_header/")
    Observable<String> get_bills_header(@Field("json") String json, @Field("sign") String sign);


    //获取历史账单头列表
    @FormUrlEncoded
    @POST("LoanService/get_history_bills_headers/")
    Observable<String> get_history_bills_headers(@Field("json") String json, @Field("sign") String sign);


    //获取已出账单明细
    @FormUrlEncoded
    @POST("LoanService/get_outer_bills_details/")
    Observable<String> get_outer_bills_details(@Field("json") String json, @Field("sign") String sign);

    //获取未出账单明细
    @FormUrlEncoded
    @POST("LoanService/get_not_outer_bills_details/")
    Observable<String> get_not_outer_bills_details(@Field("json") String json, @Field("sign") String sign);

    //获取已还贷款明细
    @FormUrlEncoded
    @POST("LoanService/get_repaymented_loan_bills/")
    Observable<String> get_repaymented_loan_bills(@Field("json") String json, @Field("sign") String sign);

    //获取未还贷款明细
    @FormUrlEncoded
    @POST("LoanService/get_not_repaymented_loan_bills/")
    Observable<String> get_not_repaymented_loan_bills(@Field("json") String json, @Field("sign") String sign);

    //提前还款接口
    @FormUrlEncoded
    @POST("LoanService/repayment_amount/")
    Observable<String> advice_loan_bills(@Field("json") String json, @Field("sign") String sign);

    //提前还款试算
    @FormUrlEncoded
    @POST("LoanService/repayment_trial/")
    Observable<String> test_calculation_loan_bills(@Field("json") String json, @Field("sign") String sign);
}
