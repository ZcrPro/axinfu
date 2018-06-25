package com.zhihuianxin.xyaxf.app;

import com.google.gson.stream.JsonReader;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLDecoder;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Vincent on 2016/12/2.
 */

public class ToStringConverterFactory extends Converter.Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");


    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody responseBody) throws IOException {
                    String response = responseBody.string();
                    String[] params = response.split("&");
                    if (params.length < 2) {
                        throw new ApiException(AppConstant.SIGNATURE_OR_JSON_NOT_FOUND,"签名或json数据未找到");
                    }
                    String json = null, sign = null;
                    for (String s : params) {
                        String[] kv = s.split("=");
                        if (kv.length != 2) {
                            throw new ApiException(AppConstant.ILLEGAL_FORMAT_RESPONSE,"返回数据格式不正确");
                        }
                        String key = kv[0];
                        String value;
                        try {
                            value = URLDecoder.decode(kv[1], "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new ApiException(AppConstant.CAN_DECODE_VALUE_UTF8,"Can not decode value with UTF-8");
                        }
                        if (key.equals("json")) {
                            json = value;
                        } else if (key.equals("sign")) {
                            sign = value;
                        }
                    }

                    JsonReader jsonReader = null;
                    String mySign;
                    mySign = NetUtils.getSign(json);

                    boolean signatureIsOK = sign != null && mySign != null && sign.equals(mySign);

                    if (!signatureIsOK) {
                        throw new ApiException(AppConstant.SIGNATURE_ERROR,"签名错误");
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONObject resJosn = jsonObject.getJSONObject("resp");
                        String resCode = resJosn.getString("resp_code");
                        String resDesc = resJosn.getString("resp_desc");
                        if(resCode.equals(AppConstant.SUCCESS)){
//                            MediaType contentType = responseBody.contentType();
//                            Charset charset = contentType != null ? contentType.charset(Charset.forName("UTF-8")) : Charset.forName("UTF-8");
//                            JSONObject customerJson = (JSONObject) jsonObject.remove("resp");
//                            InputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
//                            Reader reader = new InputStreamReader(inputStream, charset);
//                            jsonReader = gson.newJsonReader(reader);
                            return response;
                        } else{
                            //Server error code.
                        }
                    } catch (JSONException e) {
                        throw new ApiException(AppConstant.INIT_JSON_ERROR,"json格式错误");
                    }
                    return null;
                }
            };
        }
        return null;
    }

    @Override public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                                    Annotation[] methodAnnotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<String, RequestBody>() {
                @Override
                public RequestBody convert(String value) throws IOException {
                    return RequestBody.create(MEDIA_TYPE, value);
                }
            };
        }
        return null;
    }
}