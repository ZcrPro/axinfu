package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

/**
 * Created by Vincent on 2016/12/14.
 */

public class MeFeedBackShowPicActivity extends Activity implements LoadingDialog.IOnKeyDownBackDismiss{
    public static final String EXTRA_DATA = "extra_data";

    private LoadingDialog dialog;
    private String mUrl;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_showpic_activity);
        dialog = new LoadingDialog(this);
        LoadingDialog.setOnKeyDownBackDismissInterface(this);

        mImg = (ImageView) findViewById(R.id.image);
        mUrl = getIntent().getExtras().getString(EXTRA_DATA,"");

        findViewById(R.id.mainView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.show();
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(mUrl, config,new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(mUrl, view, loadedImage);
                mImg.setImageBitmap(loadedImage);
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onKeyDownDisMiss() {
        dialog.dismiss();
        finish();
    }
}
