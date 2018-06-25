package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.animation.ObjectAnimator;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.app.Appendix;
import com.axinfu.modellib.thrift.base.Feedback;
import com.axinfu.modellib.thrift.resource.UploadFileAccess;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.contract.IMeFeedBackContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeFeedBackPresenter;
import com.zhihuianxin.xyaxf.app.me.view.adapter.FeedBackAdapter;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import io.realm.AppendixRealmProxy;
import io.realm.FeedbackRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Vincent on 2016/11/10.
 */

public class MeServiceActivity extends BaseRealmActionBarActivity implements IMeFeedBackContract.IMeFeedBackView{
    public static final int FEED_BACK_RETURN = 1000;
    public static final String EXTRA_REFRESH = "refresh";

    @InjectView(R.id.feedback_list)
    ListView mFeedBackListView;
    @InjectView(R.id.swipeView)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.share_title)
    TextView mShareTitleText;
    @InjectView(R.id.click_focus)
    TextView mShareFocusText;
    @InjectView(R.id.exit)
    TextView mShareExitText;
    @InjectView(R.id.tv_null)
    TextView tv_null;
    @InjectView(R.id.backAnimView)
    View mBackAlertView;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.backAnimView)
    View mAlertView;
    @InjectView(R.id.backview)
    View backView;

    private static final String mWechatPkn = "com.tencent.mm";
    private static final String mSinaPkn = "com.sina.weibo";

    private IMeFeedBackContract.IMeFeedBackPresenter mPresenter;
    private FeedBackAdapter mAdapter;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        findViewById(R.id.action_bar).setVisibility(View.GONE);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        new MeFeedBackPresenter(this,this);
        initViews();
    }

    private void initViews(){
        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        mFeedBackListView.setOnScrollListener(scrollListener);

        mAdapter = new FeedBackAdapter(this,getImgWidth());
        mFeedBackListView.setAdapter(mAdapter);

        RealmResults<Feedback> realmResults = realm.where(Feedback.class).findAll();
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(getIntent().getExtras() != null && !Util.isEmpty(getIntent().getExtras().getString(EXTRA_REFRESH))){
            mPresenter.getFeedBack();
        } else{
            if(realmResults.size() == 0){
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                mPresenter.getFeedBack();
            } else{
                setDBDataToList(realmResults);
            }
        }
    }

    private int getImgWidth(){
        return (int) (((metrics.widthPixels - 15*2*metrics.density) / 4) - 5*2*metrics.density);
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.getFeedBack();
        }
    };

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0)
                mSwipeRefreshLayout.setEnabled(true);
            else
                mSwipeRefreshLayout.setEnabled(false);
        }
    };

    private void setDBDataToList(RealmResults<Feedback> list){
        for (Feedback obj : list){
            Feedback feedback = new Feedback();
            feedback.answer = ((FeedbackRealmProxy)obj).realmGet$answer();
            feedback.date = ((FeedbackRealmProxy)obj).realmGet$date();
            feedback.id = ((FeedbackRealmProxy)obj).realmGet$id();
            feedback.question = ((FeedbackRealmProxy)obj).realmGet$question();
            if(((FeedbackRealmProxy)obj).realmGet$appendices().size() != 0){
                RealmList<Appendix> appendices = new RealmList<>();
                for(Appendix appendix : ((FeedbackRealmProxy)obj).realmGet$appendices()){
                    Appendix appendixLocal = new Appendix();
                    appendixLocal.url = ((AppendixRealmProxy)appendix).realmGet$url();
                    appendixLocal.id = ((AppendixRealmProxy)appendix).realmGet$id();
                    appendices.add(appendixLocal);
                }
                feedback.appendices = appendices;
            }
            mAdapter.add(feedback);
        }
        mAdapter.notifyDataSetChanged();
    }

    View.OnClickListener weChatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideBackAlertAnim();

            ClipboardManager cm = (ClipboardManager) getSystemService("clipboard");//Context.CLIPBOARD_SERVICE
            cm.setText("校园安心付");

            if(isPkgInstalled(mWechatPkn)){
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName(mWechatPkn,"com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivity(intent);
            } else{
                Toast.makeText(MeServiceActivity.this,"未安装微信！",Toast.LENGTH_LONG).show();
            }
        }
    };

    View.OnClickListener sinaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideBackAlertAnim();

            ClipboardManager cm = (ClipboardManager) getSystemService("clipboard");//Context.CLIPBOARD_SERVICE
            cm.setText("@校园安心付");

            if(isPkgInstalled(mSinaPkn)){
                Toast.makeText(MeServiceActivity.this,"正在打开客户端，请稍后！",Toast.LENGTH_LONG).show();
                ShareSDK.initSDK(getApplicationContext());
                SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
                sp.setText("@校园安心付");

                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                weibo.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        Toast.makeText(MeServiceActivity.this,"分享成功!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        Toast.makeText(MeServiceActivity.this,"客户端打开失败！（"+throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                weibo.share(sp);
            } else{
                Toast.makeText(MeServiceActivity.this,"未安装新浪微博！",Toast.LENGTH_LONG).show();
            }
        }
    };

    private void showShareAlertAnim(String type){
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", 0,
                halfScreen,halfScreen + 50,halfScreen + 10,halfScreen + 35,halfScreen + 30);//450 410 435 430
        animator2.setDuration(700);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);

        if(type.equals(mWechatPkn)){
           mShareFocusText.setOnClickListener(weChatListener);
            mShareTitleText.setText("“校园安心付”已复制，打开微信，粘帖搜索，关注我们吧！");
            mAlertView.setBackgroundResource(R.drawable.window_wechat);
        } else{
            mShareFocusText.setOnClickListener(sinaListener);
            mShareTitleText.setText("“@校园安心付”已复制，打开新浪微博，粘帖搜索，关注我们吧！");
            mAlertView.setBackgroundResource(R.drawable.window_weibo);
        }
        mShareExitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBackAlertAnim();
            }
        });
    }

    private void hideBackAlertAnim(){
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY",halfScreen + 30,0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);
    }

    @OnClick(R.id.grayBg)
    public void onBtnGreyBgClick(){

    }

    @OnClick(R.id.share_wechat)
    public void onBtnShareWechat(){
        showShareAlertAnim(mWechatPkn);
    }

    @OnClick(R.id.share_xinlang)
    public void onBtnShareSina(){
        showShareAlertAnim(mSinaPkn);
    }

    @OnClick(R.id.share_tell)
    public void onBtnTell(){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4000281024"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isPkgInstalled(String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    @OnClick(R.id.feedback)
    public void onBtnFeedBack(){
        startActivityForResult(new Intent(this,MeFeedBackActivity.class),FEED_BACK_RETURN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FEED_BACK_RETURN && resultCode == RESULT_OK){
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            refreshListener.onRefresh();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_service_activity;
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }

    @Override
    public void feedBackSuccess() {
    }

    @OnClick(R.id.service_back)
    public void onBackBtn(){
        finish();
    }

    @Override
    public void getFeedBackList(RealmList<Feedback> feedbacks) {
        if (feedbacks.size() == 0){
            tv_null.setVisibility(View.VISIBLE);
        }else {
            tv_null.setVisibility(View.GONE);
            mAdapter.clear();
            for (Feedback feedback : feedbacks) {
                mAdapter.add(feedback);
            }
            mAdapter.notifyDataSetChanged();
            saveDataInDB(feedbacks);
        }
    }

    @Override
    public void getQiNiuAccessSuccess(RealmList<UploadFileAccess> access) {
        // no use
    }

    private void saveDataInDB(final RealmList<Feedback> feedbacks){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(feedbacks);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.d("LoginVerPwdActivity", "存储customer数据成功!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("LoginVerPwdActivity", "存储customer数据失败!");
            }
        });
    }

    @Override
    public void setPresenter(IMeFeedBackContract.IMeFeedBackPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
