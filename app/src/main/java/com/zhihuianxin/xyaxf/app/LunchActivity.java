package com.zhihuianxin.xyaxf.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.zhihuianxin.xyaxf.app.base.BaseActivity;
import modellib.service.MeService;
import modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/12/26.
 */

public class LunchActivity extends BaseActivity{
    public static final int MAX_WAIT_TIME = 7 * 1000;
    public static final int SHOW_PIC_TIME = 1 * 1000;

    @InjectView(R.id.lunch_img)
    ImageView mLunchImg;

    private boolean isAlreadyGo = false;

    public static class GetSplashImgResponse extends RealmObject {
        public BaseResponse resp;
        public String splash_image_url;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(!isAlreadyGo){
                isAlreadyGo = true;
                startActivity(new Intent(LunchActivity.this,SplashActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                handler.sendMessage(message);
            }
        }, MAX_WAIT_TIME);

        getSplashImg();
    }

    private void getSplashImg(){
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getSplashUrl(NetUtils.getRequestParams(this,map),NetUtils.getSign(NetUtils.getRequestParams(this,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this,false,null) {

                    @Override
                    public void onNext(Object o) {
                        GetSplashImgResponse response = new Gson().fromJson(o.toString(),GetSplashImgResponse.class);
                        loadImg(response.splash_image_url);// http://www.google.com
                    }

                    @Override
                    public void onError(Throwable e) {
                        goNext();
                    }
                });
    }

    private void loadImg(final String url){
        if(Util.isHTTPUrl(url) || Util.isHTTPSUrl(url)){
            DisplayImageOptions config = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
//            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
//                    .imageDownloader(new BaseImageDownloader(this, 3 * 1000, 6 * 1000)).build();
            //ImageLoader.getInstance().init(configuration);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(url, config,new SimpleImageLoadingListener(){

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    mLunchImg.setImageBitmap(loadedImage);
                    goNext();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    goNext();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    goNext();
                }
            });
        } else{
            goNext();
        }
    }

    private void goNext(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isAlreadyGo){
                    isAlreadyGo = true;
                    startActivity(new Intent(LunchActivity.this,SplashActivity.class));
                    finish();
                }
            }
        }, SHOW_PIC_TIME);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.lunch_activity;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

}
