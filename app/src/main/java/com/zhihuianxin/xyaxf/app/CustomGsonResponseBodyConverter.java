package com.zhihuianxin.xyaxf.app;

import android.util.Log;

import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Vincent on 2016/12/9.
 */

public class CustomGsonResponseBodyConverter<T, F> implements Converter<ResponseBody, T> {

    CustomGsonResponseBodyConverter() {
    }

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        String response = responseBody.string();
        String[] params = response.split("&");
        if (params.length < 2) {
            throw new ApiException(AppConstant.SIGNATURE_OR_JSON_NOT_FOUND, "签名或json数据未找到");
        }
        String json = null, sign = null;
        for (String s : params) {
            String[] kv = s.split("=");
            if (kv.length != 2) {
                throw new ApiException(AppConstant.ILLEGAL_FORMAT_RESPONSE, "返回数据格式不正确");
            }
            String key = kv[0];
            String value;
            try {
                value = URLDecoder.decode(kv[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ApiException(AppConstant.CAN_DECODE_VALUE_UTF8, "Can not decode value with UTF-8");
            }
            if (key.equals("json")) {
                json = value;
            } else if (key.equals("sign")) {
                sign = value;
            }
        }

        String mySign;
        mySign = NetUtils.getSign(json);

        boolean signatureIsOK = sign != null && mySign != null && sign.equals(mySign);

        if (!signatureIsOK) {
            throw new ApiException(AppConstant.SIGNATURE_ERROR, "签名错误");
        }

        try {
            Log.d("data", json);
            JSONObject jsonObject = new JSONObject(json);
            JSONObject resJosn = jsonObject.getJSONObject("resp");
            String resCode = resJosn.getString("resp_code");
            String resDesc = resJosn.getString("resp_desc");
            if (resCode.equals(AppConstant.SUCCESS)||resCode.equals(AppConstant.FREE)) {
                return (T) json;
            } else if (resCode.equals(AppConstant.SESSION_OUT_OF_TIME) ||
                    resCode.equals(AppConstant.LOGIN_IN_OTHER_DEVICE) ||
                    resCode.equals(AppConstant.INVALID_SESSION) ||
                    resCode.equals(AppConstant.SCHOOL_REQUIRED) ||
                    resCode.equals(AppConstant.SESSION_ERROR_ID_NULL) ||
                    resCode.equals(AppConstant.SESSION_ERROR) ||
                    resCode.equals(AppConstant.SESSION_SETTING_ERROR)
                    ) {
                throw new ApiException(AppConstant.RELOGIN, "需要重新登录(" + resDesc + "_" + resCode + ")");
            } else {
                throw new ApiException(AppConstant.OTHER_SERVER_ERROR, resDesc + "(" + resCode + ")");
            }
        } catch (JSONException e) {
            throw new ApiException(AppConstant.INIT_JSON_ERROR, "json格式错误");
        }
    }
}
