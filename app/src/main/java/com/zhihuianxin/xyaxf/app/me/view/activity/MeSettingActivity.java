package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.app.Update;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.RealNameAuthStatus;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.lock.create.CreateFingerActivity;
import com.zhihuianxin.xyaxf.app.lock.create.CurbLockAdapter;
import com.zhihuianxin.xyaxf.app.lock.create.CurbLockDialog;
import com.zhihuianxin.xyaxf.app.lock.create.GestureActivity;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo_Table;
import com.zhihuianxin.xyaxf.app.lock.create.SetGestureActivity;
import com.zhihuianxin.xyaxf.app.lock.create.VerFingerActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginInputMobilActivityNew;
import com.zhihuianxin.xyaxf.app.me.contract.IMeCheckUpdateContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeCheckUpdatePresenter;
import com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionForgetPayPwdCodeActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSwepLittlePayInputPwdActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.DownloadAppDialog;
import com.zhihuianxin.xyaxf.app.view.DownloadAppForceDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.CustomerRealmProxy;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/11.
 */

public class MeSettingActivity extends BaseRealmActionBarActivity implements
        IMeCheckUpdateContract.IMeCheckUpdateView{
    @InjectView(R.id.lock_pwd_more)
    RelativeLayout lockPwdMore;
    @InjectView(R.id.gestureView)
    RelativeLayout gestureView;
    @InjectView(R.id.tv_version)
    TextView tvVersion;
    @InjectView(R.id.lock_text)
    TextView lockText;
    @InjectView(R.id.littleswitch)
    Switch littleswitch;
    @InjectView(R.id.littertxt)
    TextView litterTxt;
    @InjectView(R.id.forgetPwdId)
    View forgetView;
    @InjectView(R.id.open_lock)
    ImageView openLock;
    @InjectView(R.id.ll_mianmi)
    RelativeLayout llMianmi;
    @InjectView(R.id.cancelimgid)
    ImageView cancelimgid;

    private CheckUpdateController mCheckUpdateController;
    private IMeCheckUpdateContract.IMeCheckUpdatePresenter mPresenter;
    private DownloadAppForceDialog appUpdateForceDialog;
    private DownloadAppDialog appUpdateDialog;

    public static final String EXTRA_VER_GES_FROM_SETTING = "verGesFromSetting";// 设置页面打开关闭手势进入的标示(生活也会进入这个页面)
    public static final String BROADCAST_CANCELACCOUNT_CLOSE_MESETTING = "bc_cancelaccount_mesetting";

    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;

    @InjectView(R.id.logout)
    View logoutView;
    @InjectView(R.id.paySetting)
    View mPaySetting;
    @InjectView(R.id.update)
    View update;
    @InjectView(R.id.hasUpdateText)
    TextView mHasUpdateText;
    @InjectView(R.id.check_update_next)
    ImageView mCheckUpIcon;
    @InjectView(R.id.progress)
    ProgressBar mProgressBar;
    @InjectView(R.id.noUpdateText)
    TextView mNoUpdateText;
    @InjectView(R.id.payCancel)
    View cancelView;
    @InjectView(R.id.login_pwd)
    View mLoginPwdView;

    FingerprintManagerCompat manager;
    KeyguardManager mKeyManager;

    private List<String> lockTypes;
    private CurbLockDialog curbLockDialog;
    private PaymentConfig config;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();// 注销后关闭这个activity
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MeCheckUpdatePresenter(getActivity(), this);
        ButterKnife.inject(this);
        lockTypes = new ArrayList<>();
        initView();

        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        PackageInfo pi = Util.getPackageInfo(this);
        if (tvVersion != null && pi != null) {
            tvVersion.setText(String.format("v%s(%s)", pi.versionName, pi.versionCode));
        }


        gestureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lockText.getText().toString().equals("手势密码解锁")){
                    //如果是手势密码解锁
                    lockTypes.clear();
                    lockTypes.add("关闭应用锁");
                    if (isFinger())
                    lockTypes.add("切换为指纹识别解锁");
                    curbLockDialog = new CurbLockDialog(MeSettingActivity.this,lockTypes);
                    curbLockDialog.show();
                }else if (lockText.getText().toString().equals("指纹识别解锁")){
                    //如果当前是指纹解锁密码
                    lockTypes.clear();
                    lockTypes.add("关闭应用锁");
                    lockTypes.add("切换为手势密码解锁");
                    curbLockDialog = new CurbLockDialog(MeSettingActivity.this,lockTypes);
                    curbLockDialog.show();
                }else {
                    //没有打开解锁
                    lockTypes.clear();
                    if (isFinger())
                    lockTypes.add("指纹识别解锁");
                    lockTypes.add("手势密码解锁");
                    curbLockDialog = new CurbLockDialog(MeSettingActivity.this,lockTypes);
                    curbLockDialog.show();
                }

                curbLockDialog.setOnNextListener(new CurbLockAdapter.OnNextListener() {
                    @Override
                    public void onNext(String type) {
                        if (type.equals("指纹识别解锁")){
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                            Intent i = new Intent(MeSettingActivity.this, CreateFingerActivity.class);
                            i.putExtras(bundle);
                            startActivity(i);
                        }else if (type.equals("手势密码解锁")){
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                            Intent i = new Intent(MeSettingActivity.this, SetGestureActivity.class);
                            i.putExtras(bundle);
                            startActivity(i);
                        }else if (type.equals("切换为指纹识别解锁")){
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                            Intent i = new Intent(MeSettingActivity.this, CreateFingerActivity.class);
                            i.putExtras(bundle);
                            startActivity(i);
                        }else if(type.equals("切换为手势密码解锁")){
                            //验证指纹解锁
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                            bundle.putBoolean(VerFingerActivity.CHANGE, true);
                            Intent i = new Intent(MeSettingActivity.this, VerFingerActivity.class);
                            i.putExtras(bundle);
                            startActivity(i);
                        }else if (type.equals("关闭应用锁")){
                            final LockInfo lockInfo = SQLite.select()
                                    .from(LockInfo.class)
                                    .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                                    .querySingle();
                            assert lockInfo != null;
                            if (lockInfo.fingerStatus){
                                //验证指纹解锁
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                                Intent i = new Intent(MeSettingActivity.this, VerFingerActivity.class);
                                i.putExtras(bundle);
                                startActivity(i);
                            }else {
                                //验证手势解锁
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                                bundle.putBoolean(GestureActivity.SETTING, true);
                                Intent i = new Intent(MeSettingActivity.this, GestureActivity.class);
                                i.putExtras(bundle);
                                startActivity(i);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.JudgePayPwd();
        //初始化这个解锁方式
        LockInfo lockInfo = SQLite.select()
                .from(LockInfo.class)
                .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                .querySingle();// 区别与 queryList()

        if (lockInfo != null && lockInfo.gestureStatus) {
            lockText.setText("手势密码解锁");
        }else if (lockInfo != null && lockInfo.fingerStatus){
            lockText.setText("指纹识别解锁");
        }else {
            lockText.setText("关闭");
        }


    }

    private void initSwitchView(){
        litterTxt.setText("扫码支付，"+config.pin_free_amount+"元内免输支付密码");
        if(config.pin_free){
            littleswitch.setChecked(true);
        } else{
            littleswitch.setChecked(false);
        }
        littleswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isPressed())return;

                if(isChecked){// 关闭状态，需要打开 isChecked 是将要变为的状态
                    Intent intent = new Intent(MeSettingActivity.this,
                            UnionSwepLittlePayInputPwdActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(UnionSwepLittlePayInputPwdActivity.EXTRA_FREE_AMOUNT,config.pin_free_amount);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    App.no_add_card = true;
                    App.no_add_card_and_pay=false;
                } else{// 开启状态，需要关闭
                    setPinFree(false,config.pin_free_amount);
                    App.no_add_card = true;
                    App.no_add_card_and_pay=false;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        appUpdateDialog = new DownloadAppDialog(this);
        appUpdateForceDialog = new DownloadAppForceDialog(this);
        RealmResults<Customer> realmResults = realm.
                where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (((CustomerRealmProxy) realmResults.get(0)).realmGet$is_could_cancel()) {
            cancelView.setVisibility(View.VISIBLE);
        } else {
            cancelView.setVisibility(View.GONE);
        }
        mLoginPwdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MeMsgModifyPwdActivity.class));
            }
        });
        mCheckUpdateController = new CheckUpdateController();
        mCheckUpdateController.init();
        forgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.no_add_card = true;
                App.no_add_card_and_pay=false;
                mPresenter.getRealName();
            }
        });
        mHasUpdateText.setOnClickListener(updateListener);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.mAxLoginSp.setHasCheckUpdate(true);
                mPresenter.checkUpdate(null);
            }
        });
