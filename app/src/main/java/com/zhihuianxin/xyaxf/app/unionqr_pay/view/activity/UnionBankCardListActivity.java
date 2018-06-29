package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import modellib.thrift.customer.Customer;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.UPBankCard;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.WebPageActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionQrMainContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CustomerBaseInfoRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Vincent on 2017/11/7.
 */

public class UnionBankCardListActivity extends BaseRealmActionBarActivity implements IunionQrMainContract.IGetBankCardInfo {
    public static final String EXTRA_FROMME = "from_me";
    @InjectView(R.id.bankList)
    ListView listView;
    @InjectView(R.id.backAnimView)
    View mBackAlertView;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.exit)
    TextView mShareExitText;
    @InjectView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.click_focus)
    Button delBnt;
    @InjectView(R.id.empty_list_icon)
    ImageView emptyIcon;
    @InjectView(R.id.bottomproview)
    LinearLayout bottomproview;
    @InjectView(R.id.share_title)
    TextView shareTitle;
    @InjectView(R.id.content1)
    TextView content1;
    @InjectView(R.id.xieyi1)
    RelativeLayout xieyi1;
    @InjectView(R.id.content2)
    TextView content2;
    @InjectView(R.id.xieyi2)
    RelativeLayout xieyi2;
    @InjectView(R.id.know)
    Button know;
    @InjectView(R.id.service_window)
    LinearLayout serviceWindow;

    private IunionQrMainContract.IGetBankCardInfoPresenter presenter;
    private UnionPayEntity entity;
    private DisplayMetrics metrics;

    private Subscription rxSubscription;

    @Override
    protected int getContentViewId() {
        return R.layout.unionqr_banklist_activity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("back")) {
                    finish();
                }
            }
        });

        //swipeRefreshLayout.setRefreshing(true);
        presenter.getBankCard();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        new UnionQrMainPresenter(this, this);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (getIntent().getExtras() == null || getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) == null) {
//            presenter.getBankCard();
        } else {
            entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
            initView();
        }

        if (App.protocols.size() != 0) {
            try {
                content1.setText(App.protocols.get(0).name);
                content2.setText(App.protocols.get(1).name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        xieyi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebPageActivity.class);
                intent.putExtra(WebPageActivity.EXTRA_URL, App.protocols.get(0).content);
                intent.putExtra(WebPageActivity.EXTRA_TITLE, "服务协议");
                getActivity().startActivity(intent);
                hideNotLostAlertAnim();
            }
        });

        xieyi2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebPageActivity.class);
                intent.putExtra(WebPageActivity.EXTRA_URL, App.protocols.get(1).content);
                intent.putExtra(WebPageActivity.EXTRA_TITLE, "服务协议");
                getActivity().startActivity(intent);
                hideNotLostAlertAnim();
            }
        });
        
        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideNotLostAlertAnim();
            }
        });

    }

    private void initView() {
        swipeAdapter.setMode(Attributes.Mode.Single);
        listView.setAdapter(swipeAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getBankCard();
            }
        });
    }

    private void showShareAlertAnim(final String id) {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", 0,
                halfScreen, halfScreen + 50, halfScreen + 10, halfScreen + 35, halfScreen + 30);//450 410 435 430
        animator2.setDuration(700);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);

        mShareExitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBackAlertAnim();
            }
        });
        delBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.mAxLoginSp.getUnionSelBankId().equals(id)) {
                    App.mAxLoginSp.setUnionSelBankId("");
                }
                presenter.removeBankCard(id);
            }
        });
    }

    private void hideBackAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", halfScreen + 30, 0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(EXTRA_FROMME)) {
                finish();
            } else {
                Intent i = new Intent(UnionBankCardListActivity.this, UnionPayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
                bundle.putBoolean(UnionCashierFragment.EXTRA_SHOW_UNIONCASHIER, true);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.bottomproview)
    public void proViewClick(View view) {
//        Intent intent = new Intent(this, UnionServiceProActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(UnionServiceProActivity.EXTRA_SHOW_BTN, "0");
//        intent.putExtras(bundle);
//        startActivity(intent);
        showNotLostAlertAnim();
    }

    private void setBankIcon(final String url, final ImageView imageView) {
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(url, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (Util.isEmpty(url)) {
                    imageView.setBackgroundResource(R.drawable.defaulf_circle);
                } else {
                    imageView.setImageBitmap(loadedImage);
                }

            }
        });
    }

    BaseSwipeAdapter swipeAdapter = new BaseSwipeAdapter() {
        SwipeLayout swipeLayout;

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipeLayout;
        }

        @Override
        public View generateView(final int position, ViewGroup parent) {
            View view = LayoutInflater.from(UnionBankCardListActivity.this).inflate(R.layout.unionqr_banklist_item, null);
            swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            ((TextView) view.findViewById(R.id.bank_name)).setText(entity.getBankCards().get(position).getIss_ins_name());
            ((TextView) view.findViewById(R.id.bank_type)).setText(entity.getBankCards().get(position).getCard_type_name());
            ((TextView) view.findViewById(R.id.card_no)).setText(entity.getBankCards().get(position).getCard_no());

            setBankIcon(entity.getBankCards().get(position).getIss_ins_icon(), (ImageView) view.findViewById(R.id.bank_icon));
            view.findViewById(R.id.bottom_wrapper).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShareAlertAnim(entity.getBankCards().get(position).getId());
                }
            });

            view.findViewById(R.id.mainItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(EXTRA_FROMME)) {
                        return;
                    }
                    UPBankCard bankCard = entity.getBankCards().get(position);
                    App.mAxLoginSp.setUnionSelBankId(bankCard.getId());

                    Intent i = new Intent(UnionBankCardListActivity.this, UnionPayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
                    bundle.putBoolean(UnionCashierFragment.EXTRA_SHOW_UNIONCASHIER, true);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            });

            return view;
        }

        @Override
        public void fillValues(int position, View convertView) {

        }

        @Override
        public int getCount() {
            return entity.getBankCards().size();

        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    };


    @Override
    public boolean rightButtonEnabled() {
        return true;
    }

    @Override
    public String getRightButtonText() {
        return "添加";
    }

    @Override
    public void onRightButtonClick(View view) {
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();

        if (customers.size() != 0) {
            if (((CustomerBaseInfoRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$base_info()).realmGet$protocol() != null) {
                if (((CustomerBaseInfoRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$base_info()).realmGet$protocol().size() > 0) {
//                    //存在同意了的协议
//                    presenter.JudgePayPwd();
                    Intent intent = new Intent(UnionBankCardListActivity.this, UnionHtmlActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                    intent.putExtras(bundle);
                    UnionBankCardListActivity.this.startActivity(new Intent(intent));
                } else {
                    Intent intent = new Intent(this, UnionServiceProActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(UnionServiceProActivity.EXTRA_SHOW_BTN, "1");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, UnionServiceProActivity.REQUEST_SURE_PRO);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UnionServiceProActivity.REQUEST_SURE_PRO) {
            if (resultCode == RESULT_OK) {
                presenter.JudgePayPwd();
            }
        }
    }

    @Override
    public void onLeftButtonClick(View view) {
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(EXTRA_FROMME)) {
            finish();
        } else {
            Intent i = new Intent(UnionBankCardListActivity.this, UnionPayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
            bundle.putBoolean(UnionCashierFragment.EXTRA_SHOW_UNIONCASHIER, true);
            i.putExtras(bundle);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {
        if (config.has_pay_password) {
            Intent intent = new Intent(this, UnionInputPayPwdActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, true);
            intent.putExtras(bundle);
            startActivity(new Intent(intent));
        } else {
            startActivity(new Intent(this, UnionSetPayPwdActivity.class));
        }
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
        if (bankCards.size() == 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("no_more",true);
            Intent intent =new Intent(getActivity(), UnionSweptEmptyCardActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            emptyIcon.setVisibility(View.VISIBLE);
            App.hasBankCard=false;
        } else {
            emptyIcon.setVisibility(View.GONE);
        }
        ArrayList<UPBankCard> bankCardsNew = new ArrayList<>();
        for (int i = 0; i < bankCards.size(); i++) {
            UPBankCard bankCard = new UPBankCard();
            bankCard.setId(Util.isEmpty(bankCards.get(i).getId()) ? "" : bankCards.get(i).getId());
            bankCard.setIss_ins_name(Util.isEmpty(bankCards.get(i).getIss_ins_name()) ? "" : bankCards.get(i).getIss_ins_name());
            bankCard.setCard_no(Util.isEmpty(bankCards.get(i).getCard_no()) ? "" : "**** **** **** " + bankCards.get(i).getCard_no());
            bankCard.setCard_type_name(Util.isEmpty(bankCards.get(i).getCard_type_name()) ? "" : bankCards.get(i).getCard_type_name());
            bankCard.setIss_ins_icon(Util.isEmpty(bankCards.get(i).getIss_ins_icon()) ? "" : bankCards.get(i).getIss_ins_icon());
            bankCardsNew.add(bankCard);
        }
        if (entity == null) {
            entity = new UnionPayEntity();
        } else {
            entity.getBankCards().clear();
        }
        entity.setBankCards(bankCardsNew);
        swipeRefreshLayout.setRefreshing(false);

        initView();
    }

    @Override
    public void applyBankCardResult(String addCardUrl) {

    }

    @Override
    public void removeBankCardResult() {
        Toast.makeText(this, "成功删除", Toast.LENGTH_LONG).show();
        hideBackAlertAnim();

        swipeRefreshLayout.setRefreshing(true);
        presenter.getBankCard();
    }

    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {

    }

    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {

    }

    @Override
    public void setPresenter(IunionQrMainContract.IGetBankCardInfoPresenter presenter) {
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


    private void showNotLostAlertAnim() {
        serviceWindow.setVisibility(View.VISIBLE);
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(serviceWindow, "translationY", 0, halfScreen + 50);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);
        mGrayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void hideNotLostAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(serviceWindow, "translationY", halfScreen + 50, 0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                serviceWindow.setVisibility(View.GONE);
            }
        }, 590);
    }



}

