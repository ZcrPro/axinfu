package modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrpro on 2017/11/16.
 */

public interface AxfQRPayService {

    //获取账户状态
    @FormUrlEncoded
    @POST("AxfQRPayService/get_account_info/")
    Observable<String> get_account_info(@Field("json") String json, @Field("sign") String sign);

    //获取账户状态
    @FormUrlEncoded
    @POST("AxfQRPayService/acquiring/")
    Observable<String> acquiring(@Field("json") String json, @Field("sign") String sign);

    //一码通开通
    @FormUrlEncoded
    @POST("AxfQRPayService/open_account/")
    Observable<String> open_account(@Field("json") String json, @Field("sign") String sign);

  //更新用户信息
    @FormUrlEncoded
    @POST("AxfQRPayService/update_account_info/")
    Observable<String> update_account_info(@Field("json") String json, @Field("sign") String sign);

    //更新用户信息
    @FormUrlEncoded
    @POST("AxfQRPayService/get_customer_types/")
    Observable<String> get_customer_types(@Field("json") String json, @Field("sign") String sign);

    //新的计价策略
    @FormUrlEncoded
    @POST("AxfQRPayService/get_pricing_strategy/")
    Observable<String> get_pricing_strategy(@Field("json") String json, @Field("sign") String sign);

}
