package com.zhihuianxin.xyaxf;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.axinfu.basetools.base.BaseFragment;

import io.realm.Realm;

/**
 * Created by zcrpro on 2016/11/10.
 */
public abstract class BaseRealmFragment extends BaseFragment {

    protected static Realm realm;

    @Override
    public void onStart() {
        super.onStart();
        if(getActivity() != null){
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null){
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(getActivity() != null){
            realm.close();
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if(!realm.isClosed()){
//            realm.close();
//        }
//    }
}
