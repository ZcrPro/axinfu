package com.zhihuianxin.xyaxf.basetools.image.common;


import com.zhihuianxin.xyaxf.basetools.image.bean.Image;

/**
 * Created by zcrpro on 2016/10/24.
 */
public interface OnItemClickListener {

    int onCheckedClick(int position, Image image);

    void onImageClick(int position, Image image);
}
