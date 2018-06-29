package modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrprozcrpro on 2017/5/18.
 */

public interface BankCardService {

    //获取银行卡
    @FormUrlEncoded
    @POST("BankCardService/get_bank_cards/")
    Observable<String> get_bank_cards(@Field("json") String json, @Field("sign") String sign);

      //添加银行卡
    @FormUrlEncoded
    @POST("BankCardService/add_bank_card/")
    Observable<String> add_bank_card(@Field("json") String json, @Field("sign") String sign);

      //添加银行卡
    @FormUrlEncoded
    @POST("BankCardService/del_bank_cards/")
    Observable<String> del_bank_cards(@Field("json") String json, @Field("sign") String sign);


    //验证银行卡
    @FormUrlEncoded
    @POST("BankCardService/get_bankcard_info/")
    Observable<String> get_bankcard_info(@Field("json") String json, @Field("sign") String sign);
}
