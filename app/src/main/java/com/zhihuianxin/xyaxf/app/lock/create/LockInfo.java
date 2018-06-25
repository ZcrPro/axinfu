package com.zhihuianxin.xyaxf.app.lock.create;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.zhihuianxin.xyaxf.DBFlowDataBase;


@Table(database = DBFlowDataBase.class) //上面自己创建的类（定义表的名称 版本）

public class LockInfo extends BaseModel {

    @PrimaryKey //主键
    public String regist_serial;
    @Column
    public String gesturePassword; //手势密码
    @Column
    public boolean gestureStatus; //手势密码是否打开
    @Column
    public boolean fingerStatus;  //指纹密码是否打开
    @Column
    public boolean laterStatus;  //稍后设置
    @Column
    public boolean remindStatus; //不再提醒
    @Column
    public int unionGestureEorTimes; //手势输入错误的次数
    @Column
    public boolean lockStatus; //是否已经解开了锁
    @Column
    public boolean hasBankCard; //当前用户是否已经绑卡
}
