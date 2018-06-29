package com.zhihuianxin.xyaxf.api;

import modellib.thrift.resource.UploadFileAccess;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by John on 2014/8/21.
 */
public class QiNiuStorateAPI {
	public static class QiNiuUploadException extends Exception {
		public QiNiuUploadException(String detailMessage) {
			super(detailMessage);
		}
	}
	final static int[] mUploadFileLock = {};

	public static String uploadFile(UploadFileAccess access,String file) throws QiNiuUploadException {
		String token = access.uptoken;
		String key = access.key;

		//do upload
		class UploadResult{
			boolean isSuccess = false;
			String error;
		}
		final UploadResult result = new UploadResult();

		Configuration config = new Configuration.Builder().build();
		UploadManager uploadManager = new UploadManager(config);
		uploadManager.put(new File(file), key, token, new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				result.isSuccess = info.isOK();
				if(!info.isOK()){
					result.error = info.error;
				}

				synchronized (mUploadFileLock) {
					mUploadFileLock.notify();
				}
			}
		}, null);

		synchronized (mUploadFileLock){
			try {
				mUploadFileLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(result.isSuccess){
			return access.access_url;
		}
		else{
			throw new QiNiuUploadException(result.error);
		}
	}
}
