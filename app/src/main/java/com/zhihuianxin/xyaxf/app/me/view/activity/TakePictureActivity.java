package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.app.view.TakePictureDialog;
import com.zhihuianxin.xyaxf.app.view.TakePictureDialogSimple;
import com.zhihuianxin.xyaxf.basetools.image.ImageLoader;
import com.zhihuianxin.xyaxf.basetools.image.ImgSelActivity;
import com.zhihuianxin.xyaxf.basetools.image.ImgSelConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



/**
 * Created by John on 2014/8/14.
 */
public class TakePictureActivity extends Activity {
	public static final String EXTRA_FROM_SIMPLE = "extra_from";
	public static final String EXTRA_MAX_MUTI_SELECT = "max_muti_select";
	public static final String TAG = "TakePictureActivity";
	public static final int REQUEST_CAPTURE_PHOTO = 4002;
	public static final int REQUEST_PICK_PHOTO = 4003;
	public static final int REQUEST_CROP_IMAGE = 4004;

	public static final String EXTRA_OUTPUT_PATH = "output_path";
	public static final String EXTRA_OUTPUT_PATH_MUTI = "output_path_muti";
	public static final String EXTRA_ASPECT_RATIO = CropImageActivity.EXTRA_ASPECT_RATIO;

	/**
	 * set required width and size
	 * value format: string, W(int)xH(int)
	 * if this value is set, EXTRA_ASPECT_RATIO will be ignored
	 */
	public static final String EXTRA_REQUIRED_SIZE = "required_size";

	/**
	 * set required max border length
	 * value format: int
	 * if this value set, EXTRA_REQUIRED_SIZE will be ignored
	 */
	public static final String EXTRA_REQUIRED_MAX_LENGTH = "max_length";

	public static final String EXTRA_FROM = "select_from";// 是选择头像还是选择反馈内容
	public static final String EXTRA_FROM_AVATAR = "from_avatar";

	private Uri mPhotoUri;
	private String mOutputPath;
	private float mAspectRatio = 1.0f;
	private boolean mAspectRatioEnabled = false;

	private int[] mRequiredSize;
	private int mRequiredMaxLength = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mOutputPath = getIntent().getStringExtra(EXTRA_OUTPUT_PATH);

		if(getIntent().hasExtra(EXTRA_REQUIRED_SIZE)){
			mRequiredSize = getIntent().getIntArrayExtra(EXTRA_REQUIRED_SIZE);
		}

		if(getIntent().hasExtra(EXTRA_REQUIRED_MAX_LENGTH)){
			mRequiredMaxLength = getIntent().getIntExtra(EXTRA_REQUIRED_MAX_LENGTH, -1);
		}

		if(!mAspectRatioEnabled && getIntent().hasExtra(EXTRA_ASPECT_RATIO)){
			mAspectRatioEnabled = true;
			mAspectRatio = getIntent().getFloatExtra(EXTRA_ASPECT_RATIO, 1.0f);
		}

