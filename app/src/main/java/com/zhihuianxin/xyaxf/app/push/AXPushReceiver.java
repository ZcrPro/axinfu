package com.zhihuianxin.xyaxf.app.push;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.message.ImportantMessage;
import com.axinfu.modellib.thrift.message.PricingStrategy;
import com.axinfu.modellib.thrift.message.PushMessage;
import com.axinfu.modellib.thrift.message.UPC2BQRPayResultPushContent;
import com.axinfu.modellib.thrift.message.UPC2BQRVerifyPasswordPushContent;
import com.axinfu.modellib.thrift.unqr.NewUnionPayResult;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.view.activity.MePayListDetailActivity;
import com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSwepPayResultActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptPwdActivity;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/10/15.
 */
public class AXPushReceiver extends GeTuiPushReceiver implements IPushContract.IPushImportantMsgView{
    private Context mContext;
    private IPushContract.IPushImportantMsgPresenter mPresenter;

    @Override
    public void onGotMessage(final Context context, String appid, String taskid,
                             String messageid, String payload) {
        super.onGotMessage(context, appid, taskid, messageid, payload);
        mContext = context;
        if (!App.mAxLoginSp.getLoginSign()) {
            return;
        }
        new PushPresenter(context,this);
        final PushMessage msg = new Gson().fromJson(payload, PushMessage.class);
        if (msg.type.equals("notify_message")) {
            noticeShowPoint(context);

            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pi =
                    PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            String title = "您有新的公告信息--校园安心付";
            showNotification(context, pi, title, msg.title, 0);
        } else if (msg.type.equals("important_notify")) {
            noticeShowPoint(context);

            // Do nothing 主页面统一加载重要消息。
            // mPresenter.getImportantMessage("");
            // 直接推送消息.
            // Intent resultIntent = new Intent(mContext, MainActivity.class);
            // resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // mContext.startActivity(resultIntent);
            // PendingIntent pi =
            // PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // String title = "您有新的公告信息--校园安心付";
            // showNotification(mContext, pi, title, "",0);
        } else if(msg.type.equals("feedback_message")){
            feedbackShowPoint(context);
        } else if(msg.type.equals("up_c2bqr_verify_password") || msg.type.equals("up_c2bqr_pay_result")){
            upqrSwepB2C(msg);
        }else if(msg.type.equals("refund") || msg.type.equals("cancel")){
            noticeShowPoint(context);
            Bundle bundle = new Bundle();
            PaymentRecord paymentRecord =new Gson().fromJson(msg.content,PaymentRecord.class);
            bundle.putSerializable(MePayListDetailActivity.EXTRA_DATA,paymentRecord);
            Intent resultIntent = new Intent(context, MePayListDetailActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtras(bundle);
            PendingIntent pi = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            showNotificationRefund(context, pi, msg.title,"你有一笔退款订单，请查看", 0);

        }
    }

    private void upqrSwepB2C(PushMessage msg){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if(msg.type.equals("up_c2bqr_verify_password")){
            intent.setClass(mContext, UnionSweptPwdActivity.class);
            bundle.putSerializable(UnionSweptPwdActivity.EXTRA_PWD_ORDER,new Gson().fromJson(msg.content, UPC2BQRVerifyPasswordPushContent.class));
        } else if(msg.type.equals("up_c2bqr_pay_result")){
            intent.setClass(mContext, UnionSwepPayResultActivity.class);
            bundle.putSerializable(UnionSwepPayResultActivity.EXTRA_ORDER,tranClass(new Gson().fromJson(msg.content, UPC2BQRPayResultPushContent.class)));
        }
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private NewUnionPayResult tranClass(UPC2BQRPayResultPushContent content){
        NewUnionPayResult result = new NewUnionPayResult();
        result.resp_code = content.getResp_code();
        result.resp_desc = content.getResp_desc();
        result.qr_code = content.getQr_code();
        result.status = content.getStatus();
        result.amount = content.getAmount();
        result.merchant_id = content.getMerchant_id();
        result.merchant_name = content.getMerchant_name();
        result.trade_summary = content.getTrade_summary();
        result.ori_amt = content.getOrig_amt();
        result.order_no = content.getOrder_no();
        result.pay_time = content.getPay_time();

        if(content.getCouponInfo()!=null && content.getCouponInfo().size() > 0){
            result.couponType = content.getCouponInfo().get(0).type;
            result.coupunSpnsrId = content.getCouponInfo().get(0).spnsr_id;
            result. couponOffstAmt = content.getCouponInfo().get(0).offst_amt;
            result.couponId = content.getCouponInfo().get(0).id;
            result.couponDesc = content.getCouponInfo().get(0).desc;
            result.couponAddnInfo = content.getCouponInfo().get(0).addn_info;
        }

        result.bankid = content.getCard_info().getId();
        result.bank_name = content.getCard_info().getBank_name();
        result.card_type_name = content.getCard_info().getCard_type_name();
        result.bankcard_no = content.getCard_info().getCard_no();
        result.iss_ins_code = content.getCard_info().getIss_ins_code();
        result.iss_ins_name = content.getCard_info().getIss_ins_name();
        result.iss_ins_icon = content.getCard_info().getIss_ins_icon();

        result.strategy =new ArrayList<>();
        if (content.getStrategy()!=null){
            for (int i = 0; i <content.getStrategy().size(); i++) {
                PricingStrategy pricingStrategy = new PricingStrategy();
                pricingStrategy.float_amt = content.getStrategy().get(i).float_amt;
                pricingStrategy.id = content.getStrategy().get(i).id;
                pricingStrategy.name = content.getStrategy().get(i).name;
                result.strategy.add(pricingStrategy);
            }
        }
        return result;
    }

    private void feedbackShowPoint(Context context){
        App.mAxLoginSp.setHasClickGetuiFeedback(false);// 设置为未点击
        Intent intent = new Intent(MeFragment.BROADCAST_MEFRAGMENT_FEEDBACK);
        context.sendBroadcast(intent);
    }

    private void noticeShowPoint(Context context){
        App.mAxLoginSp.setHasClickGetui(false);// 设置为未点击
        Intent intent = new Intent(MainActivity.NOTICE_BROADCAST);
        context.sendBroadcast(intent);
    }

    private void showNotification(Context context, PendingIntent pendingIntent, String title, String content, int id){
        if(context == null){
            return;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.a_icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        @SuppressLint("WrongConstant") NotificationManager notificationManager =
                (NotificationManager) context.getSystemService("notification");//Context.NOTIFICATION_SERVICE
        notificationManager.notify(id, mBuilder.build());
    }


    private void showNotificationRefund(Context context, PendingIntent pendingIntent, String title, String content, int id){
        if(context == null){
            return;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.app_icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        @SuppressLint("WrongConstant") NotificationManager notificationManager =
                (NotificationManager) context.getSystemService("notification");//Context.NOTIFICATION_SERVICE
        notificationManager.notify(id, mBuilder.build());
    }

//    @Override
//    public void onGotClientID(Context context, String clientid) {
//        super.onGotClientID(context, clientid);
//        App.mAxLoginSp.setGetuiClientId(clientid);
//    }

    @Override
    public void getImportantMessageSuccess(RealmList<ImportantMessage> list) {
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi =
                PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = "您有新的公告信息--校园安心付";
        showNotification(mContext, pi, title, "",0);
    }

    @Override
    public void setPresenter(IPushContract.IPushImportantMsgPresenter presenter) {
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

    }
}
