package modellib.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Vincent on 2016/10/8.
 */

public interface LoginService {
    // 获取手机号状态
    @FormUrlEncoded
    @POST("CustomerService/get_mobile_status/")
    Observable<String> getLoginStatus(@Field("json") String json, @Field("sign") String sign); //获取登录状态是否设置了密码

    // 获取城市列表
    @FormUrlEncoded
    @POST("ResourceService/get_cities/")
    Observable<String> getLoginCities(@Field("json") String json, @Field("sign") String sign); //获取城市信息列表

    // 获取学校列表
    @FormUrlEncoded
    @POST("ResourceService/get_schools/")
    Observable<String> getLoginSchools(@Field("json") String json, @Field("sign") String sign); //获取学校信息列表

    // 获取验证码
    @FormUrlEncoded
    @POST("SecureCodeService/send_sms_secureity_code/")
    Observable<String> getVerCode(@Field("json") String json, @Field("sign") String sign);     //获取手机验证码

    // 设置密码/充值密码
    @FormUrlEncoded
    @POST("CustomerService/reset_password/")
    Observable<String> setPwdAndLogin(@Field("json") String json, @Field("sign") String sign); //登录并且设置密码(充值密码)

    // 选择学校后更新数据
    @FormUrlEncoded
    @POST("CustomerService/update_school/")
    Observable<String> updateSchool(@Field("json") String json, @Field("sign") String sign);   //选择学校(更新学校信息)

    // 登录
    @FormUrlEncoded
    @POST("CustomerService/login/")
    Observable<String> login(@Field("json") String json, @Field("sign") String sign);          //登录

    // 注册
    @FormUrlEncoded
    @POST("CustomerService/register/")
    Observable<String> regist(@Field("json") String json, @Field("sign") String sign);          //登录
}
