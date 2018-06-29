package com.zhihuianxin.xyaxf;

import android.app.Activity;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import modellib.data.ISession;
import modellib.thrift.fee.SubFeeItem;
import modellib.thrift.ocp.OcpAccount;
import com.cocosw.favor.FavorAdapter;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.app.axxyf.AxxyfActivity;
import com.zhihuianxin.xyaxf.app.data.IAXLogin;
import com.zhihuianxin.xyaxf.app.lock.create.CreateFingerActivity;
import com.zhihuianxin.xyaxf.app.lock.create.GestureActivity;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo_Table;
import com.zhihuianxin.xyaxf.app.lock.create.SetGestureActivity;
import com.zhihuianxin.xyaxf.app.lock.create.VerFingerActivity;
import com.zhihuianxin.xyaxf.app.ocp.PayWayTagData;
import com.zhihuianxin.xyaxf.app.payment.BankInfoCache;
import com.zhihuianxin.xyaxf.app.utils.ESUUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by zcrpro on 16/9/29.
 */

public class App extends Application {
    public static IAXLogin mAxLoginSp;
    public static ISession mSession;
    public static String hint;
    public static String serials;
    public static HashMap<String, SubFeeItem> subFeeDeductionHashMap;
    public static OcpAccount ocpAccount; //一码通账户相关
    public static AxxyfActivity.LoanAccountInfoRep loanAccountInfoRep;
    public static String WXAPPID; //一码通账户相关
    public static String ECARD_PASSWORD; //一码通账户相关
    private static List<Activity> activitys;
    private static List<Activity> activitysLogin;
    private static List<PayWayTagData> payWayTagData =new ArrayList<>();
    public static List<Protocol> protocols = new ArrayList<>();
    public static boolean hasBankCard;
    public static boolean add_card_back_home; //是否添加完银行卡直接回首页
    public static boolean isNeedCheck; //是否需要查询非正常状态切回
    public static boolean later; //是否息屏提示重新设置手势密码
    public static boolean no_add_card = false; //重置完密码直接关闭
    public static boolean no_add_card_and_pay = false; //重置完密码关闭并且去支付
    public static String formCasher; //从缴费还是从一卡通进入的收银台
    private FingerprintManagerCompat manager;
    private KeyguardManager mKeyManager;
    public static boolean splash = false;

    public static PayRequest payRequest;

    /**
     * 最大无需解锁时长 5分钟 单位：毫秒
     */
    public final static long MAX_UNLOCK_DURATION = 5 * 60 * 1000;
    public final static long UPDATE_TIME = 60 * 60 * 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        PushManager.getInstance().initialize(this,null);
        mAxLoginSp = new FavorAdapter.Builder(getApplicationContext()).build().create(IAXLogin.class);
        mSession = new FavorAdapter.Builder(getApplicationContext()).build().create(ISession.class);
        subFeeDeductionHashMap = new HashMap<>();
        Secure s = new Secure();
        s.setIsDebug(BuildConfig.AnXinDEBUG);
        s.initialize(this);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(configuration);
        ESUUID esuuid = new ESUUID();
        esuuid.load();
        BankInfoCache.initialize(this);
        //阿里云日志服务
        MANService manService = MANServiceProvider.getService();
        manService.getMANAnalytics().init(this, getApplicationContext());
        activitys = new ArrayList<>();
        activitysLogin = new ArrayList<>();
        registerActivityLifecycleCallbacks(new AppForeBackStatusCallback());
        //调试时候打开 上线关闭
        //manService.getMANAnalytics().turnOnDebug();
        //CrashUtil crashUtil = CrashUtil.getInstance();
        //crashUtil.init(this);


        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        //数据库初始化
        FlowManager.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void addActivities(Activity activity) {
        activitys.add(activity);
    }

