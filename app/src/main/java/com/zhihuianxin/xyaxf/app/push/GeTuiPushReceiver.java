package com.zhihuianxin.xyaxf.app.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;

public class GeTuiPushReceiver extends BroadcastReceiver {
    public static final String TAG = "GeTuiPushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
			case PushConsts.GET_MSG_DATA:
				// 获取透传数据
				String appid = bundle.getString("appid");
				String taskid = bundle.getString("taskid");
				byte[] payload = bundle.getByteArray("payload");
				String messageid = bundle.getString("messageid");

				// noticeShowPoint(context);
				// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
				// boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
				if (payload != null) {
					String strPayload = Util.getUtf8String(payload);
					onGotMessage(context, appid, taskid, messageid, strPayload);
				}
				break;

			case PushConsts.GET_CLIENTID:
				String cid = bundle.getString("clientid");
				App.mAxLoginSp.setGetuiClientId(cid);
				//onGotClientID(context, cid);
				break;

			case PushConsts.THIRDPART_FEEDBACK:
				break;

			default:
				break;
		}
	}

	public void onGotMessage(Context context, String appid, String taskid, String messageid, String payload){
		Log.d(TAG, String.format("Got message appid=[%s] taskid=[%s] messageid=[%s] payload=[%s]",
				appid, taskid, messageid, payload));
	}

	public void onGotClientID(Context context, String clientid){
		Log.d(TAG, String.format("Got clientid appid=[%s]", clientid));
	}
}
