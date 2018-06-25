package com.zhihuianxin.xyaxf.app.me.view.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axinfu.modellib.thrift.app.QuestionAnswer;
import com.axinfu.modellib.thrift.base.Feedback;
import com.axinfu.modellib.thrift.business.Business;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.fee.FeeNotCheckCacheItem;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.fee.SchoolRoll;
import com.axinfu.modellib.thrift.fee.SubFeeDeduction;
import com.axinfu.modellib.thrift.message.AxfMesssage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.axxyf.AxxyfActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginFingerActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginInputMobilActivityNew;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeHelpCenterActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeMsgActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeMsgModifyPwdActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MePayListNewActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeServiceActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeSettingActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeStuMsgActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionBankCardListActivity;

import io.realm.CustomerBaseInfoRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Vincent on 2016/10/31.
 */

public class MeFragment extends BaseRealmFragment {
    public static final String EXTRA_TEXT = "extra_text";
    public static final String BROADCAST_MEFRAGMENT = "broadcast_mefragemnt";
    public static final String BROADCAST_MEFRAGMENT_FEEDBACK = "broadcast_mefragemnt_feedback";
    public static final String BROADCAST_MEFRAGMENT_CLOSE = "broadcast_mefragemnt_close";

    private ImageView mAvatarImg;
    private TextView mSMText;
    private View mMainView;
    private ImageView mMeServicePoint;
    private RelativeLayout back_icon;
    private RelativeLayout meAxxyf;
    private TextView last_period_not_repayment_amt;

