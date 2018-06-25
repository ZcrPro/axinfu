package com.zhihuianxin.xyaxf.app.banner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.axinfu.modellib.thrift.message.Advertise;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/1/9.
 */

public class BannerDetailActivity extends BaseRealmActionBarActivity{
    public static final String EXTRA_DATA = "extra_data";

    @InjectView(R.id.banner_detail_title)
    TextView mTitle;
    @InjectView(R.id.banner_time)
    TextView mTime;
    @InjectView(R.id.banner_content)
    TextView mContent;
    @InjectView(R.id.banner_img)
    ImageView mBannerImg;

    private Advertise advertise;
    @Override
    protected int getContentViewId() {
        return R.layout.banner_detail_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        advertise = (Advertise) getIntent().getExtras().getSerializable(EXTRA_DATA);

        initViews();
    }

    private void initViews(){
        mTitle.setText(advertise.title);
        int[] timeItems = advertise.time != null? Util.getTimeItems(advertise.time):null;
        mTime.setText(String.format("%04d-%02d-%02d",timeItems[0], timeItems[1],timeItems[2]));
        try {
            JSONObject object = new JSONObject(advertise.action.args);
            mContent.setText(object.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(advertise.image, config,new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,  Bitmap loadedImage) {
                if(!Util.isEmpty(imageUri)){
                    mBannerImg.setBackground(null);
                }
                mBannerImg.setImageBitmap(loadedImage);
            }
        });
    }
}
