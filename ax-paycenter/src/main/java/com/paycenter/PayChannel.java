package com.paycenter;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PayChannel {

    public static final String UnionPay = "UnionPay";                  // 银联支付
    public static final String CCBWapPay = "CCBWapPay";                // 建行Wap支付
    public static final String RFID = "RFID";                          // 一卡通余额支付
    public static final String UPWapPay = "UPWapPay";                  // 银联Wap支付
    public static final String ApplePay = "ApplePay";                  // ApplePay
    public static final String AliPay = "AliPay";                      // ApplePay
    public static final String WxPay = "WxPay";                        // 微信Wap跳转支付
    public static final String WxQrPay = "WxQrPay";                    // 微信二维码支付
    public static final String WxPubPay = "WxPubPay";                  // 微信公众号支付
    public static final String QuickPay = "QuickPay";                  // 快捷支付
    public static final String NoNeed = "NoNeed";                      // 金额为0; 不需要支付
    public static final String GuiYangCreditLoanPay = "GuiYangCreditLoanPay";    // 贵阳银行信用支付
    public static final String AliAppPay = "AliAppPay";                // 支付宝APP支付
    public static final String WxAppPay = "WxAppPay";                  // 微信普通商户模式APP支付
    public static final String UPQRPay = "UPQRPay";                    // 银联二维码支付
    public static final String UPQRQuickPay = "UPQRQuickPay";          // 银联二维码快捷支付
    public static final String UPTokenPay = "UPTokenPay";              // 无跳转支付
    public static final String CFCAPay = "CFCAPay";                    // 广美快捷支付
    public static final String MiniPay = "MiniPay";                    // 小程序支付
    public static final String JDPay = "JDPay";                        // 京东支付

    private String channel;

    public void setPaychannel(@channel String channel) {
        this.channel = channel;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({UnionPay,CCBWapPay,RFID,UPWapPay,ApplePay,AliPay,WxPay,WxQrPay,WxPubPay,QuickPay,NoNeed,GuiYangCreditLoanPay,AliAppPay,WxAppPay,UPQRPay,UPQRQuickPay,UPTokenPay,CFCAPay,MiniPay,JDPay})
    public @interface channel {

    }
}