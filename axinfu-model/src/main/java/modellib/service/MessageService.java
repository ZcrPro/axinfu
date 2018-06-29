package modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zcrpro on 2016/11/7.
 */
public interface MessageService {

    //获取banner广告内容
    @FormUrlEncoded
    @POST("MessageService/get_advertises/")
    Observable<String> getAdvertises(@Field("json") String json, @Field("sign") String sign);

    //获取通知消息
    @FormUrlEncoded
    @POST("MessageService/get_messages/")
    Observable<String> getMsg(@Field("json") String json, @Field("sign") String sign);


    //获取通知消息
    @FormUrlEncoded
    @POST("MessageService/get_important_messages/")
    Observable<String> getImportantMsg(@Field("json") String json, @Field("sign") String sign);
}
