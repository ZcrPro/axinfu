package com.zhihuianxin.xyaxf.app.ocp;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.zhihuianxin.xyaxf.DBFlowDataBase;

@Table(database = DBFlowDataBase.class) //上面自己创建的类（定义表的名称 版本）

public class PayWayTagData extends BaseModel {

    @PrimaryKey
    public String cardId;
    @Column
    public String channel;
    @Column
    public String purpose;
    @Column
    public boolean isNews;
}