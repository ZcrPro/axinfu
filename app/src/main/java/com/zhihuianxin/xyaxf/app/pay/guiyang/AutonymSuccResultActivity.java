package com.zhihuianxin.xyaxf.app.pay.guiyang;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2018/3/13.
 */

public class AutonymSuccResultActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        guiyangPayResult(getIntent().getExtras().getString("tn"));
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
                .subscribe(new BaseSubscriber<Object>(this, true, null) {

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
