package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.BankCardService;
import modellib.thrift.bankcard.BankCard;
import modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.me.view.adapter.BankCardAdapter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;

/**
 * Created by zcrprozcrpro on 2017/5/18.
 */

public class MeBankCardActivity extends Activity {


    @InjectView(R.id.btn_add_card)
    Button btnAddCard;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.input_back)
    ImageView inputBack;
    @InjectView(R.id.back_icon)
    RelativeLayout backIcon;
    @InjectView(R.id.top_title)
    TextView topTitle;
    @InjectView(R.id.tv_card_manger)
    TextView tvCardManger;
    @InjectView(R.id.ll)
    RelativeLayout ll;
    @InjectView(R.id.ll_del)
    RelativeLayout llDel;
    @InjectView(R.id.neePasswordView)
    RelativeLayout neePasswordView;
    @InjectView(R.id.grayBg)
    View grayBg;
    @InjectView(R.id.exit)
    TextView exit;
    @InjectView(R.id.click_errorbtn)
    Button clickErrorbtn;
    @InjectView(R.id.ed_re_password)
    EditText edRePassword;

    private BankCardAdapter adapter;
    private List<String> ids;
    private DisplayMetrics metrics;

    public List<String> supported_bank_name;

    private boolean isOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_bank_card_activity);
        ButterKnife.inject(this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if (supported_bank_name != null)
                    bundle.putStringArrayList("supported_bank_name", (ArrayList<String>) supported_bank_name);
                Intent i = new Intent(MeBankCardActivity.this, AddBankCardActivity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        ids = new ArrayList<>();
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBackAlertAnim();
                if (isOpen)
                    btnAddCard.setVisibility(View.VISIBLE);
            }
        });

        clickErrorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edRePassword.getText().toString())) {
                    Toast.makeText(MeBankCardActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    delCard(edRePassword.getText().toString().trim());
                }
            }
        });

        inputBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadingCard() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        BankCardService bankCardService = ApiFactory.getFactory().create(BankCardService.class);
        bankCardService.get_bank_cards(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final BankCardResponse bankCardResponse = new Gson().fromJson(o.toString(), BankCardResponse.class);
                        if (bankCardResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            if (bankCardResponse.bank_cards.size() > 0) {
                                if (bankCardResponse.bank_cards.size() == bankCardResponse.max_card_no) {
                                    btnAddCard.setVisibility(View.GONE);
                                    isOpen = false;
                                    adapter = new BankCardAdapter(MeBankCardActivity.this, bankCardResponse.bank_cards, R.layout.bank_card_item);
                                    recyclerview.setAdapter(adapter);
                                    adapter.getDel(new BankCardAdapter.Listener() {
                                        @Override
                                        public void getDelList(List<String> id_s) {
                                            ids.clear();
                                            ids.addAll(id_s);
                                        }
                                    });

                                    tvCardManger.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (tvCardManger.getText().toString().equals("管理")) {
                                                adapter.showDel();
                                                tvCardManger.setText("取消");
                                                llDel.setVisibility(View.VISIBLE);
                                            } else {
                                                adapter.UnshowDel();
                                                tvCardManger.setText("管理");
                                                llDel.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    adapter.notifyDataSetChanged();
                                } else {
                                    btnAddCard.setVisibility(View.VISIBLE);
                                    adapter = new BankCardAdapter(MeBankCardActivity.this, bankCardResponse.bank_cards, R.layout.bank_card_item);
                                    recyclerview.setAdapter(adapter);
                                    adapter.getDel(new BankCardAdapter.Listener() {
                                        @Override
                                        public void getDelList(List<String> id_s) {
                                            ids.clear();
                                            ids.addAll(id_s);
                                        }
                                    });

                                    tvCardManger.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (tvCardManger.getText().toString().equals("管理")) {
                                                adapter.showDel();
                                                tvCardManger.setText("取消");
                                                llDel.setVisibility(View.VISIBLE);
                                            } else {
                                                adapter.UnshowDel();
                                                tvCardManger.setText("管理");
                                                llDel.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    adapter.notifyDataSetChanged();
                                }
                                tvCardManger.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(MeBankCardActivity.this, "暂无银行卡", Toast.LENGTH_SHORT).show();
                                tvCardManger.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(MeBankCardActivity.this, "请先添加银行卡", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                tvCardManger.setVisibility(View.GONE);
                                recyclerview.setAdapter(null);
                                btnAddCard.setVisibility(View.VISIBLE);
                            }
                            if (bankCardResponse.supported_bank_name != null)
                                supported_bank_name = bankCardResponse.supported_bank_name;

                        } else {
                            tvCardManger.setVisibility(View.GONE);
                        }
                    }
                });

        llDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ids.size() == 0) {
                    Toast.makeText(MeBankCardActivity.this, "请先选择要删除的银行卡", Toast.LENGTH_SHORT).show();
                } else {
                    btnAddCard.setVisibility(View.GONE);
                    showBackAlertAnim();
                }
            }
        });
    }

    private void delCard(String login_password) {
        //进入界面获取银行卡信息
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("card_ids", ids);
        try {
            map.put("login_password", bytesToHexString(Secure.encodeMessageField(login_password.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BankCardService bankCardService = ApiFactory.getFactory().create(BankCardService.class);
        bankCardService.del_bank_cards(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {

                        tvCardManger.setText("管理");
                        llDel.setVisibility(View.GONE);
                        hideBackAlertAnim();
                        btnAddCard.setVisibility(View.VISIBLE);

                        Response baseResponse = new Gson().fromJson(o.toString(), Response.class);
                        if (baseResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            Toast.makeText(MeBankCardActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            loadingCard();
                        } else {
                            Toast.makeText(MeBankCardActivity.this, baseResponse.resp.resp_desc, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public static class BankCardResponse {
        public BaseResponse resp;
        public List<BankCard> bank_cards;
        public int max_card_no;
        public List<String> supported_bank_name;
    }

    public static class Response {
        public BaseResponse resp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingCard();
        tvCardManger.setText("管理");
        llDel.setVisibility(View.GONE);
    }

    private void showBackAlertAnim() {
        neePasswordView.setVisibility(View.VISIBLE);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(neePasswordView, "translationY", 0, (int) metrics.density * 450);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.VISIBLE);
    }

    private void hideBackAlertAnim() {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(neePasswordView, "translationY", (int) metrics.density * 450, 0);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                neePasswordView.setVisibility(View.GONE);
            }
        }, 590);
    }
}