		if(getIntent().getExtras() != null && !Util.isEmpty(getIntent().getExtras().getString(EXTRA_FROM_SIMPLE))){
			TakePictureDialogSimple dlgSimple = new TakePictureDialogSimple(this);
			dlgSimple.setOnSelectedListener(new TakePictureDialogSimple.OnSelectedListener() {
				@Override
				public void onFromCameraSelected() {
					takePhoto();
				}

				@Override
				public void onFromGallerySelected() {
					pickPicture();
				}
			});
			dlgSimple.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, 300);
				}
			});
			dlgSimple.show();
		} else{
			TakePictureDialog dlg = new TakePictureDialog(this);
			dlg.setOnSelectedListener(new TakePictureDialog.OnSelectedListener() {
				@Override
				public void onFromCameraSelected() {
					takePhoto();
				}

				@Override
				public void onFromGallerySelected() {
					pickPicture();
				}
			});
			dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, 300);
				}
			});
			dlg.show();
		}
	}

	private void takePhoto(){
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(new Date()) + ".jpg";
		File out = getExternalCacheDir();
		if (!out.exists()) {
			out.mkdirs();
		}

		out = new File(out, fileName);
		mPhotoUri = Uri.fromFile(out);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.8);
		startActivityForResult(imageCaptureIntent, REQUEST_CAPTURE_PHOTO);
	}

	private ImageLoader loader = new ImageLoader() {
		@Override
		public void displayImage(Context context, String path, ImageView imageView) {
			Glide.with(context).load(path).into(imageView);
		}
	};

	private void pickPicture(){
//		Intent intent = new Intent(Intent.ACTION_PICK);
//		intent.setType("image/*");
//		startActivityForResult(intent, REQUEST_PICK_PHOTO);
		ImgSelConfig config = new ImgSelConfig.Builder(loader).multiSelect(true)
				// 使用沉浸式状态栏
				.maxNum(getMutiGetPicCount())
				.statusBarColor(Color.parseColor("#3F51B5")).build();

		ImgSelActivity.startActivity(this, config, REQUEST_PICK_PHOTO);
	}

	private int getMutiGetPicCount(){
		if(getIntent().getStringExtra(EXTRA_FROM) != null && getIntent().getStringExtra(EXTRA_FROM).equals(EXTRA_FROM_AVATAR)){
			return 1;
		} else{
			return getIntent().getExtras().getInt(EXTRA_MAX_MUTI_SELECT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_CANCELED){
			finish();
			return;
		}

		if(requestCode == REQUEST_CAPTURE_PHOTO){
			if(resultCode == RESULT_OK){
				handlePicture(mPhotoUri);// 拍照返回再去剪裁
			}
		}
		else if(requestCode == REQUEST_PICK_PHOTO){// 选择照片返回
			if(resultCode == RESULT_OK){
				//handlePicture(data.getData());
				mutiSelPictureHandler(data);
			}
		}
		else if(requestCode == REQUEST_CROP_IMAGE){// 拍照后剪裁返回
			if(resultCode == RESULT_OK){// 拍照结果的返回
				String cropResult = data.getStringExtra(CropImageActivity.EXTRA_OUTPUT_IMAGE_PATH);
				Intent result = new Intent();
				result.putExtra(EXTRA_OUTPUT_PATH, cropResult);
				setResult(RESULT_OK, result);
				finish();
			}
			else if(resultCode == CropImageActivity.RESULT_CAN_NOT_LOAD_IMAGE_URI || resultCode == CropImageActivity.RESULT_CAN_NOT_CREATE_OUTPUT_FILE){
				Log.w("TakePictureActivity", "can not load img: " + data.getSerializableExtra(CropImageActivity.EXTRA_ERROR));
			}
		}
	}

	private void mutiSelPictureHandler(Intent data){
		// 多选结果的返回
		ArrayList<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
		Intent result = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_OUTPUT_PATH_MUTI, pathList);
		result.putExtras(bundle);
		setResult(RESULT_OK, result);
		finish();
	}

	private void handlePicture(Uri uri){
		Intent intent = new Intent(this, CropImageActivity.class);
		intent.setData(uri);
		if(Util.isEnabled(mOutputPath)){
			intent.putExtra(CropImageActivity.EXTRA_OUTPUT_IMAGE_PATH, mOutputPath);
		}
		if(mAspectRatioEnabled){
			intent.putExtra(CropImageActivity.EXTRA_ASPECT_RATIO, mAspectRatio);
		}

		if(mRequiredSize != null){
			intent.putExtra(CropImageActivity.EXTRA_REQUIRED_SIZE, mRequiredSize);
		}

		if(mRequiredMaxLength > 0){
			intent.putExtra(CropImageActivity.EXTRA_REQUIRED_MAX_LENGTH, mRequiredMaxLength);
		}

		startActivityForResult(intent, REQUEST_CROP_IMAGE);
	}
}