    public static void finishAllActivity() {
        for (Activity activity : activitys) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void addLoginActivities(Activity activity) {
        activitysLogin.add(activity);
    }

    public static void finishAllLoginActivity() {
        for (Activity activity : activitysLogin) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public class AppForeBackStatusCallback implements ActivityLifecycleCallbacks {

        /**
         * 活动的Activity数量,为1时，APP处于前台状态，为0时，APP处于后台状态
         */
        private int activityCount = 0;

        /**
         * 最后一次可见的Activity
         * 用于比对Activity，这样可以排除启动应用时的这种特殊情况，
         * 如果启动应用时也需要锁屏等操作，请在启动页里进行操作。
         */
        private Activity lastVisibleActivity;


        @Override

        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            // 后台进程切换到前台进程,不包含启动应用时的这种特殊情况
            // 最后一次可见Activity是被唤醒的Activity && 活动的Activity数量为1
            if (lastVisibleActivity == activity && activityCount == 1) {
                //Background -> Foreground , do something
                startLockScreen(activity);
            }

            lastVisibleActivity = activity;
        }

        /**
         * 打开手势密码
         *
         * @param activity Activity
         */
        private void startLockScreen(Activity activity) {
            if (lockScreen(activity)) {
                //从后台切换到前台 到了锁屏时间
                final LockInfo lockInfo = SQLite.select()
                        .from(LockInfo.class)
                        .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                        .querySingle();
                if (lockInfo == null) return;

                if (lockInfo.hasBankCard) {
                    if (lockInfo.fingerStatus) {
                        //指纹解锁
                        if (isFinger()){
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                            Intent i = new Intent(getApplicationContext(), VerFingerActivity.class);
                            i.putExtras(bundle);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                            bundle.putBoolean(GestureActivity.CHANGE, true);
                            Intent i = new Intent(getApplicationContext(), VerFingerActivity.class);
                            i.putExtras(bundle);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    } else if (lockInfo.gestureStatus) {
                        //手势解锁
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                        Intent i = new Intent(getApplicationContext(), GestureActivity.class);
                        i.putExtras(bundle);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        //未设置解锁
                        if (!lockInfo.remindStatus) {
                            if (isFinger()) {
                                //设置指纹解锁
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                Intent i = new Intent(getApplicationContext(), CreateFingerActivity.class);
                                i.putExtras(bundle);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } else {
                                //设置手势解锁
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                Intent i = new Intent(getApplicationContext(), SetGestureActivity.class);
                                i.putExtras(bundle);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }
                    }
                } else {
                    //无卡用户
                    //如果是无卡用户 但是用户主动设置了解锁密码
                    if (lockInfo.fingerStatus) {
                        if (isFinger()){
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                            Intent i = new Intent(getApplicationContext(), VerFingerActivity.class);
                            i.putExtras(bundle);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                            bundle.putBoolean(GestureActivity.CHANGE, true);
                            Intent i = new Intent(getApplicationContext(), VerFingerActivity.class);
                            i.putExtras(bundle);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    } else if(lockInfo.gestureStatus){
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                        Intent i = new Intent(getApplicationContext(), GestureActivity.class);
                        i.putExtras(bundle);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }
            }
        }

        /**
         * 锁屏
         *
         * @param activity Activity
         * @return true 锁屏，反之不锁屏
         */
        private boolean lockScreen(Activity activity) {
            //解锁未超时，不锁屏
            if (!unlockTimeout())
                return false;
            // 当前Activity是锁屏Activity或登录Activity，不锁屏
            if (activity instanceof VerFingerActivity || activity instanceof GestureActivity || activity instanceof SetGestureActivity|| activity instanceof CreateFingerActivity)//|| activity instanceof LoginActivity
                return false;
            //不满足其它条件，不锁屏，#备用#
            if (!otherCondition()) {
                return false;
            }
            //锁屏
            return true;
        }

        /**
         * 由后台切到前台时，解锁时间超时
         *
         * @return 时间间隔大于解锁时长为true，反之为false
         */
        private boolean unlockTimeout() {
            //当前时间和上次离开应用时间间隔
            long dTime = System.currentTimeMillis() - Long.parseLong(mAxLoginSp.getLastTime());
            return dTime > MAX_UNLOCK_DURATION;
        }

        /**
         * 其它条件
         *
         * @return boolean
         */
        private boolean otherCondition() {
            return true;
        }


        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCount--;
            mAxLoginSp.setLastTime(System.currentTimeMillis() + "");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }


    public class Protocol {
        public String serial_no;            // 协议唯一标识
        public String protocol_no;            // 协议编号
        public String name;                    // 协议名称
        public String content;                // 协议内容
    }

    public boolean isFinger() {
        if (!manager.isHardwareDetected()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (!mKeyManager.isKeyguardSecure()) {
                return false;
            }
        }
        if (!manager.hasEnrolledFingerprints()) {
            return false;
        }
        return true;
    }
}
