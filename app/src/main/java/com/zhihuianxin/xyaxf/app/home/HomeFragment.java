package com.zhihuianxin.xyaxf.app.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.man.MANHitBuilders;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.axinfu.modellib.thrift.business.Business;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.axinfu.modellib.thrift.message.Advertise;
import com.axinfu.modellib.thrift.ocp.OcpAccount;
import com.axinfu.modellib.thrift.payment.BankLimit;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.axxyf.AxxyfActivity;
import com.zhihuianxin.xyaxf.app.banner.BannerFragment;
import com.zhihuianxin.xyaxf.app.ecard.open.EcardOpenActivity;
import com.zhihuianxin.xyaxf.app.ecard.view.EcardActivity;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.fee.check.FeeCheckActivity;
import com.zhihuianxin.xyaxf.app.home.adapter.HomeGridAdapter;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginMsgActivity;
import com.zhihuianxin.xyaxf.app.login.view.fragment.LoginSelectCityActivity;
import com.zhihuianxin.xyaxf.app.me.MeActivity;
import com.zhihuianxin.xyaxf.app.ocp.OcpVerActivity;
import com.zhihuianxin.xyaxf.app.payment.BankInfoCache;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptCodeActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.BusinessRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.SchoolRealmProxy;
import rx.functions.Action1;

import static com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment.BROADCAST_MEFRAGMENT_CLOSE;

/**
 * Created by zcrpro on 2016/10/28.
 */

public class HomeFragment extends BaseRealmFragment implements HomeContract.HomeView {
    @InjectView(R.id.fl_banner_bg)
    FrameLayout flBanner;
    @InjectView(R.id.loufang)
    View mLoufang;
    @InjectView(R.id.sun)
    ImageView sun;
    @InjectView(R.id.tree_view)
    View mTreeView;
    @InjectView(R.id.banner_view)
    FrameLayout bannerView;
    @InjectView(R.id.ani3)
    View ani3;
    @InjectView(R.id.ani4)
    View ani4;
    @InjectView(R.id.ani5)
    View ani5;

    @InjectView(R.id.tv_fee_status)
    TextView tvFeeStatus;
    @InjectView(R.id.home_mine_view)
    RelativeLayout homeMineView;
    @InjectView(R.id.home_mine)
    ImageView homeMine;
    @InjectView(R.id.ll_ocp)
    LinearLayout llOcp;
    @InjectView(R.id.banner)
    FrameLayout banner;
    @InjectView(R.id.titleView)
    RelativeLayout titleView;
    @InjectView(R.id.upqr_view)
    LinearLayout upqrView;
    @InjectView(R.id.img_bind)
    ImageView imgBind;
    @InjectView(R.id.tv_bind_title)
    TextView tvBindTitle;
    @InjectView(R.id.tv_bind_content)
    TextView tvBindContent;
    @InjectView(R.id.sun_def)
    ImageView sunDef;
    @InjectView(R.id.plant4_def)
    ImageView plant4Def;
    @InjectView(R.id.plant3_def)
    ImageView plant3Def;
    @InjectView(R.id.local_def)
    View localDef;
    @InjectView(R.id.ani3_def)
    RelativeLayout ani3Def;
    @InjectView(R.id.loufang_def)
    ImageView loufangDef;
    @InjectView(R.id.plant_def)
    ImageView plantDef;
    @InjectView(R.id.tree_view_def)
    RelativeLayout treeViewDef;
    @InjectView(R.id.banner_default_view)
    FrameLayout bannerDefaultView;
    @InjectView(R.id.building)
    ImageView building;
    @InjectView(R.id.plant4)
    ImageView plant4;
    @InjectView(R.id.plant3)
    ImageView plant3;
    @InjectView(R.id.local)
    View local;
    @InjectView(R.id.local1)
    View local1;
    @InjectView(R.id.plant)
    ImageView plant;
    @InjectView(R.id.img_bind_card)
    ImageView imgBindCard;
    @InjectView(R.id.tv_bind_card_title)
    TextView tvBindCardTitle;
    @InjectView(R.id.tv_bind_card_content)
    TextView tvBindCardContent;
    @InjectView(R.id.ll_bank_card)
    LinearLayout llBankCard;
    @InjectView(R.id.ll_home_gg)
    LinearLayout llHomeGg;
    @InjectView(R.id.center_view)
    View centerView;

