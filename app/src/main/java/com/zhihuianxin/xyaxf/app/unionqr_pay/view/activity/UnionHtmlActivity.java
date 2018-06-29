package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.UPQRService;
import modellib.thrift.unqr.UPBankCard;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.lock.create.CreateFingerActivity;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo;
import com.zhihuianxin.xyaxf.app.lock.create.LockInfo_Table;
import com.zhihuianxin.xyaxf.app.lock.create.SetGestureActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionBindCardContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionUPQRBindCardPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/15.
 */

public class UnionHtmlActivity extends BaseRealmActionBarActivity implements IunionBindCardContract.IbindCard{
    public static final String EXTRA_FROM_UPQRCASHIER = "from_upqr";
    public static final String EXTRA_FROM_BANKCARDLIST = "from_bankcard_List";
    public WebView mWebView;
    private ProgressBar progressBar;
    private WebChromeClient mWebChromeClient;
    private IunionBindCardContract.IbindCardPresenter presenter;
    private UnionPayEntity entity;

    private boolean isOcp;


    FingerprintManagerCompat manager;
    FingerprintManagerCompat fingerprintManagerCompat;
    KeyguardManager mKeyManager;

    @Override
    protected int getContentViewId() {
        return R.layout.union_opencard_html;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        initViews();
    }

    private void initViews(){
        if(getIntent().getExtras() != null && getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) != null){
            entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        }
        if(getIntent().getExtras() != null){
            isOcp = getIntent().getExtras().getBoolean("isOcp");
        }
        new UnionUPQRBindCardPresenter(this,this);
        mWebView = (WebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " AXinFu.App.V3");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWebChromeClient = new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    } else {
                        progressBar.setProgress(newProgress);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if(title.contains("html") || title.contains("http")){
                    return;
                }
                setTitle(title);
            }
        };

        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.addJavascriptInterface(new AxinfuApp(), "axinfuApp");
        presenter.bingUPQRBankCard();
    }

    public class AxinfuApp {

        @JavascriptInterface
        public void closeView() {
           myBack();
        }
    }

    private void myBack(){
        if (App.add_card_back_home){
            finish();
        }else {
            boolean fromBankList = false;
            if(getIntent().getExtras() != null && getIntent().getExtras().getBoolean(EXTRA_FROM_BANKCARDLIST)){
                fromBankList = true;
            }
            if(getIntent().getExtras().getBoolean(EXTRA_FROM_UPQRCASHIER) && entity != null){// 验证支付密码 和 UPQR收银台帮卡fragment过来的
                presenter.getBankCard();
            } else if(!fromBankList && getIntent().getExtras().getBoolean(EXTRA_FROM_UPQRCASHIER,false) && entity == null){// swep 过来的
                presenter.getBankCard();
            } else if(getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null){
                presenter.getBankCard();
            } else {
                finish();
            }
        }

        //        if (!isOcp){
//            RxBus.getDefault().send("fixed_activity_add_bank");
//        }else {
//        if(!this.isFinishing())
        try {
            RxBus.getDefault().send("fixed_activity_add_bank_def");
            RxBus.getDefault().send("finish");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }

        //引导去设置解锁
        getBankCard();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void bingUPQRBankCardResult(String add_card_url) {
        mWebView.loadData(
                new String(Base64.decode(add_card_url,Base64.DEFAULT)), "text/html", "UTF-8");
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
        ArrayList<UPBankCard> bankCardsNew = new ArrayList<>();
        for(int i = 0;i < bankCards.size();i++){
            UPBankCard bankCard = new UPBankCard();
            bankCard.setId(Util.isEmpty(bankCards.get(i).getId())?"":bankCards.get(i).getId());
            bankCard.setIss_ins_name(Util.isEmpty(bankCards.get(i).getIss_ins_name())?"":bankCards.get(i).getIss_ins_name());
            bankCard.setCard_no(Util.isEmpty(bankCards.get(i).getCard_no())?"":bankCards.get(i).getCard_no());
            bankCard.setCard_type_name(Util.isEmpty(bankCards.get(i).getCard_type_name())?"":bankCards.get(i).getCard_type_name());
            bankCard.setIss_ins_icon(Util.isEmpty(bankCards.get(i).getIss_ins_icon()) ? "" : bankCards.get(i).getIss_ins_icon());
            bankCardsNew.add(bankCard);
        }
        if(bankCards != null && bankCards.size() > 0){
            App.mAxLoginSp.setUnionSelBankId(bankCardsNew.get(bankCardsNew.size()-1).getId());
        }

        if(entity != null){
            entity.getBankCards().clear();
            entity.setBankCards(bankCardsNew);

            App.finishAllActivity();// UnionPayActivty删除 不会出现两个

            Intent i = new Intent(UnionHtmlActivity.this,UnionPayActivity.class);
            Bundle b = new Bundle();
            b.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
            b.putBoolean(UnionCashierFragment.EXTRA_SHOW_UNIONCASHIER,true);
            i.putExtras(b);
            startActivity(i);
            finish();
        } else{//swep
            App.finishAllActivity();// UnionPayActivty删除 不会出现两个
//
//            Intent i = new Intent(UnionHtmlActivity.this,UnionSweptCodeActivity.class);
//            Bundle b = new Bundle();
//            b.putBoolean(UnionSweptCodeActivity.EXTRA_SHOWFRAG,true);
//            i.putExtras(b);
//            startActivity(i);
            finish();
        }
    }

    @Override
    public void setPresenter(IunionBindCardContract.IbindCardPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLeftButtonClick(View view) {
        myBack();
    }

    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}



    public void getBankCard() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPQRBankCards(NetUtils.getRequestParams(this,map),NetUtils.getSign(NetUtils.getRequestParams(this,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this,false,null) {

                    @Override
                    public void onNext(Object o) {

                        LockInfo lockInfo = SQLite.select()
                                .from(LockInfo.class)
                                .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                                .querySingle();// 区别与 queryList()

                        UnionQrMainPresenter.GetUPQRBankCardsResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetUPQRBankCardsResponse.class);
                        if (response.bank_cards.size()==1){
                            App.hasBankCard =true;
                            if (lockInfo == null) {
                                //没有任何解锁信息
                                if (isFinger()) {
                                    //设置指纹解锁
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                    Intent i = new Intent(UnionHtmlActivity.this, CreateFingerActivity.class);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                    finish();
                                } else {
                                    //设置手势解锁
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                    Intent i = new Intent(UnionHtmlActivity.this, SetGestureActivity.class);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                    finish();
                                }
                                LockInfo lockInfox = new LockInfo();
                                lockInfox.regist_serial = App.mAxLoginSp.getRegistSerial();
                                lockInfox.hasBankCard = true;
                                lockInfox.save();
                            }else {
                                lockInfo.hasBankCard = true;
                                lockInfo.save();
                                //先判断是否设置了解锁
                                if (lockInfo.fingerStatus) {
                                    //去指纹解锁
                                } else if (lockInfo.gestureStatus) {
                                    //去手势解锁
                                } else {
                                    //是否是不在提醒
                                    if (!lockInfo.remindStatus) {
                                        if (isFinger()) {
                                            //设置指纹解锁
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                            Intent i = new Intent(UnionHtmlActivity.this, CreateFingerActivity.class);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            //设置手势解锁
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                                            Intent i = new Intent(UnionHtmlActivity.this, SetGestureActivity.class);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
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