    public static Fragment newInstance(String text) {
        MeFragment fragment = new MeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    BroadcastReceiver MeFragmentReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_MEFRAGMENT)) {
                ((TextView) mMainView.findViewById(R.id.telephone)).setText(App.mAxLoginSp.getUserMobil());
            } else if (intent.getAction().equals(BROADCAST_MEFRAGMENT_CLOSE)) {
                if (getActivity() != null) {
                    getActivity().finish();// 关掉MeActivity
                }
            } else {
                checkingFeedbackNotice();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() == null) {
            return;
        }
        setNickName();
        setQiniuPicUrlToUI();
        checkingFeedbackNotice();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(BROADCAST_MEFRAGMENT);
        IntentFilter filterFeedBack = new IntentFilter(BROADCAST_MEFRAGMENT_FEEDBACK);
        IntentFilter filterClose = new IntentFilter(BROADCAST_MEFRAGMENT_CLOSE);
        if (getActivity() != null) {
            getActivity().registerReceiver(MeFragmentReceive, filter);
            getActivity().registerReceiver(MeFragmentReceive, filterFeedBack);
            getActivity().registerReceiver(MeFragmentReceive, filterClose);
        }

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mMainView = view;
        ((TextView) view.findViewById(R.id.telephone)).setText(App.mAxLoginSp.getUserMobil().substring(0, 3) + "****" + App.mAxLoginSp.getUserMobil().substring(7));

        int size = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().size();
        if (size > 0) {
            String avatarUrl = ((CustomerBaseInfoRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                    .findAll().get(0)).realmGet$base_info()).realmGet$avatar();
            App.mAxLoginSp.setAvatarUrl(avatarUrl);
        }

        mAvatarImg = (ImageView) view.findViewById(R.id.touxiang);
        mSMText = (TextView) view.findViewById(R.id.shuoming);
        last_period_not_repayment_amt = (TextView) view.findViewById(R.id.right_top_txt);
        mMeServicePoint = (ImageView) view.findViewById(R.id.me_service_point);
        back_icon = (RelativeLayout) view.findViewById(R.id.back_icon);
        meAxxyf = (RelativeLayout) view.findViewById(R.id.me_axxyf);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        view.findViewById(R.id.me_msg).setOnClickListener(listener);
        view.findViewById(R.id.me_stu).setOnClickListener(listener);
        view.findViewById(R.id.pay_list).setOnClickListener(listener);
        view.findViewById(R.id.service).setOnClickListener(listener);
        view.findViewById(R.id.help_center).setOnClickListener(listener);
        //view.findViewById(R.id.logout).setOnClickListener(listener);
        view.findViewById(R.id.me_axxyf).setOnClickListener(listener);
        view.findViewById(R.id.bankCardId).setOnClickListener(listener);
        view.findViewById(R.id.setting).setOnClickListener(listener);
        //mSMText.setText();
        if (BuildConfig.AnXinDEBUG) {
            TextView version = (TextView) view.findViewById(R.id.tv_debug_version);
            version.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    startActivity(new Intent(getActivity(), LoginFingerActivity.class));
                    return false;
                }
            });
            PackageInfo pi = Util.getPackageInfo(getActivity());
            if (version != null && pi != null) {
                version.setText(String.format("v%s(%s)", pi.versionName, pi.versionCode));
            }
        }


        if (App.loanAccountInfoRep != null) {
            for (int i = 0; i < App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.size(); i++) {
                if (App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay")) {
                    if (App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).status.equals("OK")) {
                        meAxxyf.setVisibility(View.VISIBLE);
                        if (Float.parseFloat(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).last_period_not_repayment_amt) == 0) {
                            last_period_not_repayment_amt.setText("");
                        } else {
                            last_period_not_repayment_amt.setText("本期剩余应还：" + App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).last_period_not_repayment_amt);
                        }
                    } else {
                        meAxxyf.setVisibility(View.GONE);
                    }
                } else {
                    meAxxyf.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setNickName() {
        RealmResults<Customer> realmResults = realm.
                where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (realmResults.size() <= 0) {
            return;
        }
        String nickName = ((CustomerBaseInfoRealmProxy) (((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$nickname();
        mSMText.setText(Util.isEmpty(nickName) ? "" : nickName);
    }

    private void checkingFeedbackNotice() {
        if (App.mAxLoginSp.getHasClickGetuiFeedback()) {
            mMeServicePoint.setVisibility(View.INVISIBLE);
        } else {
            mMeServicePoint.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.me_msg:
                    startActivity(new Intent(getActivity(), MeMsgActivity.class));
                    break;
                case R.id.me_stu:
                    startActivity(new Intent(getActivity(), MeStuMsgActivity.class));
                    break;
                case R.id.pay_list:
                    startActivity(new Intent(getActivity(), MePayListNewActivity.class));
                    break;
                case R.id.login_pwd:
                    startActivity(new Intent(getActivity(), MeMsgModifyPwdActivity.class));
                    break;
                case R.id.service:
                    Intent intent = new Intent(getActivity(), MeServiceActivity.class);
                    if (!App.mAxLoginSp.getHasClickGetuiFeedback()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(MeServiceActivity.EXTRA_REFRESH, "refresh");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        startActivity(intent);
                    }
                    App.mAxLoginSp.setHasClickGetuiFeedback(true);
                    break;
                case R.id.help_center:
                    startActivity(new Intent(getActivity(), MeHelpCenterActivity.class));
                    break;
                case R.id.logout:
                    logout();
                    break;
                case R.id.me_axxyf:
                    startActivity(new Intent(getActivity(), AxxyfActivity.class));
                    break;
                case R.id.bankCardId:
                    Intent i = new Intent(getActivity(), UnionBankCardListActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean(UnionBankCardListActivity.EXTRA_FROMME, true);
                    i.putExtras(b);
                    startActivity(i);
                    break;
                case R.id.setting:
                    startActivity(new Intent(getActivity(), MeSettingActivity.class));
            }
        }
    };

    private void setQiniuPicUrlToUI() {
        if (getActivity() == null) {
            return;
        }
        final String url = App.mAxLoginSp.getAvatarUrl();
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(config)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);
        imageLoader.loadImage(url, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                if (!Util.isEmpty(url)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mAvatarImg.setBackground(null);
                    }
                }

                mAvatarImg.setImageBitmap(loadedImage);
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.me_fragment;
    }

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
                logoutOperat();

                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), LoginInputMobilActivityNew.class));
                    getActivity().finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public static void logoutOperat() {

        App.subFeeDeductionHashMap.clear();

        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();

        App.mSession.setSession("");
        App.mAxLoginSp.setLoginSign(false);
        App.mAxLoginSp.setAvatarUrl("");
        App.mAxLoginSp.setUserMobil("");
        App.mAxLoginSp.setRegistSerial("");
        App.mAxLoginSp.setVerCode("");
        App.mAxLoginSp.setHasCheckUpdate(false);
        App.mAxLoginSp.setVersionFromServer("");

        final RealmResults<QuestionAnswer> questionAnswers = realm.where(QuestionAnswer.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                questionAnswers.deleteAllFromRealm();
            }
        });
        final RealmResults<Business> businesses = realm.where(Business.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                businesses.deleteAllFromRealm();
            }
        });
        final RealmResults<FeeNotCheckCacheItem> feeNotCheckCacheItems = realm.where(FeeNotCheckCacheItem.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                feeNotCheckCacheItems.deleteAllFromRealm();
            }
        });
        final RealmResults<Customer> customers = realm.where(Customer.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                customers.deleteAllFromRealm();
            }
        });
        final RealmResults<Feedback> feedbacks = realm.where(Feedback.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                feedbacks.deleteAllFromRealm();
            }
        });
        final RealmResults<PaymentRecord> paymentRecords = realm.where(PaymentRecord.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                paymentRecords.deleteAllFromRealm();
            }
        });
        final RealmResults<AxfMesssage> axfMesssages = realm.where(AxfMesssage.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                axfMesssages.deleteAllFromRealm();
            }
        });
        final RealmResults<SubFeeDeduction> subFeeDeductions = realm.where(SubFeeDeduction.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                subFeeDeductions.deleteAllFromRealm();
            }
        });

        final RealmResults<SchoolRoll> schoolRolls = realm.where(SchoolRoll.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                schoolRolls.deleteAllFromRealm();
            }
        });

        //切换账号不需要再去验证解锁密码
        App.splash = false;
        App.hasBankCard =false;
        App.loanAccountInfoRep =null;
        App.ocpAccount=null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(MeFragmentReceive);
        }
    }


}
