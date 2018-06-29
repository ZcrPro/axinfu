package com.zhihuianxin.xyaxf.app.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import modellib.thrift.base.PayChannel;
import modellib.thrift.base.PayMethod;
import modellib.thrift.payment.PaymentOrder;
import com.google.gson.Gson;
import com.unionpay.UPPayAssistEx;
import com.xyaxf.axpay.AppConstants;
import com.xyaxf.axpay.Util;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.xyaxf.axpay.modle.UpPayInfo;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.app.ocp.OcpPaySucActivity;
import com.zhihuianxin.xyaxf.app.ocp.QrResultActivity;
import com.zhihuianxin.xyaxf.app.payment.CCBWapPayActivity;
import com.zhihuianxin.xyaxf.app.payment.PaymentStatusActivity;
import com.zhihuianxin.xyaxf.app.payment.SimpleDialog;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.io.File;
import java.io.IOException;

/**
 * Created by John on 2014/12/11.
 */
public class PayActivity extends Activity {
    private static final String LOG_TAG = "PayActivity";

    public static final String EXTRA_PAYMENT_INFO = "payment_info";
    public static final String EXTRA_PAY_FOR = "pay_for";

    private static final int REQUEST_CCB_WAP_PAY = 4001;
    private static final int REQUEST_INSTALL_UP_APK = 4002;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private static final String UP_PAY_PLUGIN_FILE = "assets://up_pay_plugin_ex.inf";

    private PaymentInfo mPaymentInfo;
    private boolean mPaySuccess;

//    // Identifier for the permission request
//    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

//    // Called when the user is performing an action which requires the app to read the
//    // user's contacts
//    @SuppressLint("NewApi")
//    public void getPermissionToReadUserContacts() {
//        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
//        // checking the build version since Context.checkSelfPermission(...) is only available
//        // in Marshmallow
//        // 2) Always check for permission (even if permission has already been granted)
//        // since the user can revoke permissions at any time through Settings
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // The permission is NOT already granted.
//            // Check if the user has been asked about this permission already and denied
//            // it. If so, we want to give more explanation about why the permission is needed.
//            if (shouldShowRequestPermissionRationale(
//                    Manifest.permission.READ_PHONE_STATE)) {
//                // Show our own UI to explain to the user why we need to read the contacts
//                // before actually requesting the permission and showing the default UI
//                if (mPaymentInfo.pay_method.equals(PayChannel.UnionPay)) {
//                    Gson gson = new Gson();
//                    UpPayInfo upPayInfo = gson.fromJson(mPaymentInfo.pay_info, UpPayInfo.class);
//                    pay(mPaymentInfo.pay_method, upPayInfo.tn);
//                    UPPay(upPayInfo.tn);
//                }
//            }
//
//            // Fire off an async request to actually get the permission
//            // This will show the standard permission request dialog UI
//            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
//                    READ_CONTACTS_PERMISSIONS_REQUEST);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPaymentInfo = (PaymentInfo) getIntent().getSerializableExtra(EXTRA_PAYMENT_INFO);
        super.onCreate(savedInstanceState);
//        getPermissionToReadUserContacts();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        pay(mPaymentInfo.pay_method, mPaymentInfo.pay_info);
        if (mPaymentInfo.pay_method.channel.equals(PayChannel.UnionPay.name())) {
            Gson gson = new Gson();
            UpPayInfo upPayInfo = gson.fromJson(mPaymentInfo.pay_info, UpPayInfo.class);
            if (upPayInfo.tn != null || !TextUtils.isEmpty(upPayInfo.tn))
                pay(mPaymentInfo.pay_method, upPayInfo.tn);
            Log.d("PayActivity", "tn=" + upPayInfo.tn);
        } else if (mPaymentInfo.pay_method.channel.equals(PayChannel.CCBWapPay.name())) {
            pay(mPaymentInfo.pay_method, mPaymentInfo.pay_info);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void pay(PayMethod payMethod, String info) {
        if (payMethod.channel.equals(PayChannel.UnionPay.name())) {
            UPPay(info);
        } else if (payMethod.channel.equals(PayChannel.CCBWapPay.name())) {
            CCBWapPay(info);
        } else {
            Toast.makeText(this, "Empty pay_method", Toast.LENGTH_LONG).show();
        }
    }

    private void CCBWapPay(String payInfo) {
        Intent intent = new Intent(this, CCBWapPayActivity.class);
        intent.putExtra(CCBWapPayActivity.EXTRA_PAYMENT_INFO, mPaymentInfo);
        intent.putExtra(CCBWapPayActivity.EXTRA_PAY_FOR, getIntent().getSerializableExtra(EXTRA_PAY_FOR));
        startActivityForResult(intent, REQUEST_CCB_WAP_PAY);
    }

    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    // 校掌测试环境也连接银联正式环境, 所以配置全为0
    public static final String UPPayMode = BuildConfig.AnXinDEBUG ? "00" : "00";

    public void UPPay(String tn) {
//        Log.d("PayActivity", tn);
        int ret = checkInstalledUnionPayApk(PayActivity.this);
//        if (ret == PLUGIN_VALID) {
//            if (PermissionUtil.isLacksOfPermission(this, PermissionUtil.PERMISSION[0])) {
//                ActivityCompat.requestPermissions(this, PermissionUtil.PERMISSION, 0x12);
//            } else {
        UPPayAssistEx.startPay(this, null, null, tn, UPPayMode);
//            }
//        } else if (ret == PLUGIN_NEED_UPGRADE)
//
//        {
//            Toast.makeText(this, "您的银联支付控件需要升级", Toast.LENGTH_SHORT).show();
//            onUPPayError("您的银联支付控件需要升级");
//        } else if (ret == PLUGIN_NOT_INSTALLED)
//
//        {
//            Toast.makeText(this, "您的银联支付控件未安装", Toast.LENGTH_SHORT).show();
//            onUPPayError("您的银联支付控件未安装");
//        }
//
//        Log.e(LOG_TAG, "" + ret);
    }

//    public static boolean checkReqEnv(Context context, PayChannel channel) {
//        switch (channel) {
//            case UnionPay:
//                return checkUnionPayEnv(context);
//            default:
//                return true;
//        }
//    }

//    private static boolean checkUnionPayEnv(final Context context) {
//        int ret = checkInstalledUnionPayApk(context);
//
//        if (ret == PLUGIN_VALID) {
//            return true;
//        }
//
//        if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
//            Log.e(LOG_TAG, " plugin not found or need upgrade!!!");
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("提示");
//            builder.setMessage(String.format("完成支付需要%s银联支付控件，是否%s？",
//                    ret == PLUGIN_NOT_INSTALLED ? "安装" : "更新", ret == PLUGIN_NOT_INSTALLED ? "安装" : "更新"));
//
//            builder.setNegativeButton("确定",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            installUPPayApk(context);
//                            dialog.dismiss();
//                        }
//                    });
//
//            builder.setPositiveButton("取消",
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                }
//            });
//            builder.create().show();
//        }
//
//        Log.e(LOG_TAG, "" + ret);
//
//        return false;
//    }

