package modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrpro on 2016/11/8.
 */
public interface EcardService {

    //开通ecard
    @FormUrlEncoded
    @POST("ECardService/open_ecard_account/")
    Observable<String> openEcard(@Field("json") String json, @Field("sign") String sign);


    //获取ecard
    @FormUrlEncoded
    @POST("ECardService/get_ecard/")
    Observable<String> getEcard(@Field("json") String json, @Field("sign") String sign);


    //获取ecard充值记录
    @FormUrlEncoded
    @POST("ECardService/get_ecard_charge_records/")
    Observable<String> getChargeRecords(@Field("json") String json, @Field("sign") String sign);

    //获取ecard记录
    @FormUrlEncoded
    @POST("ECardService/get_ecard_records/")
    Observable<String> getRecords(@Field("json") String json, @Field("sign") String sign);

    //验证一卡通密码
    @FormUrlEncoded
    @POST("ECardService/verify_password/")
    Observable<String> verifyPassword(@Field("json") String json, @Field("sign") String sign);

    //挂失
    @FormUrlEncoded
    @POST("ECardService/report_loss/")
    Observable<String> reportLoss(@Field("json") String json, @Field("sign") String sign);

    //解除挂失
    @FormUrlEncoded
    @POST("ECardService/cancel_freeze/")
    Observable<String> cancelFreeze(@Field("json") String json, @Field("sign") String sign);

}
