package com.zhihuianxin.xyaxf.app.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.thrift.base.BaseMessageObject;
import modellib.thrift.base.BaseRequest;
import modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;

/**
 * Created by John on 2014/7/28.
 */
public class MessageFactory {
	private static final String TAG = "MessageFactory";

	public String toStringMessage(BaseMessageObject obj){
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}

	public <T extends BaseMessageObject> T create(Context context, Class<T> clazz){
		try {
			BaseMessageObject msg = clazz.newInstance();
			if(msg instanceof BaseRequest){
				initBaseRequest(context, (BaseRequest)msg);
			}
			if(msg instanceof BaseResponse){
				initBaseResponse(context, (BaseResponse)msg);
			}
			return (T)msg;
		} catch (Exception e) {
			Log.e(TAG, String.format("Create msg: %s failed", clazz), e);
			return null;
		}
	}

	public <T extends BaseMessageObject> T clone(BaseMessageObject obj){
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		T newObj = (T)gson.fromJson(json, ((Object)obj).getClass());

		return newObj;
	}

	protected void initBaseRequest(Context context, BaseRequest baseRequest){
		baseRequest.app_name = context.getPackageName();
		baseRequest.app_version = Util.getPackageInfo(context).versionName;
		baseRequest.sys_name = AppConstant.SYSTEM_NAME.name();
		baseRequest.sys_version = Build.VERSION.RELEASE;
		baseRequest.session_id = App.mSession.getSession();
	}

	protected void initBaseResponse(Context context, BaseResponse baseResponse){
		baseResponse.resp_code = AppConstant.SUCCESS;
		baseResponse.resp_desc = "成功";
	}

	public BaseRequest createBaseRequest(Context context){
		BaseRequest req = create(context, BaseRequest.class);
		return req;
	}
}
