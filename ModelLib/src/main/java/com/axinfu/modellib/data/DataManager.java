package com.axinfu.modellib.data;

import io.realm.Realm;

/**
 * Created by zcrpro on 16/10/10.
 * 模拟数据核心
 */

public class DataManager {

    private Realm realm;

    /**
     * 模拟的数据直接存入数据库
     */
    public void initData() {
        realm = Realm.getDefaultInstance();
    }
}
