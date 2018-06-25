package com.zhihuianxin.xyaxf.app.home.business;

import android.content.Context;

import com.zhihuianxin.axutil.BaseSchedulerProvider;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by zcrpro on 2016/10/31.
 */
public class BusinessGridPresenter implements BusinessGridContract.IBusinessGridPresenter {

    private BusinessGridContract.IBusinessGridView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;
    private BaseSchedulerProvider mSchedulerProvider;

    public BusinessGridPresenter(BusinessGridContract.IBusinessGridView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
        this.mView = mView;
        this.mContext = mContext;
        this.mSchedulerProvider = mSchedulerProvider;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void loadBusinessData() {
//        final RealmResults<Business> business = Realm.getDefaultInstance().where(Business.class).equalTo("container", "AnXinFu")
//                .findAll();
//        if (business.size() > 0) {
//            mView.success(business);
//        }
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
