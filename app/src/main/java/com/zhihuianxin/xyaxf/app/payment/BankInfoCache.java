package com.zhihuianxin.xyaxf.app.payment;

import android.content.Context;

import com.axinfu.modellib.thrift.payment.BankLimit;
import com.axinfu.modellib.thrift.payment.PayLimit;
import com.xyaxf.axpay.persistence.AbsSharedPreferencesData;

import java.util.List;

/**
 * Created by zhengzheng on 16/7/23.
 * 用于缓存超限额提示功能所推荐/支持的银行列表
 */
public class BankInfoCache extends AbsSharedPreferencesData {

    public static BankInfoCache sInstance;
    public static PayLimit NOLIMIT;
    static {
        NOLIMIT = new PayLimit();
        NOLIMIT.trade_limit_amt = "0.00";
    }

    public static BankInfoCache getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Must call initialize() before use");
        }

        return sInstance;
    }

    public static void initialize(Context context) {
        if (sInstance == null) {
            sInstance = new BankInfoCache(context.getApplicationContext());
            sInstance.load();
        }
    }

    public BankInfoCache(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return "bankinfo";
    }

    private List<BankLimit> bankinfos;
    private PayLimit tradeLimits;

    public void setBankInfo(List<BankLimit> bankinfos) {
        this.bankinfos=bankinfos;
    }

    public void setTradeLimit(PayLimit tradeLimits) {
        this.tradeLimits=tradeLimits;
    }

    public List<BankLimit> getBankInfo() {
        if(bankinfos != null){
            return bankinfos;
        }
        return null;
    }

    public PayLimit getTradeLimit() {
        if (tradeLimits == null){
            return NOLIMIT;
        }

        return tradeLimits;
    }

}