//        mPaySetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.JudgePayPwd();
//            }
//        });
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MeSettingActivity.this, MeCancelAccountActivity.class));
            }
        });

        IntentFilter filter = new IntentFilter(BROADCAST_CANCELACCOUNT_CLOSE_MESETTING);
        registerReceiver(receiver, filter);

        mPresenter.checkUpdate(null);
    }

    View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity() != null) {
                //App.mAxLoginSp.setUpdateType("");// 弹出更新，新apk安装结束后保证正常显示。
                showUpdateDialog();
            }
        }
    };

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.logoutDialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.logout_dialog, null);
        try {
            builder.setView(view).create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final AlertDialog alertDialog = builder.show();

        View out = view.findViewById(R.id.logout);
        View cancel = view.findViewById(R.id.logcancel);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MeFragment.logoutOperat();

                Intent intent = new Intent(MeFragment.BROADCAST_MEFRAGMENT_CLOSE);
                sendBroadcast(intent);

                startActivity(new Intent(getActivity(), LoginInputMobilActivityNew.class));

                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void checkUpdateSuccess(Update update, ArrayList<Update> plugin_updates) {
        if (update == null) {
            return;
        }
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("update",update);
        message.setData(bundle);
        handler.sendMessage(message);

        if (Util.isEmpty(update.update_type) || update.update_type.equals("None")) {
            mCheckUpdateController.showNoUpdateContent();
        } else {
            mCheckUpdateController.showHasUpdateContent();
        }

        if (App.mAxLoginSp.getUpdateType().equals("Required")) {// 强制更新
            showUpdateForceDialog();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Update update = (Update) msg.getData().getSerializable("update");
            App.mAxLoginSp.setVersionFromServer(Util.isEmpty(update.current_version) ? "" : update.current_version);
            App.mAxLoginSp.setUpdateType(Util.isEmpty(update.update_type) ? "" : update.update_type);
            App.mAxLoginSp.setUpdateUrl(Util.isEmpty(update.update_url) ? "" : update.update_url);
        }
    };

    private void showUpdateDialog(){
        if(App.mAxLoginSp.getAppApkDone()){
            appUpdateDialog.startDownloadService();
            appUpdateDialog.setStart();
        } else{
            appUpdateDialog.show();
        }
    }

    private void showUpdateForceDialog() {
        if(App.mAxLoginSp.getAppApkDone()){
            appUpdateForceDialog.startDownloadService();
        } else{
            appUpdateForceDialog.show();
        }
    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {
        if (config.has_pay_password) {
           llMianmi.setVisibility(View.VISIBLE);
           forgetView.setVisibility(View.VISIBLE);
        } else {
            llMianmi.setVisibility(View.GONE);
            forgetView.setVisibility(View.GONE);
        }
        this.config = config;
        initSwitchView();
    }

    @Override
    public void getRealNameResult(RealName realName) {
        if(realName.status.equals(RealNameAuthStatus.OK.name())){
            startActivity(new Intent(this, UnionForgetPayPwdCodeActivity.class));
        } else{
            Intent i = new Intent(this, UnionForgetPayPwdCodeActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(UnionForgetPayPwdCodeActivity.EXTRA_SHOWIMG,false);
            i.putExtras(b);
            startActivity(i);
        }
    }

    public void setPinFree(boolean pin_free,String pin_free_amount) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        map.put("pin_free",pin_free);
        map.put("pin_free_amount",pin_free_amount);
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.setPinFree(NetUtils.getRequestParams(MeSettingActivity.this,map),NetUtils.getSign(NetUtils.getRequestParams(MeSettingActivity.this,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(MeSettingActivity.this,true,null) {
                    @Override
                    public void onNext(Object o) {
                        littleswitch.setChecked(false);
                        litterTxt.setText("扫码支付，"+config.pin_free_amount+"元内免输支付密码");
                    }
                });
    }

    @Override
    public void setPresenter(IMeCheckUpdateContract.IMeCheckUpdatePresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void loadStart() {
        mCheckUpdateController.checking();
    }

    @Override
    public void loadError(String errorMsg) {
        mCheckUpdateController.stopChecking();
        mCheckUpdateController.init();
    }

    @Override
    public void loadComplete() {
        mCheckUpdateController.stopChecking();
        mCheckUpdateController.init();
    }

    private void updateDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.logoutDialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.update_app_force_dialog, null);
        builder.setView(view).create();
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.show();

        View update = view.findViewById(R.id.update);
        View exit = view.findViewById(R.id.exit);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    App.mAxLoginSp.setUpdateType("");// 弹出更新，新apk安装结束后保证正常显示。

                    if (Util.isHTTPUrl(url) || Util.isHTTPSUrl(url)) {
                        viewHttpUrl(url);
                    } else {
                        installLocalAPK(url);
                    }
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MeFragment.logoutOperat();

                Intent intent = new Intent(MeFragment.BROADCAST_MEFRAGMENT_CLOSE);
                sendBroadcast(intent);
                startActivity(new Intent(getActivity(), LoginInputMobilActivityNew.class));
                finish();
            }
        });
    }

    class CheckUpdateController {
        private void init() {
            if (hasChecked()) {
                showHasCheckedView();
            } else {
                showNoCheckedView();
            }
        }

        private boolean hasChecked() {
            return App.mAxLoginSp.getHasCheckUpdate();
        }

        private void checking() {
            mProgressBar.setVisibility(View.VISIBLE);
            mCheckUpIcon.setVisibility(View.GONE);
            mHasUpdateText.setVisibility(View.GONE);
            mNoUpdateText.setVisibility(View.GONE);
        }

        private void stopChecking() {
            mProgressBar.setVisibility(View.GONE);
        }

        // 显示没有更新时的UI
        private void showNoUpdateContent() {
            mCheckUpIcon.setVisibility(View.GONE);
            mNoUpdateText.setVisibility(View.VISIBLE);
            mHasUpdateText.setVisibility(View.GONE);
        }

        // 显示有更新时的UI
        private void showHasUpdateContent() {
            mCheckUpIcon.setVisibility(View.GONE);
            mHasUpdateText.setVisibility(View.VISIBLE);
            mNoUpdateText.setVisibility(View.GONE);
        }

        // 检查过更新
        private void showHasCheckedView() {
            if (Util.isEmpty(App.mAxLoginSp.getUpdateType()) || App.mAxLoginSp.getUpdateType().equals("None")) {
                showNoUpdateContent();
            } else {
                showHasUpdateContent();
            }
        }

        // 没有检查过更新
        private void showNoCheckedView() {
            mCheckUpIcon.setVisibility(View.VISIBLE);
        }
    }

    private void installLocalAPK(String path) {
        if (Util.isHTTPUrl(path) || Util.isHTTPSUrl(path)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "下载路径有误！", Toast.LENGTH_LONG).show();
        }
    }

    private void viewHttpUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_setting_activity;
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