    private static class UnionAPKInfo {
        public String package_name;
        public String version_name;
        public int version_code;
        public String apk_file;
    }

    private static int checkInstalledUnionPayApk(Context context) {
        boolean installed = UPPayAssistEx.checkInstalled(context);
        if (!installed) {
            return PLUGIN_NOT_INSTALLED;
        }

        UnionAPKInfo upApkInfo = null;
        try {
            String strUpApkInfo = Util.getString(context, UP_PAY_PLUGIN_FILE, "utf-8");
            upApkInfo = new Gson().fromJson(strUpApkInfo, UnionAPKInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (upApkInfo == null) {
            return PLUGIN_VALID;
        }

        PackageInfo info = Util.getPackageInfo(context, upApkInfo.package_name);
        if (info == null) {
            return PLUGIN_VALID;
        }

        if (info.versionCode < upApkInfo.version_code) {
            return PLUGIN_NEED_UPGRADE;
        }

        return PLUGIN_VALID;
    }

    private File mUnionApkTempFile;

    private static void installUPPayApk(final Context context) {
        boolean installResult = UPPayAssistEx.installUPPayPlugin(context);
        if (!installResult) {
//            Toast.makeText(context, "尝试安装银联支付控件出了点问题, 请到银联官网下载安装. 对此给您带来的不便我们深感抱歉. ", Toast.LENGTH_SHORT).show();
            SimpleDialog dlg = new SimpleDialog(context);
            dlg.setMessage("尝试安装银联支付控件出了点问题, 请到银联官网下载安装. 对此给您带来的不便我们深感抱歉. ");
            dlg.setPositiveButton("前往安装", new SimpleDialog.OnButtonClickListener() {
                @Override
                public boolean onClick(View view) {
                    Uri uri = Uri.parse("http://mobile.unionpay.com/download/");
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));

                    return true;
                }
            });
            dlg.setNegativeButton("取消", null);
            dlg.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("PayActivity", "requestCode:" + requestCode);
        Log.d("PayActivity", "resultCode:" + resultCode);
        if (requestCode == REQUEST_CCB_WAP_PAY) {
            if (resultCode == RESULT_OK) {
                onCCBWapPaySuccess();
            } else if (resultCode == RESULT_CANCELED) {
                onCCBWapPayCanceled();
//                //发送一个取消支付的信息--在casherdesk检测
//                RxBus.getDefault().send("onPayCancelled");
                finish();
            } else if (resultCode == AppConstants.RESULT_FAIL) {
                onCCBWapPayError(null);
                finish();
            }
        } else if (requestCode == REQUEST_INSTALL_UP_APK) {
            if (mUnionApkTempFile != null) {
                mUnionApkTempFile.delete();
                mUnionApkTempFile = null;

                // 设置"Unknown Sources" resultCode = 0
                // 取消安装 resultCode = 0
                // 安装成功, 点击"Done" resultCode = 0
                // 所以无从得知安装结果, 所以需要再次尝试支付
                UPPay(mPaymentInfo.pay_info);
            }
        } else {
            /*************************************************
             *
             *  步骤3：处理银联手机支付控件返回的支付结果
             *
             ************************************************/
            if (data == null) {
                finish();
                return;
            }

            String msg = "";
            /*
             * 支付控件返回字符串:success、fail、cancel
	         *      分别代表支付成功，支付失败，支付取消
	         */
            String str = data.getExtras().getString("pay_result");
            if (str != null) {
                //union pay
                Log.d("PayActivity", "str+++++++++++= " + str);
                if (str.equalsIgnoreCase("success")) {
                    onUPPaySuccess();
                } else if (str.equalsIgnoreCase("fail")) {
                    onUPPayError("支付失败");
                    RxBus.getDefault().send("fail");
                } else if (str.equalsIgnoreCase("cancel")) {
                    //发送一个取消支付的信息--在casherdesk检测
                    RxBus.getDefault().send("onPayCancelled");
                    onUPPayCancelled();
                }
            }
        }
    }

    private void onUPPaySuccess() {
        mPaySuccess = true;
        onPayResult(mPaymentInfo, mPaySuccess);

        notifyPaySuccess();
    }

    private void onUPPayCancelled() {
        finish();
//            if (AppConstant.DEBUG) {
//            onUPPaySuccess();
//            return;
//        }
        onPayCancelled(mPaymentInfo);
    }

    private void onUPPayError(String msg) {
        mPaySuccess = false;

        onPayResult(mPaymentInfo, mPaySuccess);
    }

    private void onCCBWapPaySuccess() {
        mPaySuccess = true;
        onPayResult(mPaymentInfo, mPaySuccess);

        notifyPaySuccess();
    }

    private void CCBWapPayCancelled() {
//        if (AppConstant.DEBUG) {
//            onCCBWapPaySuccess();
//            return;
//        }

        onPayCancelled(mPaymentInfo);
    }

    private void onCCBWapPayError(String msg) {
        mPaySuccess = false;
        onPayResult(mPaymentInfo, mPaySuccess);
    }

    private void onCCBWapPayCanceled() {
//        if (AppConstant.DEBUG) {
//            onCCBWapPaySuccess();
//            return;
//        }

        onPayCancelled(mPaymentInfo);
    }

    public void onPayResult(PaymentInfo info, boolean success) {
        setResult();
        finish();
    }

    public void onPayCancelled(PaymentInfo info) {
        finish();
    }

    public void setResult() {
        Intent result = new Intent(AppConstants.ACTION_PAY_RESULT);
        result.putExtra(AppConstants.EXTRA_PAYMENT_INFO, mPaymentInfo);
        result.putExtra(AppConstants.EXTRA_PAY_SUCCESS, mPaySuccess);
        setResult(mPaySuccess ? RESULT_OK : AppConstants.RESULT_FAIL, result);
    }

    private void notifyPaySuccess() {

        if (getIntent().getSerializableExtra(EXTRA_PAY_FOR).equals(PayFor.AxfQRPay)){

            Bundle bundle1 = getIntent().getExtras();
            assert bundle1 != null;
            //成功
            Bundle bundle = new Bundle();
            bundle.putSerializable("PaymentOrder", (PaymentOrder) bundle1.getSerializable("PaymentOrder"));
            bundle.putSerializable("PayInfo", (QrResultActivity.PayInfo) bundle1.getSerializable("PayInfo"));
            Intent intent = new Intent(PayActivity.this, OcpPaySucActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else {
            //成功
            Intent intent = new Intent(this, PaymentStatusActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(PaymentStatusActivity.AMOUNT, mPaymentInfo.payment_amt);
            bundle.putString(PaymentStatusActivity.NAME, mPaymentInfo.fee_name);
            bundle.putSerializable(PaymentStatusActivity.WAY, mPaymentInfo.pay_method);
            bundle.putString(PaymentStatusActivity.TIME, mPaymentInfo.fee_time);
            bundle.putString(PaymentStatusActivity.ODER, mPaymentInfo.channel_orderno);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }

    public PaymentInfo getPaymentInfo() {
        return mPaymentInfo;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == 0x12) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mPaymentInfo.pay_method.channel.equals(PayChannel.UnionPay.name())) {
                    Gson gson = new Gson();
                    UpPayInfo upPayInfo = gson.fromJson(mPaymentInfo.pay_info, UpPayInfo.class);
                    pay(mPaymentInfo.pay_method, upPayInfo.tn);
                    UPPay(upPayInfo.tn);
                }
            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
