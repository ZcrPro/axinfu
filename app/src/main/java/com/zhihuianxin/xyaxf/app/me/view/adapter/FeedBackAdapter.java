package com.zhihuianxin.xyaxf.app.me.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import modellib.thrift.app.Appendix;
import modellib.thrift.base.Feedback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeFeedBackShowPicActivity;

import java.util.HashMap;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/11/10.
 */

public class FeedBackAdapter extends ArrayAdapter<Feedback>{
    private Context mContext;
    private int mAbbreviationSize = 0;
    private DisplayMetrics metrics;
    private HashMap<String,View> mItemViewMap;

    public FeedBackAdapter(Context context,int imgSize) {
        super(context, 0);
        mContext = context;
        mItemViewMap = new HashMap<>();
        mAbbreviationSize = imgSize;
        metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if(convertView != null){
//            view = convertView;
//        }
//        else{
//            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.me_service_feedback_item, parent, false);
//        }
        View view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.me_service_feedback_item, parent, false);

        ((TextView)view.findViewById(R.id.question)).setText(getItem(position).question);
        int[] timeItems = getItem(position).date != null? Util.getTimeItems(getItem(position).date):null;
        ((TextView)view.findViewById(R.id.time_title)).setText(timeItems!=null?String.format("%04d-%02d-%02d", timeItems[0], timeItems[1],timeItems[2]):"");

        if(getItem(position).appendices != null && getItem(position).appendices.size() != 0){
            showAppendix(view,getItem(position).appendices);
        }
        else {
            view.findViewById(R.id.photo_list).setVisibility(View.GONE);
        }

        showAnswer(view,getItem(position).answer);
        return view;
    }

    private void showAnswer(View view,String answer){
        if(Util.isEmpty(answer)){
            view.findViewById(R.id.default_answer_view).setVisibility(View.VISIBLE);
            view.findViewById(R.id.answer).setVisibility(View.GONE);
        } else{
            view.findViewById(R.id.default_answer_view).setVisibility(View.GONE);
            view.findViewById(R.id.answer).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.answer)).setText(answer);
        }
    }

    private LinearLayout mAppdenixListView;
    private void showAppendix(View view,RealmList<Appendix> realmList){
        mAppdenixListView = (LinearLayout) view.findViewById(R.id.photo_list);
        mAppdenixListView.setVisibility(View.VISIBLE);
        mAppdenixListView.removeAllViews();

        for(final Appendix appendix : realmList){
            final ImageView img = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mAbbreviationSize,mAbbreviationSize);
            params.setMargins((int) (metrics.density*10),0,0,(int) (metrics.density*10));
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mAppdenixListView.addView(img);
            img.setBackgroundResource(R.drawable.defaulf9_sq);
            DisplayImageOptions config = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext).build();
            ImageLoader.getInstance().init(configuration);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(appendix.url, config,new SimpleImageLoadingListener(){

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    super.onLoadingComplete(appendix.url, view, loadedImage);
                    img.setBackground(null);
                    img.setImageBitmap(loadedImage);
                }

            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MeFeedBackShowPicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(MeFeedBackShowPicActivity.EXTRA_DATA,appendix.url);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
