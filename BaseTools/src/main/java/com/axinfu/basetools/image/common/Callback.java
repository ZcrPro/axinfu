package com.axinfu.basetools.image.common;

import java.io.File;
import java.io.Serializable;

/**
 * Created by zcrpro on 2016/10/24.
 */
public interface Callback extends Serializable {

    void onSingleImageSelected(String path);

    void onImageSelected(String path);

    void onImageUnselected(String path);

    void onCameraShot(File imageFile);

    void onPreviewChanged(int select, int sum, boolean visible);
}
