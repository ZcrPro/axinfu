package com.axinfu.basetools.image;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by zcrpro on 2016/10/24.
 */
public interface ImageLoader extends Serializable {
    void displayImage(Context context, String path, ImageView imageView);
}