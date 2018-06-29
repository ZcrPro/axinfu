package com.zhihuianxin.xyaxf.app.pay.guiyang;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.LoanService;
import modellib.thrift.app.PluginInfo;
import modellib.thrift.app.Update;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import com.google.gson.Gson;
import com.xyaxf.axpay.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.me.contract.IMeCheckUpdateContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeCheckUpdatePresenter;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ErrorActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ProcessingActivity;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.service.DownloadAPKService;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.DownloadGysdkDialog;
import com.zhihuianxin.xyaxf.app.view.DownloadGysdkProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/11/13.
 */

public class AutonymSuccActivity extends BaseRealmActionBarActivity implements IMeCheckUpdateContract.IMeCheckUpdateView{
    public static final int REQUEST_GR_OPEN = 1001;

    @InjectView(R.id.btn_ok)
    Button btnOk;

    private String name;
    private String idCard;
    private float amount;
    private OpenAccountResponse openAccountResponse;
    private IMeCheckUpdateContract.IMeCheckUpdatePresenter presenter;
    private DownloadGysdkDialog downloadGysdkDialog = null;
    private DownloadGysdkProgressDialog progressDialog = null;

    @Override
    protected int getContentViewId() {
        return R.layout.autonym_succ_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getBundle();
        presenter = new MeCheckUpdatePresenter(this,this);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkApprovalInfo(name,idCard,amount);
            }
        });
    }

    private void initDialog(){
        if(downloadGysdkDialog == null){
            downloadGysdkDialog = new DownloadGysdkDialog(this);
        }
        if(progressDialog == null){
            progressDialog = new DownloadGysdkProgressDialog(this);
        }
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getString("name") != null && !TextUtils.isEmpty(bundle.getString("name"))) {
            this.name = bundle.getString("name");
        }

        if (bundle.getString("idCard") != null && !TextUtils.isEmpty(bundle.getString("idCard"))) {
            this.idCard = bundle.getString("idCard");
        }

        if (bundle.getFloat("amount") != 0) {
            this.amount = bundle.getFloat("amount");
        }
    }

    /**
     * 通知金融服务端--开户
     */
    private void GuiyangPay(final String idCard) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type","GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.apply_open_account(NetUtils.getRequestParams(AutonymSuccActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(AutonymSuccActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(AutonymSuccActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        openAccountResponse = new Gson().fromJson(o.toString(), OpenAccountResponse.class);
                        if (openAccountResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
//                            Gysdk.open(AutonymSuccActivity.this, idCard, getPackageName(), openAccountResponse.credit_info.serial_no, "0", new GyAccountCallback() {
//                                @Override
//                                public void result(final String s) {
//                                    Toast.makeText(AutonymSuccActivity.this, "开户成功", Toast.LENGTH_SHORT).show();
//                                    guiyangPayResult(openAccountResponse.credit_info.serial_no);
//                                    finish();
//                                }
//                            });
                            Intent it = new Intent(CashierDeskActivity.GYSDK_PACKAGE_ACTIVITY_NAME);
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "open");
                            bundle.putString("idCard", idCard);
                            bundle.putString("tn", openAccountResponse.credit_info.serial_no);
                            it.putExtras(bundle);
                            try{
                                startActivityForResult(it, REQUEST_GR_OPEN);
                            } catch (Exception e){
                                PluginInfo pluginInfo = new PluginInfo();
                                pluginInfo.package_name = CashierDeskActivity.GYSDK_PACKAGE_NAME;
                                pluginInfo.version = "1.0.0";
                                ArrayList<PluginInfo> arrayList = new ArrayList<>();
                                arrayList.add(pluginInfo);
                                presenter.checkUpdate(arrayList);
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void checkUpdateSuccess(Update update, ArrayList<Update> plugin_updates) {
        String url = null;
        for (Update item : plugin_updates){
            if(!item.update_type.equals("None") &&
                    item.name.equals(CashierDeskActivity.GYSDK_PACKAGE_NAME) &&
                    !Util.isEmpty(item.update_url)){
                url = item.update_url;

                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }

        if(Util.isEmpty(url)){
            Toast.makeText(getActivity(),  "下载失败", Toast.LENGTH_LONG).show();
        } else{
            initDialog();
            if(App.mAxLoginSp.getGysdkDone() || DownloadAPKService.isRunning()){
                // 下载完成；正在下载.
                downloadGysdkDialog.gotoService();
                progressDialog.setStart();
            }else {
                downloadGysdkDialog.show();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                String url = msg.getData().getString("url");
                App.mAxLoginSp.setGysdkUrl(url);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(data != null && requestCode == REQUEST_GR_OPEN && data.getExtras() != null){
                RxBus.getDefault().send("fixed_activity_add_bank_def");
                Toast.makeText(AutonymSuccActivity.this, "开户成功", Toast.LENGTH_SHORT).show();
                guiyangPayResult(openAccountResponse.credit_info.serial_no);
                finish();
            }
        }
    }

    /**
     * 开户成功通知
     *
     * @param serial_no
     */
    private void guiyangPayResult(String serial_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("serial_no", serial_no);
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.open_account_success_notify(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, false, null) {

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {}
    @Override
    public void getRealNameResult(RealName realName) {}
    @Override
    public void setPresenter(IMeCheckUpdateContract.IMeCheckUpdatePresenter presenter) {
        this.presenter = presenter;
    }
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}

    /**
     * 开户
     */
    public static class OpenAccountResponse extends RealmObject {
        public BaseResponse resp;
        public CreditInfo credit_info;
    }

    public class CreditInfo {
        public String id_card_no;
        public String name;
        public String serial_no;
        public String loan_way_type;
    }

    private void checkApprovalInfo(final String name, final String idCard, final float amount) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.check_pre_approval(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final ApprovalResponse approvalResponse = new Gson().fromJson(o.toString(),ApprovalResponse.class);
                        if (approvalResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //判断预授信信息
                            if (approvalResponse.status.equals("Success")) {
                                GuiyangPay(idCard);
                            } else if (approvalResponse.status.equals("Processing")) {
                                AutonymSuccActivity.this.startActivity(new Intent(AutonymSuccActivity.this, ProcessingActivity.class));
                                finish();
                            } else if (approvalResponse.status.equals("Error")) {
                                AutonymSuccActivity.this.startActivity(new Intent(AutonymSuccActivity.this, ErrorActivity.class));
                                finish();
                            } else if (approvalResponse.status.equals("AccountNotExist")) {
                                //进入预售信息补全界面
                                AutonymSuccActivity.this.startActivity(new Intent(AutonymSuccActivity.this, PreApprovalActivity.class));
                                finish();
                            } else if (approvalResponse.status.equals("RealNameAuthError")) {
                                //实名认证失败
                                AutonymSuccActivity.this.startActivity(new Intent(AutonymSuccActivity.this, ErrorActivity.class));
                                finish();
                            } else {

                            }
                        }
                    }
                });
    }

    public static class ApprovalResponse extends RealmObject {
        public BaseResponse resp;
        public String status;
    }
}
