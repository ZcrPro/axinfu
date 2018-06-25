package com.zhihuianxin.xyaxf.app.data;

import com.cocosw.favor.AllFavor;
import com.cocosw.favor.Default;

/**
 * Created by Vincent on 2016/10/9.
 */
@AllFavor
public interface IAXLogin {
    // 登录成功标识
    @Default("false")
    boolean getLoginSign();
    void setLoginSign(boolean sign);

    // 登录用户手机号
    @Default("")
    String getUserMobil();
    void setUserMobil(String mobil);

    // 登录用户的唯一标识
    @Default("")
    String getRegistSerial();
    void setRegistSerial(String regist_serial);

    // 登录用户验证码
    @Default("")
    String getVerCode();
    void setVerCode(String verCode);

    // 登录用户验证码
    @Default("")
    String getUserStatue();
    void setUserStatue(String statue);

    // UUID
    @Default("")
    String getUUID();
    void setUUID(String uuid);

    // 头像url
    @Default("")
    String getAvatarUrl();
    void setAvatarUrl(String url);

    // 个推 client_id
    @Default("")
    String getGetuiClientId();
    void setGetuiClientId(String client_id);

    // 是否检测过更新
    @Default("")
    boolean getHasCheckUpdate();
    void setHasCheckUpdate(boolean flag);

    // 保存刚选择了的学校
    @Default("")
    String getSelectSchoolCode();
    void setSelectSchoolCode(String flag);

    // 保存从服务器获取的最新版本号
    @Default("")
    String getVersionFromServer();
    void setVersionFromServer(String version);

    // 保存从服务器获取的最新版本号
    @Default("")
    String getUpdateType();
    void setUpdateType(String type);

    // 保存从服务器获取的最新url
    @Default("")
    String getUpdateUrl();
    void setUpdateUrl(String urlu);

    // 是否点击过推送消息
    @Default("true")
    boolean getHasClickGetui();
    void setHasClickGetui(boolean flag);

    // 是否点击过个推的feedback内容
    @Default("true")
    boolean getHasClickGetuiFeedback();
    void setHasClickGetuiFeedback(boolean flag);

    // 是否显示过首页加载动画
    @Default("false")
    boolean getHadShowSplash();
    void setHadShowSplash(boolean flag);

    // 是否输入过other number
    @Default("")
    String getOtherFeeNo();
    void setOtherFeeNo(String flag);

    // 保存支付密码，确认的时候
    @Default("")
    String getUnionPayPwd();
    void setUnionPayPwd(String pwd);

    // 保存银联二维码备注信息
    @Default("")
    String getUnionReMark();
    void setUnionReMark(String pwd);

    // 保存银联二维选择银行卡的id
    @Default("")
    String getUnionSelBankId();
    void setUnionSelBankId(String pwd);

    // 保存银联二维的扫码连接
    @Default("")
    String getUnionQrCode();
    void setUnionQrCode(String qrcode);

    // 保存银联二维是否需要重新获取订单和优惠 // 1 需要 true ；0 不需要 false
    @Default("0")
    String getReGetUPQR();
    void setReGetUPQR(String str);


    // 银联二维码手势
    @Default("")
    String getUnionGesture();
    void setUnionGesture(String str);

    // 银联二维码手势是否打开;0 is close;i is open.
    @Default("0")
    String getUnionGestureOpenStatus();
    void setUnionGestureOpenStatus(String str);


    // 银联二维码指纹是否打开;0 is close;i is open.
    @Default("0")
    String getUnionFingerOpenStatus();
    void setUnionFingerOpenStatus(String str);


    // 银联二维码被扫前的输入手势次数 ;
    @Default("0")
    String getUnionGestureEorTimes();
    void setUnionGestureEorTimes(String str);

    // 银联二维码被扫前的输入手势输错5次 ;0 is less 5;1 is more equal 5;
    @Default("0")
    String getUnionGesture5EorTimes();
    void setUnionGesture5EorTimes(String str);


    // 是否稍后提醒
    @Default("false")
    boolean getLockShaohou();
    void setLockShaohou(boolean flag);

    // 是否不再提醒
    @Default("false")
    boolean getLockbuzai();
    void setLockbuzai(boolean flag);


    //最后离开时间
    @Default("")
    String getLastTime();
    void setLastTime(String time);


    //默认需要重新解锁
    @Default("true")
    boolean getLcokFalse();
    void setLcokFalse(boolean status);

    //是否是用户主动关闭的解锁
    @Default("false")
    boolean getUserLcok();
    void setUserLcok(boolean status);


    //一卡通密码
    @Default("")
    String getEcardPassword();
    void setEcardPassword(String password);

    //一卡通密码
    @Default("")
    String getLadmarkName();
    void setLadmarkName(String name);

    //GYSDK 成功下载
    @Default("false")
    boolean getGysdkDone();
    void setGysdkDone(boolean flag);

    //APP APK 成功下载
    @Default("false")
    boolean getAppApkDone();
    void setAppApkDone(boolean flag);

    //GYSDK APK url
    @Default("")
    String getGysdkUrl();
    void setGysdkUrl(String url);
}
