package com.zhihuianxin.xyaxf;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.service.UPQRService;
import com.axinfu.modellib.thrift.app.Update;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.message.ImportantMessage;
import com.axinfu.modellib.thrift.message.ImportantMessageWithUser;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.home.HomeFragment;
import com.zhihuianxin.xyaxf.app.home.qrcode.QRCodeActivity;
import com.zhihuianxin.xyaxf.app.life.view.activity.LifeFragment;
import com.zhihuianxin.xyaxf.app.lock.create.CreateFingerActivity;
import com.zhihuianxin.xyaxf.app.lock.create.GestureActivity;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo_Table;
import com.zhihuianxin.xyaxf.app.lock.create.SetGestureActivity;
import com.zhihuianxin.xyaxf.app.lock.create.VerFingerActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginInputMobilActivityNew;
import com.zhihuianxin.xyaxf.app.main.IMainContract;
import com.zhihuianxin.xyaxf.app.main.MainPresenter;
import com.zhihuianxin.xyaxf.app.me.presenter.MeCheckUpdatePresenter;
import com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.DownloadAppForceDialog;
import com.zhihuianxin.xyaxf.app.view.ImportantNoticeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.ImportantMessageRealmProxy;
import io.realm.ImportantMessageWithUserRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseRealmActionBarActivity implements View.OnClickListener, IMainContract.IMainView {
    public static final String EXTRA_SWITCH_TO_NOTICE = "switch_to_notice";
    public static final String NOTICE_BROADCAST = "notice_broadcast";
    public static final String RELOGIN_BROADCAST = "relogin_broadcast";

    @InjectView(R.id.layout_code)
    RelativeLayout layoutCode;
    @InjectView(R.id.ll_axinfu)
    LinearLayout llAxinfu;
    @InjectView(R.id.ll_life)
    LinearLayout llLife;

    private long exitTime = 0;

    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.img_home)
    ImageView imgHome;
    @InjectView(R.id.tv_home)
    TextView tvHome;
    @InjectView(R.id.layout_home)
    RelativeLayout layoutHome;
    @InjectView(R.id.img_life)
    ImageView imgLife;
    @InjectView(R.id.tv_life)
    TextView tvLife;
    @InjectView(R.id.layout_life)
    RelativeLayout layoutLife;
    @InjectView(R.id.activity_main)
    LinearLayout activityMain;
    @InjectView(R.id.msg_point)
    ImageView mMsgPointImg;
    @InjectView(R.id.swipeView)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int selectColor;
    private int unSelectColor;
    private Fragment[] fragments;
    private int currentIndex;
    private int index;
    private IMainContract.IMainPresenter presenter;
    private boolean mHadGetLogoutNotice = false;
    private DownloadAppForceDialog appUpdateForceDialog;

    FingerprintManagerCompat manager;
    FingerprintManagerCompat fingerprintManagerCompat;
    KeyguardManager mKeyManager;

    BroadcastReceiver MainActivityReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NOTICE_BROADCAST)) {
                checkingMsg();
            } else if (intent.getAction().equals(RELOGIN_BROADCAST)) {
                if (!mHadGetLogoutNotice) {
                    Toast.makeText(MainActivity.this, "检测到您的账号已经在其他设备登录，需重新登录！", Toast.LENGTH_LONG).show();
                    mHadGetLogoutNotice = true;
                    logout();
                }
            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            setTabs(0);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        findViewById(R.id.action_bar).setVisibility(View.GONE);
        initDatas();

        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        //直接查询是否已经绑卡
        loadingCard();

        if (updateTimeout()) {
            checkUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkingUpdate();
        checkingMsg();
        //checkingImportantMsg();
        App.payRequest = null;
        App.ECARD_PASSWORD = null;

    }

    private void initDatas() {
        new MainPresenter(this, this);
        appUpdateForceDialog = new DownloadAppForceDialog(this);
        if (!Util.isEmpty(App.mAxLoginSp.getGetuiClientId()) && App.mAxLoginSp.getLoginSign()) {
            presenter.updateGeTuiId(App.mAxLoginSp.getGetuiClientId());
        }

        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);

        layoutHome.setOnClickListener(this);
        layoutLife.setOnClickListener(this);
        layoutCode.setOnClickListener(this);

        selectColor = getResources().getColor(R.color.axf_text_content_gray);
        unSelectColor = getResources().getColor(R.color.axf_light_gray);
        fragments = new Fragment[3];
        fragments[0] = HomeFragment.newInstance("home");
        fragments[1] = LifeFragment.newInstance("life");
        fragments[2] = MeFragment.newInstance("mine");
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragments[0]).commitAllowingStateLoss();

        IntentFilter filter = new IntentFilter(NOTICE_BROADCAST);
        IntentFilter filterRelogin = new IntentFilter(RELOGIN_BROADCAST);

        registerReceiver(MainActivityReceive, filter);
        registerReceiver(MainActivityReceive, filterRelogin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_home:
                index = 0;
                setTabs(index);
                llAxinfu.setVisibility(View.VISIBLE);
                llLife.setVisibility(View.GONE);
                break;

            case R.id.layout_life:
                index = 1;
                setTabs(index);
                llAxinfu.setVisibility(View.GONE);
                llLife.setVisibility(View.VISIBLE);
                break;

            case R.id.layout_code:
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
//                acquiring("1234");
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("payinfo", null);
//                bundle.putString("qrid", null);
//                bundle.putBoolean(OcpPayFixedDeskActivity.FIXED, false);
//                Intent intent = new Intent(MainActivity.this, OcpPayFixedDeskActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    public void setTabs(int pos) {
        resetColor();
        switch (pos) {
            case 0:
                imgHome.setImageResource(R.drawable.axinfu_icon_nor);
                tvHome.setTextColor(selectColor);
                break;
            case 1:
                imgLife.setImageResource(R.drawable.life_icon_nor);
                tvLife.setTextColor(selectColor);
                break;
        }

        if (currentIndex != index || mSwipeRefreshLayout.isRefreshing()) {
            FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            if (mSwipeRefreshLayout.isRefreshing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ft.detach(fragments[index]).attach(fragments[index]).commit();
                    }
                }, 150);
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                ft.hide(fragments[currentIndex]);
                if (!fragments[index].isAdded()) {
                    ft.add(R.id.container, fragments[index]);
                }
                ft.show(fragments[index]).commitAllowingStateLoss();
            }
        }
        currentIndex = index;
        if (currentIndex == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
        if (pos == 1) {
            Intent intentLife = new Intent(LifeFragment.BROADCAST_MEFRAGMENT_UPDATE);
            sendBroadcast(intentLife);
        }
    }

    /**
     * 由后台切到前台时，需要检测更新
     *
     * @return 时间间隔大于解锁时长为true，反之为false
     */
    public boolean updateTimeout() {
        //当前时间和上次离开应用时间间隔
        if (App.mAxLoginSp.getLastTime() != null || !TextUtils.isEmpty(App.mAxLoginSp.getLastTime())) {
            long dTime = System.currentTimeMillis() - Long.parseLong(App.mAxLoginSp.getLastTime());
            return dTime > App.UPDATE_TIME;
        }
        return false;
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getCheckUpdate(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, false, null) {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                        MeCheckUpdatePresenter.GetCheckUpdateResponse response = new Gson().fromJson(o.toString(), MeCheckUpdatePresenter.GetCheckUpdateResponse.class);
                        Update update = response.app_update;

                        if (update == null) {
                            return;
                        }
                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("update", update);
                        message.setData(bundle);
                        handlerUpdate.sendMessage(message);

                        if (App.mAxLoginSp.getUpdateType().equals("Required")) {// 强制更新,非强制不处理
                            showUpdateForceDialog();
                        }
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    Handler handlerUpdate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Update update = (Update) msg.getData().getSerializable("update");
                App.mAxLoginSp.setVersionFromServer(Util.isEmpty(update.current_version) ? "" : update.current_version);
                App.mAxLoginSp.setUpdateType(Util.isEmpty(update.update_type) ? "" : update.update_type);
                App.mAxLoginSp.setUpdateUrl(Util.isEmpty(update.update_url) ? "" : update.update_url);
            }

        }
    };

    private void showUpdateForceDialog() {
        if (App.mAxLoginSp.getAppApkDone()) {
            appUpdateForceDialog.startDownloadService();
        } else {
            appUpdateForceDialog.show();
        }
    }

    public void resetColor() {
        imgHome.setImageResource(R.drawable.axinfu_icon_dis);
        imgLife.setImageResource(R.drawable.life_icon_dis);
        tvHome.setTextColor(unSelectColor);
        tvLife.setTextColor(unSelectColor);
    }

    @Override
    public void setPresenter(IMainContract.IMainPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {

    }

    @Override
    public void getImportantMessageSuccess(RealmList<ImportantMessage> list) {
        showImportantNotices(list);
    }

    private void showImportantNotices(final RealmList<ImportantMessage> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ImportantMessageWithUser withUser = new ImportantMessageWithUser();
                withUser.mobile = App.mAxLoginSp.getUserMobil();
                withUser.importantMessages = list;

                bgRealm.copyToRealmOrUpdate(withUser);
            }
        });

        ArrayList<ImportantMessage> listData = new ArrayList<>();
        for (ImportantMessage obj : list) {
            listData.add(obj);
        }
        Intent i = new Intent(this, ImportantNoticeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImportantNoticeActivity.EXTRA_DATA, listData);
        i.putExtras(bundle);
        startActivity(i);
    }

    private void checkingMsg() {
        checkingImportantMsg();
        if (App.mAxLoginSp.getHasClickGetui()) {
            mMsgPointImg.setVisibility(View.INVISIBLE);
        } else {
            mMsgPointImg.setVisibility(View.VISIBLE);
        }

    }

    private void checkingImportantMsg() {
        String time;
        int size = realm.where(ImportantMessageWithUser.class)
                .equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().size();
        if (size == 0) {
            time = (System.currentTimeMillis() - (24 * 3600 * 1000)) + "";
        } else {
            RealmList<ImportantMessage> list = ((ImportantMessageWithUserRealmProxy) realm.where(ImportantMessageWithUser.class)
                    .equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$importantMessages();
            time = ((ImportantMessageRealmProxy) list.get(list.size() - 1)).realmGet$timestamp();
        }
        presenter.getImportantMessage(time);
    }

    private void logout() {
        MeFragment.logoutOperat();
        startActivity(new Intent(MainActivity.this, LoginInputMobilActivityNew.class));
        App.finishAllLoginActivity();
        finish();
    }

    private void installLocalAPK(String path) {
        if (path != null) {
            if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                File file = new File(path);
                Uri apkUri = FileProvider.getUriForFile(this, "com.dafangya.app.pro.fileprovider.debug", file);//在AndroidManifest中的android:authorities值
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                startActivity(install);
            } else {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(install);
            }
        }
    }

    private void viewHttpUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记录时间
        App.mAxLoginSp.setLastTime(System.currentTimeMillis() + "");
        unregisterReceiver(MainActivityReceive);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            //System.exit(0);
        }
    }

    private void loadingCard() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPQRBankCards(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), true, null) {
                    @Override
                    public void onNext(Object o) {

                        LockInfo lockInfo = SQLite.select()
                                .from(LockInfo.class)
                                .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                                .querySingle();// 区别与 queryList()

                        //判断用户是否绑卡
                        final BankCardResponse bankCardResponse = new Gson().fromJson(o.toString(), BankCardResponse.class);
                        //存入是有卡用户
                        if (bankCardResponse.bank_cards.size() > 0) {
                            App.hasBankCard = true;
                            if (lockInfo == null) {
                                //没有任何解锁信息
                                if (isFinger()) {
                                    //设置指纹解锁
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                    Intent i = new Intent(MainActivity.this, CreateFingerActivity.class);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                } else {
                                    //设置手势解锁
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                    Intent i = new Intent(MainActivity.this, SetGestureActivity.class);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                }
                                LockInfo lockInfox = new LockInfo();
                                lockInfox.regist_serial = App.mAxLoginSp.getRegistSerial();
                                lockInfox.hasBankCard = true;
                                lockInfox.save();
                            } else {
                                lockInfo.hasBankCard = true;
                                lockInfo.save();
                                //先判断是否设置了解锁
                                if (lockInfo.fingerStatus) {
                                    //去指纹解锁
                                    //如果突然没有了指纹--直接去设置手势密码
                                    if (isFinger()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                        Intent i = new Intent(MainActivity.this, VerFingerActivity.class);
                                        i.putExtras(bundle);
                                        if (App.splash)
                                            startActivity(i);
                                    } else {
                                        //先验证登录密码 再去重新设置手势
                                        if (App.splash) {
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                            bundle.putBoolean(VerFingerActivity.VER_LOGIN, true);
                                            Intent i = new Intent(MainActivity.this, VerFingerActivity.class);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                        }else {
                                            if (!isFinger()){
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                                Intent i = new Intent(MainActivity.this, SetGestureActivity.class);
                                                i.putExtras(bundle);
                                                startActivity(i);
                                            }
                                        }

                                    }
                                } else if (lockInfo.gestureStatus) {
                                    //去手势解锁
                                    if (App.splash) {
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                        Intent i = new Intent(MainActivity.this, GestureActivity.class);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    } else {
                                        if (Float.parseFloat(App.mAxLoginSp.getUnionGestureEorTimes()) >= 5) {
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                            Intent i = new Intent(MainActivity.this, SetGestureActivity.class);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                        }
                                    }

                                } else {
                                    //是否是不在提醒
                                    if (!lockInfo.remindStatus) {
                                        if (isFinger()) {
                                            //设置指纹解锁
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                            Intent i = new Intent(MainActivity.this, CreateFingerActivity.class);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                        } else {
                                            //设置手势解锁
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                            Intent i = new Intent(MainActivity.this, SetGestureActivity.class);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                        }
                                    }
                                }
                            }
                        } else {
                            //如果是无卡用户 但是用户主动设置了解锁密码
                            if (lockInfo == null) return;
                            if (lockInfo.fingerStatus) {
                                if (isFinger()) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                    Intent i = new Intent(MainActivity.this, VerFingerActivity.class);
                                    i.putExtras(bundle);
                                    if (App.splash)
                                        startActivity(i);
                                } else {
                                    //先验证登录密码 再去重新设置手势
                                    if (App.splash) {
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                        bundle.putBoolean(VerFingerActivity.VER_LOGIN, true);
                                        Intent i = new Intent(MainActivity.this, VerFingerActivity.class);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    }else {
                                        if (!isFinger()){
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                            Intent i = new Intent(MainActivity.this, SetGestureActivity.class);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                        }
                                    }
                                }
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                Intent i = new Intent(MainActivity.this, GestureActivity.class);
                                i.putExtras(bundle);
                                if (App.splash)
                                    startActivity(i);
                            }
                        }
                    }
                });
    }

    public static class BankCardResponse {
        public BaseResponse resp;
        public List<UPBankCard> bank_cards;
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
