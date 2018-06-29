package com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.base.PayChannel;
import modellib.thrift.base.PayMethod;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.RealNameAuthStatus;
import modellib.thrift.unqr.UPBankCard;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.UPQRPayRequestData;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.OcpPaySucActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionForgetPayPwdCodeActivity;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdView;
import com.zhihuianxin.xyaxf.app.view.PasswordInputEdtView;
import com.zhihuianxin.xyaxf.app.view.UnionPayErrorDialog;
import com.zhihuianxin.xyaxf.app.view.UnionpayOrderErrorDialog;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;
import static com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment.EXTRA_ENTITY;

/**
 * Created by Vincent on 2017/11/9.
 */

public class UnionCashierInputPwdFragment extends BaseRealmFragment implements
        KeyBoardPwdView.OnNumberClickListener,
        IunionPayPwdContract.IJudgePayPwd,
        View.OnTouchListener{

    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;
    private View mBackView;
    private PasswordInputEdtView passwordInputEdt;
    private String pwd = "";
    private KeyBoardPwdView mNkvKeyboard;
    private UnionPayErrorDialog payPwdErrorDialog;
    private UnionpayOrderErrorDialog payOrderErrorDialog;
    private TextView mForgetPwdText;
    private UnionPayEntity entity;
    private String oriAmtInOldEntity = "";

    @Override
    protected int getLayoutId() {
        return R.layout.key_board_paypwd;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void getRealNameResult(RealName realName) {
        if(realName.status.equals(RealNameAuthStatus.OK.name())){
            startActivity(new Intent(getActivity(), UnionForgetPayPwdCodeActivity.class));
        } else{
            Intent i = new Intent(getActivity(), UnionForgetPayPwdCodeActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(UnionForgetPayPwdCodeActivity.EXTRA_SHOWIMG,false);
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hintKbTwo();
    }

    @Override
    public void setPresenter(IunionPayPwdContract.IJudgePayPwdPresenter presenter) {
        this.presenter = presenter;
    }

    public interface IputPwdInterface {
        void putPwdBack();
        void showMainView();
    }

    public IputPwdInterface iputPwdInterface;
    public void setIputPwdInterface(IputPwdInterface iputPwdInterface){
        this.iputPwdInterface = iputPwdInterface;
    }

    public static Fragment newInstance(UnionPayEntity entity) {
        UnionCashierInputPwdFragment fragment = new UnionCashierInputPwdFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ENTITY,entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        new UnionPayPwdPresenter(getActivity(),this);

        payOrderErrorDialog = new UnionpayOrderErrorDialog(getActivity());
        //payOrderErrorDialog.create();
        entity = (UnionPayEntity) getArguments().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        oriAmtInOldEntity = entity.getUpqrOrder().amt;
        payPwdErrorDialog = new UnionPayErrorDialog(getActivity());
        //payPwdErrorDialog.create();
        payPwdErrorDialog.setListener(new UnionPayErrorDialog.OnPayPwdErrorListener() {
            @Override
            public void reinput() {
                for(int i = 0;i < pwd.length();i++){
                    passwordInputEdt.onKeyDown(67,null);
                }
                pwd = "";// 清楚密码框内容
                payPwdErrorDialog.dismiss();
            }

            @Override
            public void forgetPwd() {
                payPwdErrorDialog.dismiss();
                presenter.getRealName();
            }

            @Override
            public void cancel() {
                for(int i = 0;i < pwd.length();i++){
                    passwordInputEdt.onKeyDown(67,null);
                }
                pwd = "";// 清楚密码框内容
                payPwdErrorDialog.dismiss();
                iputPwdInterface.putPwdBack();
            }
        });
        payOrderErrorDialog.setOnPayOrderErrorListener(new UnionpayOrderErrorDialog.OnPayOrderErrorListener() {
            @Override
            public void canel() {
                payOrderErrorDialog.dismiss();
                iputPwdInterface.showMainView();
            }

            @Override
            public void changeOtherCard() {
                payOrderErrorDialog.dismiss();
                iputPwdInterface.putPwdBack();
            }
        });
        mForgetPwdText = (TextView) view.findViewById(R.id.forget_pwd);
        mNkvKeyboard = (KeyBoardPwdView) view.findViewById(R.id.am_nkv_keyboard);
        mNkvKeyboard.setOnNumberClickListener(this);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        passwordInputEdt = (PasswordInputEdtView) view.findViewById(R.id.edt);
        passwordInputEdt.setFocusable(true);
        passwordInputEdt.setFocusableInTouchMode(true);
        passwordInputEdt.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hintKbTwo();
                passwordInputEdt.closeKeybord();
            }
        });

        passwordInputEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKbTwo();
                passwordInputEdt.closeKeybord();
            }
        });
        passwordInputEdt.setOnInputOverListener(new PasswordInputEdtView.onInputOverListener() {
            @Override
            public void onInputOver(String text) {
                //Toast.makeText(UnionSetPayPwdActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

        mBackView = view.findViewById(R.id.backView);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iputPwdInterface.putPwdBack();
            }
        });
        mForgetPwdText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.no_add_card_and_pay = true;
                App.no_add_card = false;
                presenter.getRealName();
            }
        });

        view.setOnTouchListener(this);
    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive() && getActivity().getCurrentFocus() != null) {
                    if (getActivity().getCurrentFocus().getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        }, 0);
    }

    @Override
    public void onNumberReturn(String number) {
        passwordInputEdt.onTextChanged(number,0,0,1);
        passwordInputEdt.onTextChanged("",0,0,1);
        if(pwd.length() < 6){
            pwd += number;
            if(pwd.length() == 6){
                setPwdOk(pwd);
            }
        }
    }

    @Override
    public void onNumberDelete() {
        passwordInputEdt.onKeyDown(67,null);
        if (pwd.length() <= 1) {
            pwd = "";
        } else {
            pwd = pwd.substring(0, pwd.length() - 1);
        }
    }

    private void payOrder(){
        DecimalFormat decimalFormat;
        PayRequest payRequest = new PayRequest();
        payRequest.pay_for = PayFor.UPQRPay;
        payRequest.amount = entity.getUpqrOrder().amt;
        payRequest.pay_method = new PayMethod();
        payRequest.pay_method.channel = PayChannel.UPQRPay.name();
        UPQRPayRequestData requestData = new UPQRPayRequestData();
        requestData.tn = entity.getUpqrOrder().tn;
        requestData.orig_amt = entity.getUpqrOrder().amt;
        if(entity.getUpCoupon() != null && !Util.isEmpty(entity.getUpCoupon().offst_amt)){
            decimalFormat = new DecimalFormat(".00");
            requestData.amt = decimalFormat.format(Double.parseDouble(entity.getUpqrOrder().amt) - Double.parseDouble(entity.getUpCoupon().offst_amt));
        } else{
            requestData.amt = entity.getUpqrOrder().amt;
        }
        requestData.bank_card_id = getSelectedBank();
        ArrayList<UPCoupon> coupons = new ArrayList<>();
        if(entity.getUpCoupon() != null){
            coupons.add(entity.getUpCoupon());
        }
        requestData.coupons = coupons;
        requestData.payer_comments = App.mAxLoginSp.getUnionReMark();
        requestData.qr_code = App.mAxLoginSp.getUnionQrCode();
        payRequest.request_data = requestData;


        UPBankCard upBankCard = entity.getBankCards().get(0);
        for(int i = 0;i < entity.getBankCards().size();i++){
            if(App.mAxLoginSp.getUnionSelBankId().equals(entity.getBankCards().get(i).getId())){
                upBankCard = entity.getBankCards().get(i);
            }
        }
        payRequest.pay_method.card = new modellib.thrift.unqr.UPBankCard();
        payRequest.pay_method.card.setCard_no(upBankCard.getCard_no());
        payRequest.pay_method.card.setIss_ins_name(upBankCard.getIss_ins_name());
        payRequest.pay_method.card.setIss_ins_icon(upBankCard.getIss_ins_icon());
        payRequest.pay_method.card.setIss_ins_code(upBankCard.getIss_ins_code());
        payRequest.pay_method.card.setId(upBankCard.getId());
        payRequest.pay_method.card.setCard_type_name(upBankCard.getCard_type_name());

        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
        upqrQuickPayChannelRequest.bank_card_code = getSelectedBank();
        try {
            upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField(pwd.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        payRequest.channel_request_data = upqrQuickPayChannelRequest;

        presenter.payOrder(payRequest);
    }

    private String getSelectedBank(){
        String result = "";
        for(int i = 0;i < entity.getBankCards().size();i++){
            if(entity.getBankCards().get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())){
                result = entity.getBankCards().get(i).getId();
            }
        }
        if(Util.isEmpty(result)){
            result = entity.getBankCards().get(0).getId();
        }
        return result;
    }

    private void setPwdOk(String content) {
        presenter.verifyPayPwd(content);
    }

    @Override
    public void verifyPayPwdResult(boolean is_match,int left_retry_count) {
        if (is_match){
            if(App.mAxLoginSp.getReGetUPQR().equals("1")){
                // 支付失败
                presenter.getOrderInfo(App.mAxLoginSp.getUnionQrCode());
            } else{
                payOrder();
            }
        } else{
            payPwdErrorDialog.show();
            if(left_retry_count == 2){
                payPwdErrorDialog.setBtnText("重新输入");
                payPwdErrorDialog.showErrorText("2");
            } else if(left_retry_count == 1){
                payPwdErrorDialog.setBtnText("重新输入");
                payPwdErrorDialog.showErrorText("1");
            } else if(left_retry_count == 0){
                payPwdErrorDialog.setBtnText("取消");
                payPwdErrorDialog.showEndText();
            }
        }
    }

    @Override
    public void payOrderResult(PaymentOrder order) {
        if(Util.isEmpty(order.error_msg)){
//            Intent i = new Intent(getActivity(), UnionPAyResultActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
//            bundle.putSerializable(UnionPAyResultActivity.EXTRA_UPQR_PAYORDER,order);
//            i.putExtras(bundle);
//            startActivity(i);

            Bundle bundle = new Bundle();
            bundle.putSerializable("PaymentOrder",order);
            bundle.putSerializable("pay_name",entity.getUpqrOrder().payee_info.name);
            Intent intent = new Intent(getActivity(), OcpPaySucActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

            getActivity().finish();
        } else{
            App.mAxLoginSp.setReGetUPQR("1");
            if(order.error_msg.contains(AppConstant.UPQR_PAY_PWD_ERROR)){
                payOrderErrorDialog.show();
                payOrderErrorDialog.setText(order.error_msg);
            } else{
                // 其他情况在底层类（basesubscriber）进行toast提示
            }
        }
    }

    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {
        entity.setUpqrOrder(upqrOrder);
        if(Util.isEmpty(entity.getUpqrOrder().amt)){// 不定额的订单内无金额
            entity.getUpqrOrder().amt = oriAmtInOldEntity;
        }
        presenter.getUpQrCoupon(entity.getUpqrOrder().tn,entity.getUpqrOrder().amt,getSelectedBank(),entity.getUpqrOrder().payee_info);
    }
    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {
        entity.setUpCoupon(upCoupon);
        payOrder();
    }

    @Override
    public void setPinFreeResult(boolean is_match, int left_retry_count) {}
    @Override
    public void judgePayPwdResult(PaymentConfig config) {}
    @Override
    public void setPayPwdResult(BaseResponse baseResponse) {}
    @Override
    public void modifyPayPwdResult(int left_retry_count) {}
    @Override
    public void slearPayPwdResult() {}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) { }
    @Override
    public void loadComplete() {}

    public class UPQRQuickPayChannelRequest {
        public String pay_password;        // 支付密码, aes128加密
        public String bank_card_code;
    }
}
