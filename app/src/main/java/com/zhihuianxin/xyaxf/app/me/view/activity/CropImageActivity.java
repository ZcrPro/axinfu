package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.axinfu.basetools.base.BaseActionBarActivity;
import com.edmodo.cropper.CropImageView;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by John on 2014/8/12.
 */
public class CropImageActivity extends BaseActionBarActivity {
	public static final String TAG = "CropImageActivity";

	public static final int RESULT_CAN_NOT_CREATE_OUTPUT_FILE = 5001;
	public static final int RESULT_CAN_NOT_LOAD_IMAGE_URI = 5002;
	public static final String EXTRA_ASPECT_RATIO = "aspect_ratio";
	public static final String EXTRA_OUTPUT_IMAGE_PATH = "output_image_path";
	public static final String EXTRA_OUTPUT_IMAGE_QUALITY = "output_image_quality";
	public static final String EXTRA_ERROR = "error";

	private CropImageView mCropImageView;
	private float mAspectRatio = 1.0f;
	private Uri mImageUri;
	private boolean mFixAspectRatio = false;
	private String mOutputPath;
	private File mOutputFile;
	private int mQuality = 95;

	private int[] mRequiredSize;
	private int mRequiredMaxLength = -1;

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

	@Override
	protected int getContentViewId() {
		return R.layout.crop_image;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUri = getIntent().getData();

		if(getIntent().hasExtra(EXTRA_REQUIRED_SIZE)){
			mRequiredSize = getIntent().getIntArrayExtra(EXTRA_REQUIRED_SIZE);

			if(mRequiredSize != null && mRequiredSize.length == 2){
				mFixAspectRatio = true;
				mAspectRatio = (float) mRequiredSize[0] / (float) mRequiredSize[1];
			}
		}

		if(getIntent().hasExtra(EXTRA_REQUIRED_MAX_LENGTH)){
			mRequiredMaxLength = getIntent().getIntExtra(EXTRA_REQUIRED_MAX_LENGTH, -1);
		}

		if(!mFixAspectRatio && getIntent().hasExtra(EXTRA_ASPECT_RATIO)){
			mFixAspectRatio = true;
			mAspectRatio = getIntent().getFloatExtra(EXTRA_ASPECT_RATIO, mAspectRatio);
		}


		mOutputPath = getIntent().getStringExtra(EXTRA_OUTPUT_IMAGE_PATH);
		mQuality = getIntent().getIntExtra(EXTRA_OUTPUT_IMAGE_QUALITY, mQuality);


		try {
			mOutputFile = createOutputFile();
		} catch (IOException e) {
			Intent data = new Intent();
			data.putExtra(EXTRA_ERROR, e);
			data.putExtra(EXTRA_OUTPUT_IMAGE_PATH, mOutputPath);
			setResult(RESULT_CAN_NOT_CREATE_OUTPUT_FILE, data);
			finish();
			return;
		}

		mCropImageView = (CropImageView) findViewById(R.id.crop_image);
		mCropImageView.setFixedAspectRatio(mFixAspectRatio);
		mCropImageView.setAspectRatio(Math.round(mAspectRatio * 100.0f), 100);

		Bitmap bmp;
		try {
			bmp = loadBitmap(mImageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Intent data = new Intent();
			data.setData(mImageUri);
			data.putExtra(EXTRA_ERROR, e);
			setResult(RESULT_CAN_NOT_LOAD_IMAGE_URI, data);
			finish();
			return;
		}
		mCropImageView.setImageBitmap(bmp);
	}

	private Bitmap loadBitmap(Uri uri) throws FileNotFoundException {
		InputStream is = getContentResolver().openInputStream(uri);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;

		Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);

		return bmp;
	}

	public void onBtnNextStepClick(View view){
		Bitmap bmp = mCropImageView.getCroppedImage();

		if(mRequiredMaxLength > 0 && (bmp.getWidth() > mRequiredMaxLength || bmp.getHeight() > mRequiredMaxLength)){
			int requiredWidth = bmp.getWidth() > bmp.getHeight()? mRequiredMaxLength: (mRequiredMaxLength * bmp.getHeight() / bmp.getWidth());
			int requiredHeight = bmp.getWidth() > bmp.getHeight()? (mRequiredMaxLength * bmp.getWidth() / bmp.getHeight()): mRequiredMaxLength;

			mRequiredSize = new int[]{requiredWidth, requiredHeight};
		}

		if(mRequiredSize != null && mRequiredSize[0] > 0 && mRequiredSize[1] > 0){
			Bitmap newBmp = Bitmap.createScaledBitmap(bmp, mRequiredSize[0], mRequiredSize[1], true);

			if(bmp != newBmp){
				bmp.recycle();
			}

			bmp = newBmp;
		}

		try {
			OutputStream os = new FileOutputStream(mOutputFile);
			bmp.compress(Bitmap.CompressFormat.JPEG, mQuality, os);

			Intent data = new Intent();
			data.putExtra(EXTRA_OUTPUT_IMAGE_PATH, mOutputFile.getAbsolutePath());
			setResult(RESULT_OK, data);
			finish();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private File createOutputFile() throws IOException {
		File outputFile;
		if(Util.isEmpty(mOutputPath)){
			outputFile = Util.createTempFile(this, "jpg");
		}
		else{
			outputFile = new File(mOutputPath);
			outputFile.getParentFile().mkdirs();
			if(!outputFile.exists()){
				outputFile.createNewFile();
			}
		}

		return outputFile;
	}

	@Override
	public boolean rightButtonEnabled() {
		return false;
	}
}
