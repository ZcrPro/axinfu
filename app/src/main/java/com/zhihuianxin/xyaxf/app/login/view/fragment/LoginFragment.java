package com.zhihuianxin.xyaxf.app.login.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginInputMobilActivityNew;

/**
 * Created by Vincent on 2016/10/11.
 */

public class LoginFragment extends BaseRealmFragment {
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(App.mAxLoginSp.getLoginSign()){
            alreadyLogin();
        } else{
            inputLocalMobil();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void inputLocalMobil() {
        if(getActivity() != null){
            startActivity(new Intent(getActivity(),LoginInputMobilActivityNew.class));
            getActivity().finish();
        }
    }
    public void alreadyLogin() {
        if(getActivity() != null){
//            RealmResults<Customer> realmResults = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
//            if(realmResults.size()==0 || ((CustomerRealmProxy)realmResults.get(0)).realmGet$school()==null){//没有选择学校去选学校
//                startActivity(new Intent(getActivity(),LoginInputMobilActivity.class));
//            } else {
//                startActivity(new Intent(getActivity(),MainActivity.class));
//            }
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }
}
