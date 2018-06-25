package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/1/4.
 */

public class MeSelectPhotoActivity extends BaseRealmActionBarActivity{
    @InjectView(R.id.pick_photo)
    View mPickPhotoView;
    @InjectView(R.id.camera_photo)
    View mCameraPhotoView;
    @InjectView(R.id.save_photo)
    View mSavePhotoView;
    @InjectView(R.id.current_photo)
    ImageView mCurrentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_select_photo_activity;
    }
}
