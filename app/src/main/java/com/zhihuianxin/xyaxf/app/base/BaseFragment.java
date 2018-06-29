package com.zhihuianxin.xyaxf.app.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;

/**
 * Created by zcrpro on 16/9/29.
 */

public abstract class BaseFragment extends Fragment {

    protected BaseFragmentActivity mActivity;

    protected abstract void initView(View view, Bundle savedInstanceState);

    //获取布局文件ID
    protected abstract int getLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view, savedInstanceState);
        return view;
    }

    public String getViewName() {
        return "view name not set: " + getClass().getName();
    }

    public String[] getShowViewArgs() {
        return null;
    }

    public String[] getDismessViewArgs() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            MANService manService = MANServiceProvider.getService();
            manService.getMANPageHitHelper().pageAppear(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            MANService manService = MANServiceProvider.getService();
            manService.getMANPageHitHelper().pageDisAppear(getActivity());
        }
    }
}
