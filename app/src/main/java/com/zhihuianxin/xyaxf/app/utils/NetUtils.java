package com.zhihuianxin.xyaxf.app.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Vincent on 2016/11/2.
 */

public class NetUtils {
    private static final String TAG = "NetUtils";

    public static String getRequestParams(Context context, Map<String, Object> paramMap) {
        paramMap.put("req", new MessageFactory().createBaseRequest(context));
        if (BuildConfig.AnXinDEBUG) {
            Log.d("req", "请求数据：" + new Gson().toJson(paramMap));
        }
        return new Gson().toJson(paramMap);
    }

    public static String getSign(String json) {
        String sign = "";
        try {
//             sign = Util.byte2HexStr(SecureLocal.signMessage((json+ "b4cab4f33afa805e5fd81c0f01465e3f").getBytes("UTF-8"))); //测试线上数据用这个
             sign = Util.byte2HexStr(Secure.signMessage((json).getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }
}
