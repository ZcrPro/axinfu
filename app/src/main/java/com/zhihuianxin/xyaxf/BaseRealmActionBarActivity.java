package com.zhihuianxin.xyaxf;

import android.os.Bundle;
import android.view.View;

import com.axinfu.basetools.base.BaseActionBarActivity;

import io.realm.Realm;

/**
 * Created by zcrpro on 2016/11/10.
 */
public class BaseRealmActionBarActivity extends BaseActionBarActivity {

    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        App.addLoginActivities(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!realm.isClosed()){
            realm.close();
        }
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        super.onLeftButtonClick(view);
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }
}
