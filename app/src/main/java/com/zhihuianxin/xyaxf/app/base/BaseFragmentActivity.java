package com.zhihuianxin.xyaxf.app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by zcrpro on 16/9/29.
 */

public abstract class BaseFragmentActivity extends FragmentActivity {

    //布局文件ID
    protected abstract int getContentViewId();

    //布局中Fragment的ID
    protected abstract int getFragmentContentId();

    //添加fragment
    protected void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

    }

    //移除fragment
    protected void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public void showKeyBoard(View view) {
        view.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) BaseFragmentActivity.this.getSystemService("input_method");//Context.INPUT_METHOD_SERVICE
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public String getViewName() {
        return "view name not set: " + getClass().getName();
    }

    public String[] getDismissViewArgs() {
        return null;
    }

    public String[] getShowViewArgs() {
        return null;
    }
}
