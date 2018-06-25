package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionVerRealNameContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/16.
 */

public class UnionVerRealNamePresenter implements IunionVerRealNameContract.IunionVerRealNamePresenter{
    private Context mContext;
    private IunionVerRealNameContract.IunionVerRealName mView;

    private class VerPwdResponse{
        public BaseResponse resp;
        public boolean is_match;
    }

    public UnionVerRealNamePresenter(Context context,IunionVerRealNameContract.IunionVerRealName view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }


    @Override
    public void verRealName(String name, String id_card_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("id_card_no",id_card_no);
        CustomerService cus = ApiFactory.getFactory().create(CustomerService.class);
        cus.verityPwd(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        VerPwdResponse response = new Gson().fromJson(o.toString(),VerPwdResponse.class);
                        mView.verRealNameResult(response.is_match);
                    }
                });
    }
}
