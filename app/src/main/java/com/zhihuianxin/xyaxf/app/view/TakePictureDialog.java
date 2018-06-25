package com.zhihuianxin.xyaxf.app.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by John on 2015/3/6.
 */
public class TakePictureDialog extends Dialog {
	public interface OnSelectedListener {
		void onFromCameraSelected();
		void onFromGallerySelected();
	}

	Context context;
	ImageView mCurrentPhoto;
	ImageView mBackImg;
	View mBtnFromCamera;
	View mBtnFromGallery;
	View mBtnSavePhoto;
	Bitmap mCurrentBitmap;

	private OnSelectedListener mOnSelectedListener;

	public TakePictureDialog(Context context) {
		super(context, R.style.BottomDialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.take_picture);

		initViews();
	}

	private void initViews(){
		mBtnSavePhoto = findViewById(R.id.save_photo);
		mBackImg = (ImageView) findViewById(R.id.input_back);
		mBtnFromCamera = findViewById(R.id.btn_from_camera);
		mBtnFromGallery = findViewById(R.id.btn_from_gallery);
		mCurrentPhoto = (ImageView) findViewById(R.id.current_photo);
		mBtnSavePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Util.isEmpty(App.mAxLoginSp.getAvatarUrl())){
					Toast.makeText(context,"您还未选择图片！",Toast.LENGTH_LONG).show();
					return;
				}
				if(mCurrentBitmap == null){
					Toast.makeText(context,"加载中请稍后！",Toast.LENGTH_LONG).show();
					return;
				}
				SavePhotoToLocalTask task = new SavePhotoToLocalTask();
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});
		mBackImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		mBtnFromCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnFromCameraClick(v);
			}
		});
		mBtnFromGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnFromGalleryClick(v);
			}
		});

		initCurrentPhoto();
	}

	class SavePhotoToLocalTask extends AsyncTask{
		LoadingDialog loadingDialog;

		public SavePhotoToLocalTask(){
			loadingDialog = new LoadingDialog(context);
			loadingDialog.show();
		}

		@Override
		protected Object doInBackground(Object[] params) {

			saveImageToGallery(context,mCurrentBitmap);
			return null;
		}

		@Override
		protected void onPostExecute(Object o) {
			loadingDialog.dismiss();
			Toast.makeText(context,"保存成功！",Toast.LENGTH_LONG).show();
		}
	}

	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(), "Axinfu");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".png";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
	}

	private void initCurrentPhoto(){
		final String url = App.mAxLoginSp.getAvatarUrl();
		DisplayImageOptions config = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).build();
		//ImageLoader.getInstance().init(configuration);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.loadImage(url, config,new SimpleImageLoadingListener(){

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if(!Util.isEmpty(url)){
					mCurrentPhoto.setBackground(null);
				}
				mCurrentBitmap = loadedImage;
				mCurrentPhoto.setImageBitmap(loadedImage);
			}
		});
	}

	private void onBtnFromGalleryClick(View v) {
		if(mOnSelectedListener != null){
			mOnSelectedListener.onFromGallerySelected();
		}

		dismiss();
	}

	private void onBtnFromCameraClick(View v) {
		if(mOnSelectedListener != null){
			mOnSelectedListener.onFromCameraSelected();
		}

		dismiss();
	}

	public void setOnSelectedListener(OnSelectedListener listener) {
		this.mOnSelectedListener = listener;
	}
}
