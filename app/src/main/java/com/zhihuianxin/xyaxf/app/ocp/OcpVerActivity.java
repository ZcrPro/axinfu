package com.zhihuianxin.xyaxf.app.ocp;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.AxfQRPayService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.ocp.AxfQRPayAccount;
import com.axinfu.modellib.thrift.ocp.CredentialType;
import com.axinfu.modellib.thrift.ocp.CustomerTypes;
import com.google.gson.Gson;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiException;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/11/16.
 */

public class OcpVerActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.ed_id)
    EditText edId;
    @InjectView(R.id.ed_stu_no)
    EditText edStuNo;
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.tv_info)
    TextView tvInfo;
    @InjectView(R.id.not_me)
    TextView notMe;
    @InjectView(R.id.tv_lost_err_text)
    TextView tvLostErrText;
    @InjectView(R.id.next2)
    Button next2;
    @InjectView(R.id.diss)
    TextView diss;
    @InjectView(R.id.lostView)
    LinearLayout lostView;
    @InjectView(R.id.grayBg)
    View grayBg;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.tv_lost_err_text2)
    TextView tvLostErrText2;
    @InjectView(R.id.next3)
    Button next3;
    @InjectView(R.id.lostView2)
    LinearLayout lostView2;
    @InjectView(R.id.o)
    TextView o;
    @InjectView(R.id.select_card)
    ImageView selectCard;
    @InjectView(R.id.ll_type)
    LinearLayout llType;
    @InjectView(R.id.ll_no)
    LinearLayout llNo;
    @InjectView(R.id.rl)
    LinearLayout rl;
    @InjectView(R.id.rl_is_need_ver)
    LinearLayout rlIsNeedVer;
    @InjectView(R.id.expandable_text)
    TextView expandableText;
    @InjectView(R.id.expand_collapse)
    ImageButton expandCollapse;
    @InjectView(R.id.expand_text_view)
    ExpandableTextView expandTextView;

    private String qrId;
    private boolean isResult = false;
    private LoadingDialog loadingDialog;
    private DisplayMetrics metrics;

    private boolean isNeedVer = false;

    private QrResultActivity.PayInfo payInfo;


    /**
     * @Fields TextView 闭合展开: 默认展示最大行数3行
     */
    public static final int MAX_LINE = 3;
    /**
     * @Fields TextView 闭合展开: 收起状态
     */
    public static final int SHRINK_UP_STATE = 1;
    /**
     * @Fields TextView 闭合展开:  展开状态
     */
    public static final int SPREAD_STATE = 2;
    /**
     * @Fields mState : 默认收起状态
     */
    private static int mState = SHRINK_UP_STATE;


    @Override
    protected int getContentViewId() {
        return R.layout.ocp_ver_activity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        loadingDialog = new LoadingDialog(this);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            initSpnner();
        }

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        qrId = bundle.getString("qr");

        isResult = bundle.getBoolean("result");

        payInfo = (QrResultActivity.PayInfo) bundle.getSerializable("payinfo");

        isNeedVer = bundle.getBoolean("isNeedVer");

        if (isNeedVer) {
            rl.setVisibility(View.GONE);
            rlIsNeedVer.setVisibility(View.VISIBLE);
            StringBuilder content = new StringBuilder();
            if (payInfo.supported_cust_types != null) {
                for (int i = 0; i < payInfo.supported_cust_types.size(); i++) {
                    if (i==payInfo.supported_cust_types.size()-1){
                        content.append(payInfo.supported_cust_types.get(i).type_name);
                    }else {
                        content.append(payInfo.supported_cust_types.get(i).type_name+"、");
                    }
                }
                expandTextView.setText(stringFilter(ToDBC("该商户仅支持认证的: " + content + "进行消费")));
            }
        }

        //如果证件号全都没有
        if (App.ocpAccount.account.supported_credential_type == null || App.ocpAccount.account.supported_credential_type.size() == 0) {
            llType.setVisibility(View.GONE);
            llNo.setVisibility(View.GONE);
        }

        //查询一卡通或者缴费是否开通 直接填写身份证信息
        if (App.ocpAccount.account.name != null && !TextUtils.isEmpty(App.ocpAccount.account.name)) {
            edName.setText(App.ocpAccount.account.name);
            notMe.setVisibility(View.VISIBLE);
            if (App.ocpAccount.account.name_verified) {
                edName.setEnabled(false);
            }
        }

        if (App.ocpAccount.account.student_no != null && !TextUtils.isEmpty(App.ocpAccount.account.student_no)) {
            edStuNo.setText(App.ocpAccount.account.student_no);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edName.getText().toString())) {
                    Toast.makeText(OcpVerActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edStuNo.getText().toString())) {
                    Toast.makeText(OcpVerActivity.this, "请输入学号", Toast.LENGTH_SHORT).show();
                } else if (llType.getVisibility() == View.VISIBLE && TextUtils.isEmpty(edId.getText().toString())) {
                    Toast.makeText(OcpVerActivity.this, "请输入" + tvInfo.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                } else {
                    if (App.ocpAccount != null && App.ocpAccount.account.supported_credential_type != null & App.ocpAccount.account.supported_credential_type.size() > 0) {

                        for (int i = 0; i < App.ocpAccount.account.supported_credential_type.size(); i++) {
                            if (tvInfo.getText().toString().trim().equals(App.ocpAccount.account.supported_credential_type.get(i).title)) {
                                CredentialInfo credentialInfo = new CredentialInfo();
                                credentialInfo.type_code = App.ocpAccount.account.supported_credential_type.get(i).verify_type_code;
                                if (App.ocpAccount != null && !TextUtils.isEmpty(App.ocpAccount.account.credential_no)) {
                                    credentialInfo.account = App.ocpAccount.account.credential_no;
                                } else {
                                    credentialInfo.account = edId.getText().toString().trim();
                                }
                                open_account(edName.getText().toString().trim(), edStuNo.getText().toString().trim(), credentialInfo, null);
                            }
                        }
                    } else {
                        //没有配置证件
                        open_account(edName.getText().toString().trim(), edStuNo.getText().toString().trim(), null);
                    }
                }
            }
        });

        notMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLostAlertAnim();
            }
        });

    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initSpnner() {
        if (App.ocpAccount != null && App.ocpAccount.account.supported_credential_type != null & App.ocpAccount.account.supported_credential_type.size() > 0) {

            if (App.ocpAccount.account.credential_type != null && App.ocpAccount.account.credential_no != null) {
                for (int i = 0; i < App.ocpAccount.account.supported_credential_type.size(); i++) {
                    if (App.ocpAccount.account.supported_credential_type.get(i).verify_type_code.equals(App.ocpAccount.account.credential_type)) {
                        tvInfo.setText(App.ocpAccount.account.supported_credential_type.get(i).title);
                        edId.setText(getIDValue(App.ocpAccount.account.credential_no));
                    }
                }

                if (App.ocpAccount.account.id_card_verified) {
                    edId.setEnabled(false);
                    tvInfo.setEnabled(false);
                }
            } else {
                tvInfo.setText(App.ocpAccount.account.supported_credential_type.get(0).title);
            }

            if (App.ocpAccount != null && App.ocpAccount.account.supported_credential_type != null & App.ocpAccount.account.supported_credential_type.size() == 1) {
                tvInfo.setBackground(getResources().getDrawable(R.drawable.payment_not_check_edittext));
                tvInfo.setCompoundDrawables(null, null, null, null);
            } else {
                tvInfo.setBackground(getResources().getDrawable(R.drawable.input_phone_s));
                selectCard.setVisibility(View.VISIBLE);


                Collections.reverse(App.ocpAccount.account.supported_credential_type);
                final OcpVerDialog ocpVerDialog = new OcpVerDialog(OcpVerActivity.this, App.ocpAccount.account.supported_credential_type);


                tvInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ocpVerDialog.show();
                        ocpVerDialog.setOnNextListener(new OcpVerDialog.OnNextListener() {
                            @Override
                            public void onNext(CredentialType credentialType) {
                                tvInfo.setText(credentialType.title);
                            }
                        });
                    }
                });
            }
        }
    }

    /**
     * 提交一码通身份验证
     */
    private void open_account(String name, final String student_no, CredentialInfo credentialInfo, String cust_type_id) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("student_no", student_no);
        map.put("credential_info", credentialInfo);
        if (cust_type_id != null)
            map.put("cust_type_id", cust_type_id);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.open_account(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, false, null) {
                    @Override
                    public void onNext(Object o) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        OpenResp openResp = new Gson().fromJson(o.toString(), OpenResp.class);
                        if (openResp.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            App.ocpAccount.account = openResp.account;
                            if (!TextUtils.isEmpty(qrId)) {
                                acquiring(qrId);
                            } else {
                                //开通成功
                                Toast.makeText(OcpVerActivity.this, "开通成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (e.toString().contains(AppConstant.NEED_TYPES)) {
                            get_customer_types(student_no);
                        } else if (e instanceof ApiException) {
                            ApiException exception = (ApiException) e;
                            if (exception.mErrorCode == AppConstant.RELOGIN) {
                                mContext.sendBroadcast(new Intent(MainActivity.RELOGIN_BROADCAST));
                                ((Activity) mContext).finish();
                            }else {
                                showLostAlertAnim2(e.getMessage());
                            }
                        } else {
                            showLostAlertAnim2(e.getMessage());
                        }
                    }
                });
    }

    /**
     * 提交一码通身份验证 --无身份验证
     */
    private void open_account(String name, final String student_no, String cust_type_id) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("student_no", student_no);
        if (cust_type_id != null)
            map.put("cust_type_id", cust_type_id);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.open_account(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpVerActivity.this, false, null) {
                    @Override
                    public void onNext(Object o) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        OpenResp openResp = new Gson().fromJson(o.toString(), OpenResp.class);
                        if (openResp.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            App.ocpAccount.account = openResp.account;
                            if (!TextUtils.isEmpty(qrId)) {
                                acquiring(qrId);
                            } else {
                                //开通成功
                                Toast.makeText(OcpVerActivity.this, "开通成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (e.toString().contains(AppConstant.NEED_TYPES)) {
                            get_customer_types(student_no);
                        } else if (e instanceof ApiException) {
                            ApiException exception = (ApiException) e;
                            if (exception.mErrorCode == AppConstant.RELOGIN) {
                                mContext.sendBroadcast(new Intent(MainActivity.RELOGIN_BROADCAST));
                                ((Activity) mContext).finish();
                            }else {
                                showLostAlertAnim2(e.getMessage());
                            }
                        } else {
                            showLostAlertAnim2(e.getMessage());
                        }
                    }
                });
    }

    private void get_customer_types(String student_no) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("student_no", student_no);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.get_customer_types(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        UserTypesResp userTypesResp = new Gson().fromJson(o.toString(), UserTypesResp.class);
                        if (userTypesResp.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //如果成功
                            final OcpUserTypeDialog ocpUserTypeDialog = new OcpUserTypeDialog(OcpVerActivity.this, userTypesResp.customer_types);
                            ocpUserTypeDialog.show();
                            ocpUserTypeDialog.setOnNextListener(new OcpUserTypeDialog.OnNextListener() {
                                @Override
                                public void onNext(final CustomerTypes customerTypes) {
                                    //点击了之后
                                    final OcpVerifyDialog ocpVerifyDialog = new OcpVerifyDialog(OcpVerActivity.this, customerTypes);
                                    ocpVerifyDialog.show();
                                    ocpVerifyDialog.setListener(new OcpVerifyDialog.selectItem() {
                                        @Override
                                        public void commit() {

                                            if (ocpVerifyDialog != null)
                                                ocpVerifyDialog.dismiss();

                                            if (ocpUserTypeDialog != null)
                                                ocpUserTypeDialog.dismiss();

                                            if (TextUtils.isEmpty(edName.getText().toString())) {
                                                Toast.makeText(OcpVerActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(edStuNo.getText().toString())) {
                                                Toast.makeText(OcpVerActivity.this, "请输入学号", Toast.LENGTH_SHORT).show();
                                            } else if (llType.getVisibility() == View.VISIBLE && TextUtils.isEmpty(edId.getText().toString())) {
                                                Toast.makeText(OcpVerActivity.this, "请输入" + tvInfo.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (App.ocpAccount != null && App.ocpAccount.account.supported_credential_type != null & App.ocpAccount.account.supported_credential_type.size() > 0) {
                                                    for (int i = 0; i < App.ocpAccount.account.supported_credential_type.size(); i++) {
                                                        if (tvInfo.getText().toString().trim().equals(App.ocpAccount.account.supported_credential_type.get(i).title)) {
                                                            CredentialInfo credentialInfo = new CredentialInfo();
                                                            credentialInfo.type_code = App.ocpAccount.account.supported_credential_type.get(i).verify_type_code;
                                                            if (App.ocpAccount != null && !TextUtils.isEmpty(App.ocpAccount.account.credential_no)) {
                                                                credentialInfo.account = App.ocpAccount.account.credential_no;
                                                            } else {
                                                                credentialInfo.account = edId.getText().toString().trim();
                                                            }
                                                            open_account(edName.getText().toString().trim(), edStuNo.getText().toString().trim(), credentialInfo, customerTypes.id);
                                                        }
                                                    }
                                                } else {
                                                    //没有配置证件
                                                    open_account(edName.getText().toString().trim(), edStuNo.getText().toString().trim(), customerTypes.id);
                                                }
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
    }

    public class CredentialInfo {
        public String type_code;                         // 证件类型
        public String account;                              // 账号

        @Override
        public String toString() {
            return "CredentialInfo{" +
                    "type_code='" + type_code + '\'' +
                    ", account='" + account + '\'' +
                    '}';
        }
    }

    /**
     * 收单
     */
    private void acquiring(final String qrId) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("qr", qrId);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.acquiring(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpVerActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        QrResultActivity.PayInfoResp payInfoResp = new Gson().fromJson(o.toString(), QrResultActivity.PayInfoResp.class);

                        if (!isResult) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("payinfo", (Serializable) payInfoResp.pay_info);
                            bundle.putString("qr", qrId);
                            if (payInfoResp.pay_info.payee_info.amounts.size() > 0) {
                                bundle.putBoolean(OcpPayFixedDeskActivity.FIXED, true);
                            } else {
                                bundle.putBoolean(OcpPayFixedDeskActivity.FIXED, false);
                            }
                            Intent intent = new Intent(OcpVerActivity.this, OcpPayFixedDeskActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            RxBus.getDefault().send("open");
                            finish();
                        }
                    }
                });
    }

    public class OpenResp {
        public AxfQRPayAccount account;
        public BaseResponse resp;
    }

    public class UserTypesResp {
        public List<CustomerTypes> customer_types;
        public BaseResponse resp;
    }

    private String getIDValue(String oriID) {
        if (oriID != null) {
            if (oriID.length() != 18) {
                return oriID;
            } else {
                return oriID.substring(0, 2) + "**************" + oriID.substring(16);
            }
        } else {
            return null;
        }
    }


    private void showLostAlertAnim() {
        lostView.setVisibility(View.VISIBLE);
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(lostView, "translationY", 0, halfScreen + 50);
        animator2.setDuration(600);
        animator2.start();
        next2.setVisibility(View.VISIBLE);
        diss.setVisibility(View.VISIBLE);
        diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLostAlertAnim();
            }
        });
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call("400-028-1024");
            }
        });

        grayBg.setVisibility(View.VISIBLE);
        grayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void showLostAlertAnim2(String err) {
        lostView2.setVisibility(View.VISIBLE);
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(lostView2, "translationY", 0, halfScreen + 50);
        animator2.setDuration(600);
        animator2.start();
        next3.setVisibility(View.VISIBLE);
        next3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLostAlertAnim2();
            }
        });

        text.setText(err);

        grayBg.setVisibility(View.VISIBLE);
        grayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void hideLostAlertAnim2() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(lostView2, "translationY", halfScreen + 50, 0);
        animator2.setDuration(600);
        animator2.start();
        next3.setVisibility(View.GONE);
        grayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lostView2.setVisibility(View.GONE);
            }
        }, 590);
    }

    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        hideLostAlertAnim();
    }

    private void hideLostAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(lostView, "translationY", halfScreen + 50, 0);
        animator2.setDuration(600);
        animator2.start();
        next2.setVisibility(View.GONE);
        diss.setVisibility(View.GONE);
        grayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lostView.setVisibility(View.GONE);
            }
        }, 590);
    }
}
