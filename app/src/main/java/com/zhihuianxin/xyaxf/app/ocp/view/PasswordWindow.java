package com.zhihuianxin.xyaxf.app.ocp.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.RealNameAuthStatus;
import com.axinfu.modellib.thrift.unqr.UPCoupon;
import com.axinfu.modellib.thrift.unqr.UPQROrder;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionForgetPayPwdCodeActivity;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdView;
import com.zhihuianxin.xyaxf.app.view.PasswordInputEdtView;
import com.zhihuianxin.xyaxf.app.view.UnionPayErrorDialog;

/**
 * Created by zcrpro on 2018/1/17.
 */

public class PasswordWindow extends PopupWindow implements
        KeyBoardPwdView.OnNumberClickListener, IunionPayPwdContract.IJudgePayPwd {


    private Context context;
    private View view;
    private PasswordInputEdtView passwordInputEdt;
    private KeyBoardPwdView mNkvKeyboard;
    private TextView forgetPwdTxt;
    private String pwd = "";
    private UnionPayErrorDialog payPwdErrorDialog;

    private IGetpassword iGetpassword;

    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;

    public PasswordWindow(Context context) {
        super(context);
        if (context == null) {
            return;
        }
        this.context = context;
        @SuppressLint("WrongConstant") LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");// Context.LAYOUT_INFLATER_SERVICE
        view = inflater.inflate(R.layout.key_board_paypwd_window, null);

        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.ecard_record_popwindow_anim_style);
        this.setBackgroundDrawable(null);

        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new UnionPayPwdPresenter(context, this);

        ImageView back = (ImageView) view.findViewById(R.id.back);
        passwordInputEdt = (PasswordInputEdtView) view.findViewById(R.id.edt);
        mNkvKeyboard = (KeyBoardPwdView) view.findViewById(R.id.am_nkv_keyboard);
        forgetPwdTxt = (TextView) view.findViewById(R.id.forget_pwd);

        mNkvKeyboard.setOnNumberClickListener(this);

        passwordInputEdt.setInputType(InputType.TYPE_NULL);
        passwordInputEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordInputEdt.closeKeybord();
            }
        });
        passwordInputEdt.setOnInputOverListener(new PasswordInputEdtView.onInputOverListener() {
            @Override
            public void onInputOver(String text) {
                //Toast.makeText(UnionSetPayPwdActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

        forgetPwdTxt.setVisibility(View.VISIBLE);
        forgetPwdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.no_add_card_and_pay = true;
                App.no_add_card = false;
                presenter.getRealName();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        payPwdErrorDialog = new UnionPayErrorDialog(context);
        //payPwdErrorDialog.create();
        payPwdErrorDialog.setListener(new UnionPayErrorDialog.OnPayPwdErrorListener() {
            @Override
            public void reinput() {
                for (int i = 0; i < pwd.length(); i++) {
                    passwordInputEdt.onKeyDown(67, null);
                }
                pwd = "";// 清楚密码框内容
                payPwdErrorDialog.dismiss();
            }

            @Override
            public void forgetPwd() {
                payPwdErrorDialog.dismiss();
                App.no_add_card_and_pay = true;
                App.no_add_card = false;
                presenter.getRealName();
            }

            @Override
            public void cancel() {
                for (int i = 0; i < pwd.length(); i++) {
                    passwordInputEdt.onKeyDown(67, null);
                }
                pwd = "";// 清楚密码框内容
                payPwdErrorDialog.dismiss();
            }
        });

    }


    public PasswordWindow(Context context, final boolean no_need_dericet_open_card) {
        super(context);
        if (context == null) {
            return;
        }
        this.context = context;
        @SuppressLint("WrongConstant") LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");// Context.LAYOUT_INFLATER_SERVICE
        view = inflater.inflate(R.layout.key_board_paypwd_window, null);

        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.ecard_record_popwindow_anim_style);
        this.setBackgroundDrawable(null);

        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new UnionPayPwdPresenter(context, this);

        ImageView back = (ImageView) view.findViewById(R.id.back);
        passwordInputEdt = (PasswordInputEdtView) view.findViewById(R.id.edt);
        mNkvKeyboard = (KeyBoardPwdView) view.findViewById(R.id.am_nkv_keyboard);
        forgetPwdTxt = (TextView) view.findViewById(R.id.forget_pwd);

        mNkvKeyboard.setOnNumberClickListener(this);

        passwordInputEdt.setInputType(InputType.TYPE_NULL);
        passwordInputEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordInputEdt.closeKeybord();
            }
        });
        passwordInputEdt.setOnInputOverListener(new PasswordInputEdtView.onInputOverListener() {
            @Override
            public void onInputOver(String text) {
                //Toast.makeText(UnionSetPayPwdActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

        forgetPwdTxt.setVisibility(View.VISIBLE);
        forgetPwdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getRealName();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        payPwdErrorDialog = new UnionPayErrorDialog(context);
        //payPwdErrorDialog.create();
        payPwdErrorDialog.setListener(new UnionPayErrorDialog.OnPayPwdErrorListener() {
            @Override
            public void reinput() {
                for (int i = 0; i < pwd.length(); i++) {
                    passwordInputEdt.onKeyDown(67, null);
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
                for (int i = 0; i < pwd.length(); i++) {
                    passwordInputEdt.onKeyDown(67, null);
                }
                pwd = "";// 清楚密码框内容
                payPwdErrorDialog.dismiss();
            }
        });

    }

    @Override
    public void onNumberReturn(String number) {
        passwordInputEdt.onTextChanged(number, 0, 0, 1);
        passwordInputEdt.onTextChanged("", 0, 0, 1);
        if (pwd.length() < 6) {
            pwd += number;
            if (pwd.length() == 6) {
                setPwdOk();
            }
        }
    }

    @Override
    public void onNumberDelete() {
        passwordInputEdt.onKeyDown(67, null);
        if (pwd.length() <= 1) {
            pwd = "";
        } else {
            pwd = pwd.substring(0, pwd.length() - 1);
        }
    }

    private void setPwdOk() {
        presenter.verifyPayPwd(pwd);
    }

    @Override
    public void getRealNameResult(RealName realName) {
        if (realName.status.equals(RealNameAuthStatus.OK.name())) {
            context.startActivity(new Intent(context, UnionForgetPayPwdCodeActivity.class));
        } else {
            Intent i = new Intent(context, UnionForgetPayPwdCodeActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(UnionForgetPayPwdCodeActivity.EXTRA_SHOWIMG, false);
            i.putExtras(b);
            context.startActivity(i);
        }
        dismiss();
    }

    @Override
    public void verifyPayPwdResult(boolean is_match, int left_retry_count) {
        if (is_match) {
            //如果是正确的
            if (iGetpassword != null)
                iGetpassword.getpass(pwd);
            dismiss();
        } else {
            payPwdErrorDialog.show();
            if (left_retry_count == 2) {
                payPwdErrorDialog.setBtnText("重新输入");
                payPwdErrorDialog.showErrorText("2");
            } else if (left_retry_count == 1) {
                payPwdErrorDialog.setBtnText("重新输入");
                payPwdErrorDialog.showErrorText("1");
            } else if (left_retry_count == 0) {
                payPwdErrorDialog.setBtnText("放弃支付");
                payPwdErrorDialog.showEndText();
            }
        }
    }

    @Override
    public void setPayPwdResult(BaseResponse baseResponse) {

    }

    @Override
    public void modifyPayPwdResult(int left_retry_count) {

    }

    @Override
    public void slearPayPwdResult() {

    }

    @Override
    public void payOrderResult(PaymentOrder order) {

    }

    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {

    }

    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {

    }

    @Override
    public void setPinFreeResult(boolean is_match, int left_retry_count) {

    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {

    }

    @Override
    public void setPresenter(IunionPayPwdContract.IJudgePayPwdPresenter presenter) {
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


    public interface IGetpassword {
        void getpass(String password);
    }

    public void getPassword(IGetpassword iGetpassword) {
        this.iGetpassword = iGetpassword;
    }
}