    private List<Advertise> mBannerList;
    private String defaultBannerImg;

    public static Fragment newInstance(String text) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    public static final String EXTRA_TEXT = "extra_text";
    public static final String EXTRA_SCHOOL_DATA = "school_data";

    @InjectView(R.id.ll_ecard)
    LinearLayout llEcard;
    @InjectView(R.id.ll_fee)
    LinearLayout llFee;
    @InjectView(R.id.ll_buss)
    LinearLayout llBuss;
    @InjectView(R.id.tv_select_school)
    TextView tvSelectSchool;
    @InjectView(R.id.home_msg)
    ImageView homeMsg;
    @InjectView(R.id.tv_ecard_balance)
    TextView tvEcardBalance;
    @InjectView(R.id.msg_count)
    TextView mMsgCountText;
    @InjectView(R.id.msg_alert)
    View mMsgAlertView;
    @InjectView(R.id.home_msg_view)
    RelativeLayout homeMsgView;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.clickChangeSchool)
    TextView changeSchoolTxt;

    private DisplayMetrics metrics;
    private HomeContract.HomePresenter presenter;
    private HomeGridAdapter homeGridAdapter;

    private Customer customer;

    BroadcastReceiver HomeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.NOTICE_BROADCAST)) {
                checkingMsg();// 检测推送消息
            } else if (intent.getAction().equals(EXTRA_SCHOOL_DATA)) {
                presenter.loadBannerData();// 选择学校后更新banner数据
            } else if (intent.getAction().equals(BROADCAST_MEFRAGMENT_CLOSE)) {
                getActivity().finish();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() == null) {
            return;
        }
        App.add_card_back_home = false;
        presenter.loadCustomerData();
        presenter.getEcardData();
        checkingMsg();
        App.mAxLoginSp.setUnionReMark("");
        RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("fee_refresh")) {
                    presenter.loadCustomerData();
                } else if (event.equals("ecard_refresh")) {
                    presenter.loadCustomerData();
                } else if (event.equals("fixed_activity_add_bank_def")) {
                    presenter.loadCustomerData();
                } else if (event.equals("open")) {
//                    //一码通已经开通
//                    if(llOcp!=null&&customer!=null&&!customer.is_show_bind_card_guide)
//                        llOcp.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        new HomePresenter(this, getActivity());

        presenter.getBanklimit();
        presenter.getAccountPayMethodInfo();
        presenter.getProtocolByNo();
        presenter.loadBannerData();
        return rootView;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        IntentFilter filter = new IntentFilter(MainActivity.NOTICE_BROADCAST);
        getActivity().registerReceiver(HomeReceiver, filter);
        IntentFilter filterUpdateSchool = new IntentFilter(EXTRA_SCHOOL_DATA);
        getActivity().registerReceiver(HomeReceiver, filterUpdateSchool);
        IntentFilter filterCloseLogout = new IntentFilter(BROADCAST_MEFRAGMENT_CLOSE);
        getActivity().registerReceiver(HomeReceiver, filterCloseLogout);
    }

    @OnClick(R.id.home_mine_view)
    public void meOnClick() {
        startActivity(new Intent(getActivity(), MeActivity.class));
    }

    @OnClick(R.id.upqr_view)
    public void upqrClick() {
        gotoQrView();
    }

    private void gotoQrView() {
        presenter.getBankCard();
    }

    private void checkingMsg() {
        if (App.mAxLoginSp.getHasClickGetui()) {
            mMsgAlertView.setVisibility(View.INVISIBLE);
        } else {
            mMsgAlertView.setVisibility(View.VISIBLE);
        }
    }

    private boolean canChangeSchool() {
        boolean feeStaueIsNull = ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account() == null;
        boolean eCardStaueIsNull = ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$ecard_account() == null;
        boolean oCpStaue = App.ocpAccount == null;

        String feeStatus = ((FeeAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account()).realmGet$status();
        String eCardStatus = ((ECardAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$ecard_account()).realmGet$status();
        String oCpStatus = App.ocpAccount.account.status;

        return (feeStaueIsNull || eCardStaueIsNull || oCpStaue) ||
                (!((!Util.isEmpty(feeStatus) && feeStatus.equals("OK")) || !Util.isEmpty(eCardStatus) && eCardStatus.equals("OK") || !Util.isEmpty(oCpStatus) && oCpStatus.equals("OK")));
    }

    @OnClick(R.id.ll_ecard)
    public void onECardOnClick() {
        boolean isLegalFee = false;
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        for (Business obj : ((CustomerRealmProxy) customers.get(0)).realmGet$businesses()) {
            if (((BusinessRealmProxy) obj).realmGet$type().equals("ECard") && ((BusinessRealmProxy) obj).realmGet$status().equals("OK")) {
                isLegalFee = true;
                final ECardAccount eCardAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account();
                if (((ECardAccountRealmProxy) eCardAccount).realmGet$status().equals("OK")) {
                    startActivity(new Intent(getActivity(), EcardActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), EcardOpenActivity.class));
                }
                break;
            }

            if (((BusinessRealmProxy) obj).realmGet$type().equals("ECard") && ((BusinessRealmProxy) obj).realmGet$status().equals("Disabled")) {
                com.xyaxf.axpay.Util.myShowToast(getActivity(), "尚未开通，敬请期待！", 800);
            }
        }

    }

    @OnClick(R.id.ll_fee)
    public void onFeeOnClick() {
        boolean isLegalFee = false;
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (customers.size() == 0) {
            return;
        }
        for (Business obj : ((CustomerRealmProxy) customers.get(0)).realmGet$businesses()) {
            if (((BusinessRealmProxy) obj).realmGet$type().equals("Fee") && ((BusinessRealmProxy) obj).realmGet$status().equals("OK")) {
                isLegalFee = true;
                final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();
                if (((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("OK")) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FeeActivity.ENTER_FLAG, FeeActivity.normal);
                    Intent intent = new Intent(getActivity(), FeeActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getActivity(), FeeCheckActivity.class));
                }
                break;
            }

            if (((BusinessRealmProxy) obj).realmGet$type().equals("Fee") && ((BusinessRealmProxy) obj).realmGet$status().equals("Disabled")) {
                com.xyaxf.axpay.Util.myShowToast(getActivity(), "尚未开通，敬请期待！", 800);
            }
        }

    }

    @OnClick(R.id.clickChangeSchool)
    public void onChangeSchoolTxtClick() {
        if (canChangeSchool()) {
            startActivity(new Intent(getActivity(), LoginSelectCityActivity.class));
        }
    }

    @OnClick(R.id.tv_select_school)
    public void onSchoolOnClick() {
        if (canChangeSchool()) {
            startActivity(new Intent(getActivity(), LoginSelectCityActivity.class));
        }
    }

    @OnClick(R.id.home_msg_view)
    public void onMsgOnClick() {
        Intent intent = new Intent(getActivity(), LoginMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(LoginMsgActivity.EXTRA_NEED_REFRESH, !App.mAxLoginSp.getHasClickGetui());// 没有点的时候LoginMsgActivity才主动刷新

        App.mAxLoginSp.setHasClickGetui(true);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void customerSuccess(final Customer customer) {
        if (getActivity() == null) {
            return;
        }

        this.customer = customer;

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                customer.mobile = App.mAxLoginSp.getUserMobil();
                bgRealm.copyToRealmOrUpdate(customer);
            }
        });

        List<Business> businesses = new ArrayList<>();
        businesses.addAll(customer.businesses);

        Iterator<Business> it = businesses.iterator();
        while (it.hasNext()) {
            Business x = it.next();
            if (!x.container.equals("AnXinFu") || x.status.equals("Invisible")) {
                it.remove();
            }
        }

        if (recyclerview != null)
            recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        homeGridAdapter = new HomeGridAdapter(getActivity(), businesses, R.layout.home_bus_item);
        if (recyclerview != null)
            recyclerview.setAdapter(homeGridAdapter);
        if (homeGridAdapter != null)
            homeGridAdapter.notifyDataSetChanged();

        if (!TextUtils.isEmpty(customer.school.name)) {
            tvSelectSchool.setText(customer.school.name);
        } else {
            tvSelectSchool.setText("请选择学校");
        }

        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (customers.size() != 0) {
            for (Business obj : ((CustomerRealmProxy) customers.get(0)).realmGet$businesses()) {

                final ECardAccount eCardAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account();
                if (((BusinessRealmProxy) obj).realmGet$type().equals("ECard") && ((BusinessRealmProxy) obj).realmGet$status().equals("Disabled")) {
                    tvEcardBalance.setText("暂未开通");
                    break;
                } else {
                    if (((ECardAccountRealmProxy) eCardAccount).realmGet$status().equals("NotOpened")) {
                        tvEcardBalance.setText("点击开通");
                    }
                }
            }
        }

        final RealmResults<Customer> customers2 = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (customers2.size() != 0) {
            for (Business obj : ((CustomerRealmProxy) customers2.get(0)).realmGet$businesses()) {
                final FeeAccount feeAccount = ((CustomerRealmProxy) customers2.get(0)).realmGet$fee_account();
                if (((BusinessRealmProxy) obj).realmGet$type().equals("Fee") && ((BusinessRealmProxy) obj).realmGet$status().equals("Disabled")) {
                    tvFeeStatus.setText("暂未开通");
                    break;
                } else {
                    if (((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("NotOpened")) {
                        tvFeeStatus.setText("点击开通");
                    } else if (((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("OK")) {
                        tvFeeStatus.setText("学费杂费");
                    }
                }
            }
        }
        presenter.getOcpAccountStatus(customer);


    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
        if (bankCards != null && bankCards.size() > 0) {
            startActivity(new Intent(getActivity(), UnionSweptCodeActivity.class));
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("show", true);
            Intent intent = new Intent(getActivity(), UnionSweptEmptyCardActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void getProtocolByNoSuccess(List<App.Protocol> protocols) {
        App.protocols.addAll(protocols);
    }

    @Override
    public void getAccountPayMethodInfoSuccess(AxxyfActivity.LoanAccountInfoRep rep) {
        for (int i = 0; i < rep.loan_account_info.valid_loan_way_account_info.size(); i++) {
            if (rep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay")) {
                App.loanAccountInfoRep = rep;
            }
        }
    }

    @Override
    public void getBanklimitSuccess(HomePresenter.BankResponse bankResponse) {
        BankLimit BankLimit = new BankLimit();
        BankLimit.name = "其他银行";
        BankLimit.local_logo = R.drawable.a_icon;
        BankLimit.local = true;
        bankResponse.limit.bank_infos.add(BankLimit);
        BankInfoCache.getInstance().setBankInfo(bankResponse.limit.bank_infos);
        BankInfoCache.getInstance().setTradeLimit(bankResponse.limit);
    }

    @Override
    public void getOcpAccountStatusSuccess(Customer customer, OcpAccount ocpAccount) {
        //获取到之后存入数据
        App.ocpAccount = ocpAccount;


        //判断是否启用了安心一码通业务
        for (int i = 0; i < customer.businesses.size(); i++) {
            if (customer.businesses.get(i).type.equals("AxfQR")) {
                if (customer.businesses.get(i).status.equals("OK")) {
                    if (ocpAccount != null && !ocpAccount.account.status.equals("OK")) {
                        llOcp.setVisibility(View.VISIBLE);
                        llHomeGg.setVisibility(View.VISIBLE);
                        llOcp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("qr", "");
                                bundle.putBoolean("result", false);
                                Intent intent = new Intent(getActivity(), OcpVerActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        llOcp.setVisibility(View.GONE);
                    }
                } else {
                    llOcp.setVisibility(View.GONE);
                }
            }
        }

        //如果是需要显示绑卡引导
        if (!App.hasBankCard && customer.is_show_bind_card_guide) {
            llBankCard.setVisibility(View.VISIBLE);
            llHomeGg.setVisibility(View.VISIBLE);
            if (llOcp.getVisibility()==View.VISIBLE){
                //短文字
                if (!TextUtils.isEmpty(customer.bind_card_short_hint))
                tvBindCardContent.setText(customer.bind_card_short_hint);
            }else {
                //长文字
                if (!TextUtils.isEmpty(customer.bind_card_long_hint))
                tvBindCardContent.setText(customer.bind_card_long_hint);
            }
            llBankCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.add_card_back_home = true;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("no_more", true);
                    Intent intent = new Intent(getActivity(), UnionSweptEmptyCardActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } else {
            llBankCard.setVisibility(View.GONE);
        }

        if (llOcp.getVisibility() == View.VISIBLE && llBankCard.getVisibility() == View.VISIBLE){
            centerView.setVisibility(View.VISIBLE);
        }

        if (llOcp.getVisibility() == View.VISIBLE && llBankCard.getVisibility() == View.GONE){
            centerView.setVisibility(View.GONE);
        }

        if (llOcp.getVisibility() == View.GONE && llBankCard.getVisibility() == View.VISIBLE){
            centerView.setVisibility(View.GONE);
        }

        if (llOcp.getVisibility() == View.GONE && llBankCard.getVisibility() == View.GONE) {
            llHomeGg.setVisibility(View.GONE);
        }

        if (canChangeSchool()) {
            changeSchoolTxt.setVisibility(View.VISIBLE);
        } else {
            changeSchoolTxt.setVisibility(View.GONE);
        }
    }

    @Override
    public void getEcardDataSuccess(HomePresenter.EcardResponse ecardResponse) {
        if (ecardResponse.ecard.status.equals("OK")) {
            if (tvEcardBalance != null)
                tvEcardBalance.setText("余额¥" + new DecimalFormat("0.00").format(Float.parseFloat(ecardResponse.ecard.card_balance)));
            MANHitBuilders.MANCustomHitBuilder hitBuilder = new MANHitBuilders.MANCustomHitBuilder("rule");
            hitBuilder.setDurationOnEvent(3 * 60 * 1000);
            hitBuilder.setEventPage("HomeFragment.class");
            hitBuilder.setProperty("rule_ecard", "lable" + "一卡通获取成功" + "||" +
                    "id_card_no" + ecardResponse.ecard.id_card_no + "||" + "time" + Util.getNowTime() + "||" + "mobile" + App.mAxLoginSp.getUserMobil() + "||");
            MANService manService = MANServiceProvider.getService();
            manService.getMANAnalytics().getDefaultTracker().send(hitBuilder.build());
        } else if (ecardResponse.ecard.status.equals("ReportLoss")) {
            if (tvEcardBalance != null)
                tvEcardBalance.setText("已挂失");
        } else {
            //卡异常
            if (tvEcardBalance != null)
                tvEcardBalance.setText("¥ -.--");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null && HomeReceiver != null) {
            getActivity().unregisterReceiver(HomeReceiver);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void customerFailure() {
    }

    @Override
    public void bannerSuccess(final List<Advertise> list) {
        try {
            defaultBannerImg = ((SchoolRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                    .findAll().get(0)).realmGet$school()).realmGet$land_mark_image();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Util.isEmpty(defaultBannerImg)) {
            showAnim(null, list);
        } else {
            DisplayImageOptions config = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(defaultBannerImg, config, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, final View view,
                                              Bitmap loadedImage) {
                    showAnim(loadedImage, list);
                }
            });
        }
    }

    private void showAnim(Bitmap loadedImage, List<Advertise> list) {
        if (loadedImage != null) {
            mLoufang.setBackground(new BitmapDrawable(loadedImage));
            int w = metrics.widthPixels * 13 / 14;
            int h = loadedImage.getHeight() * w / loadedImage.getWidth();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
            params.setMargins(0, 0, 0, -(int) (250 * metrics.density));
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            mLoufang.setLayoutParams(params);
        }

        ObjectAnimator.ofFloat(flBanner, "alpha", 0f, 1f).setDuration(1500).start();
        ObjectAnimator.ofFloat(sun, "alpha", 0f, 1f).setDuration(1500).start();

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator a1 = ObjectAnimator.ofFloat(mTreeView, "translationY", (int) metrics.density * 300, 0);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(mTreeView, "alpha", 0, 1);
        set.play(a1).with(a2);
        set.setDuration(600).start();

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mLoufang, "translationY", 0, -(int) metrics.density * 250);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mLoufang, "alpha", 0, 1).setDuration(1000);
        animatorSet.play(objectAnimator).with(objectAnimator1);
        animatorSet.setDuration(600);
        animatorSet.setStartDelay(300);
        animatorSet.start();

        ObjectAnimator objectAnimator_ani3 = ObjectAnimator.ofFloat(ani3, "translationY", 0, -(int) metrics.density * 250);
        objectAnimator_ani3.setDuration(600);
        objectAnimator_ani3.setStartDelay(500);
        objectAnimator_ani3.start();

        ObjectAnimator objectAnimator_ani4 = ObjectAnimator.ofFloat(ani4, "translationY", 0, -(int) metrics.density * 250);
        objectAnimator_ani4.setDuration(600);
        objectAnimator_ani4.setStartDelay(650);
        objectAnimator_ani4.start();

        ObjectAnimator objectAnimator_ani5 = ObjectAnimator.ofFloat(ani5, "translationY", 0, -(int) metrics.density * 250);
        objectAnimator_ani5.setDuration(600);
        objectAnimator_ani5.setStartDelay(650);
        objectAnimator_ani5.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewSaveToImage(flBanner);
            }
        }, 2000);

        if (list != null && list.size() > 0) {
            showBanner(list);
        }
    }

    private void showBanner(final List<Advertise> list) {
        ObjectAnimator objectAni = ObjectAnimator.ofFloat(flBanner, "alpha", 1, 0).setDuration(1500);
        objectAni.setDuration(1000);
        objectAni.setStartDelay(3000);
        objectAni.start();
        objectAni.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                flBanner.setVisibility(View.GONE);
                bannerView.setVisibility(View.VISIBLE);
                //加入banner
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BannerFragment bannerFragment = new BannerFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BannerFragment.EXTRA_DATA, (Serializable) list);
                bannerFragment.setArguments(bundle);
                transaction.replace(R.id.banner_view, bannerFragment);
                transaction.commitAllowingStateLoss();
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

    }

    private Bitmap loadBitmapFromView(View v) {
        Bitmap bmp = null;
        try {
            int w = v.getWidth();
            int h = v.getHeight();

            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);

            c.drawColor(Color.WHITE);
            /** 如果不设置canvas画布为白色，则生成透明 */

            v.layout(0, 0, w, h);
            v.draw(c);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }

    // 为图片target添加水印
    private Bitmap createWatermarkBitmap(Bitmap target) {
        Bitmap bmp = null;
        try {
            int w = target.getWidth();
            int h = target.getHeight();

            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);

            Paint p = new Paint();

            p.setAntiAlias(true);// 去锯齿

            canvas.drawBitmap(target, 0, 0, p);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public void viewSaveToImage(View view) {
        try {
            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            view.setDrawingCacheBackgroundColor(Color.WHITE);

            // 把一个View转换成图片
            Bitmap cachebmp = loadBitmapFromView(view);

            // 添加水印
            Bitmap bitmap = Bitmap.createBitmap(createWatermarkBitmap(cachebmp));

            FileOutputStream fos;
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录
                File sdRoot = Environment.getExternalStorageDirectory();
                if (!Util.isEmpty(App.mAxLoginSp.getLadmarkName())) {
                    File file = new File(sdRoot, App.mAxLoginSp.getLadmarkName());
                    file.delete();
                }

                String ladMrkLocalName = (int) (Math.random() * 100 + 1) + ".PNG";
                File file = new File(sdRoot, ladMrkLocalName);
                App.mAxLoginSp.setLadmarkName(ladMrkLocalName);
                fos = new FileOutputStream(file);
            } else
                throw new Exception("创建文件失败!");
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        view.destroyDrawingCache();
    }

    @Override
    public void setPresenter(HomeContract.HomePresenter presenter) {
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
}