package com.zhihuianxin.xyaxf.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by zcrpro on 2016/12/14.
 */

public class PermissionUtil {
    public static String[] PERMISSION = {Manifest.permission.READ_PHONE_STATE};

    public static boolean isLacksOfPermission(Context context,String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(
                    context.getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED;
        }
        return false;
    }

}
