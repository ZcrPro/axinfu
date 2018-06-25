package com.zhihuianxin.xyaxf.app.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.axinfu.modellib.thrift.base.PayMethod;
import com.xyaxf.axpay.Util;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.xyaxf.axpay.persistence.AbsSharedPreferencesData;
import com.xyaxf.axpay.persistence.Persist;

/**
 * Created by John on 2014/12/11.
 */
public class MyPayActivity extends PayActivity {

	public static final String EXTRA_AMOUNT = "amount";
	private static final int REQUEST_SHOW_PAY_RESULT = 5001;

	private boolean mPaySuccess;
	private PayErrorNoticeCfg mPayErrorNoticeCfg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPayErrorNoticeCfg = new PayErrorNoticeCfg(this);
		mPayErrorNoticeCfg.load();
	}

	@Override
	protected void pay(PayMethod channel, String tn) {
		PaymentInfo paymentInfo = getPaymentInfo();
		String amount = getIntent().getStringExtra(EXTRA_AMOUNT);
		// check amount and pay amount
		if(Util.isEnabled(amount) && paymentInfo != null && Util.isEnabled(paymentInfo.payment_amt)){
			if(!Util.formatAmount(amount, 2).equals(Util.formatAmount(paymentInfo.payment_amt, 2))){
				Util.showToastLong(this, "订单信息有误, 不能进行支付");
				finish();
				return;
			}
		}

		super.pay(channel,tn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_SHOW_PAY_RESULT){
			setResult();
			finish();
		}
		else{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onPayResult(final PaymentInfo info, final boolean success) {
		if(success){
			mPaySuccess = success;

			showPayResult(info);

			mPayErrorNoticeCfg.upPaySuccessBefore = true;
			mPayErrorNoticeCfg.save();
		}
		else{
			if(mPayErrorNoticeCfg.notNoticeAgain || mPayErrorNoticeCfg.upPaySuccessBefore){
				super.onPayResult(info, success);
			}
			else{
//				SimpleDialog dlg = new SimpleDialog(this);
//				dlg.setTitle("支付遇到困难? ");
//				dlg.setMessage("让我来帮助你吧! ");
//				dlg.setPositiveButton("去看看", new SimpleDialog.OnButtonClickListener() {
//					@Override
//					public boolean onClick(View view) {
//						Intent intent = new Intent(getActivity(), UnionPaySupportedBankActivity.class);
//						startActivity(intent);
//
//						return true;
//					}
//				});
//				dlg.setNegativeButton("不再提醒", new SimpleDialog.OnButtonClickListener() {
//					@Override
//					public boolean onClick(View view) {
//						mPayErrorNoticeCfg.notNoticeAgain = true;
//						mPayErrorNoticeCfg.save();
//
//						return true;
//					}
//				});
//				dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
//					@Override
//					public void onDismiss(DialogInterface dialog) {
//						MyPayActivity.super.onPayResult(info, success);
//					}
//				});
//				dlg.show();
			}
		}
	}

	@Override
	public void onPayCancelled(final PaymentInfo info) {
		if(mPayErrorNoticeCfg.notNoticeAgain || mPayErrorNoticeCfg.upPaySuccessBefore){
			super.onPayCancelled(info);
		}
		else{
//			SimpleDialog dlg = new SimpleDialog(this);
//			dlg.setTitle("支付遇到困难? ");
//			dlg.setMessage("让我来帮助你吧! ");
//			dlg.setPositiveButton("去看看", new SimpleDialog.OnButtonClickListener() {
//				@Override
//				public boolean onClick(View view) {
//					Intent intent = new Intent(getActivity(), UnionPaySupportedBankActivity.class);
//					startActivity(intent);
//
//					return true;
//				}
//			});
//			dlg.setNegativeButton("不再提醒", new SimpleDialog.OnButtonClickListener() {
//				@Override
//				public boolean onClick(View view) {
//					mPayErrorNoticeCfg.notNoticeAgain = true;
//					mPayErrorNoticeCfg.save();
//
//					return true;
//				}
//			});
//			dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
//				@Override
//				public void onDismiss(DialogInterface dialog) {
//					MyPayActivity.super.onPayCancelled(info);
//				}
//			});
//			dlg.show();
		}
	}

	private void showPayResult(PaymentInfo info){

	}

	private PayType getPayTypeByPayChannel(PayMethod channel){
		if(channel != null){
			return PayType.valueOf(channel.toString());
		}
		else{
			return PayType.none;
		}
	}

	public static class PayErrorNoticeCfg extends AbsSharedPreferencesData {
		@Persist
		public boolean upPaySuccessBefore = false;
		@Persist
		public boolean notNoticeAgain = false;

		public PayErrorNoticeCfg(Context context) {
			super(context);
		}

		@Override
		public String getName() {
			return "pay_error_notice_cfg";
		}
	}
}
