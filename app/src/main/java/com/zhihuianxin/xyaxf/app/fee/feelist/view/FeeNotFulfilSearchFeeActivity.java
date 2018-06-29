package com.zhihuianxin.xyaxf.app.fee.feelist.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.fee.Fee;
import com.axinfu.modellib.thrift.fee.FeeNotCheckCacheItem;
import com.axinfu.modellib.thrift.fee.SchoolRoll;
import com.axinfu.modellib.thrift.fee.SubFeeDeduction;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.feelist.FeeNotFullContract;
import com.zhihuianxin.xyaxf.app.fee.feelist.FeeNotFullPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CustomerRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.FeeNotCheckCacheItemRealmProxy;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Vincent on 2016/11/30.
 */

public class FeeNotFulfilSearchFeeActivity extends BaseRealmActionBarActivity implements FeeNotFullContract.FeeNotFullView{
    private static final int CHECK_ITEM_COUNT = 3;
    @InjectView(R.id.commit)
    Button mCommitBtn;
    @InjectView(R.id.card_number)
    EditText mEdit;
    @InjectView(R.id.check_cache)
    LinearLayout mCacheView;
    @InjectView(R.id.illegal_alert_view)
    View mErrorAlertView;

    private FeeNotFullContract.FeeNotFullPresenter presenter;
    private FeeNotCheckCacheController mFeeCacheontroller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        new FeeNotFullPresenter(this,this, SchedulerProvider.getInstance());
        initViews();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fee_not_fulfil_fragment_bottom_alert;
    }

    private void initViews(){
        mErrorAlertView.setVisibility(View.INVISIBLE);
        mFeeCacheontroller = new FeeNotCheckCacheController();
        findViewById(R.id.action_bar).setVisibility(View.GONE);
        mEdit.addTextChangedListener(watcher);
        initCacheItem();
    }

    private void initCacheItem(){
        ArrayList<FeeNotCheckCacheItem> list = mFeeCacheontroller.getItemList();
        if(list == null || list.size() == 0){
            mCacheView.setVisibility(View.GONE);
        } else{
            mCacheView.setVisibility(View.VISIBLE);
            for(FeeNotCheckCacheItem obj : list){
                View itemView = LayoutInflater.from(this).inflate(R.layout.fee_not_fulfil_fragment_bottom_alert_cacheitem,null);
                ((TextView)itemView.findViewById(R.id.itemview)).setText(obj.fee_number);
                itemView.setTag(obj.fee_number);
                itemView.setOnClickListener(cacheItemListener);
                mCacheView.addView(itemView);
            }
        }
    }

    View.OnClickListener cacheItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mEdit.setText(v.getTag()+"");
        }
    };

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(Util.isEmpty(s.toString())){
                mCommitBtn.setEnabled(false);
            } else{
                mCommitBtn.setEnabled(true);
            }
        }
    };

    @OnClick(R.id.cancel)
    public void onBtnCancelk(){
        finish();
    }

    @OnClick(R.id.commit)
    public void onBtnCommit(){
        if(((CustomerRealmProxy)realm.where(Customer.class)
                .equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account() != null){

            presenter.loadOtherFeeList(((FeeAccountRealmProxy)((CustomerRealmProxy)realm.where(Customer.class)
                    .equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account())
                    .realmGet$name(),mEdit.getText().toString().trim());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void feeNotFullSuccess(List<Fee> fees, SchoolRoll schoolRoll,List<SubFeeDeduction> deductible_fees,String pay_limit_hint) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(FeeNotFulfilFragment.EXTRA_CHECK_DATA, mEdit.getText().toString().trim());
        intent.putExtras(bundle);

        // 查询成功但是没有缴费项也要保存
        FeeNotCheckCacheItem item = new FeeNotCheckCacheItem();
        item.fee_number = mEdit.getText().toString().trim();
        item.time = System.currentTimeMillis()+"";
        mFeeCacheontroller.addItem(item);

        if(fees.size() == 0){
            Toast.makeText(this,"缴费项为空!",Toast.LENGTH_LONG).show();
        } else{
            App.mAxLoginSp.setOtherFeeNo(mEdit.getText().toString().trim());
            setResult(RESULT_OK,intent);
            finish();
        }

        if (pay_limit_hint != null) {
            App.hint = pay_limit_hint;
        } else {
            App.hint = null;
        }
    }

    @Override
    public void feeNotFullFailure() {

    }

    @Override
    public void setPresenter(FeeNotFullContract.FeeNotFullPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {
        mErrorAlertView.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.error_alert_content)).setText(errorMsg);
    }

    @Override
    public void loadComplete() {

    }

    private class FeeNotCheckCacheController {
        void addItem(final FeeNotCheckCacheItem item){
            final int size = realm.where(FeeNotCheckCacheItem.class).findAll().size();
            if(size < CHECK_ITEM_COUNT){
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(item);
                    }
                });
            } else{
                final RealmResults<FeeNotCheckCacheItem> results = realm.where(FeeNotCheckCacheItem.class).findAll().sort("time");
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteFirstFromRealm();
                        realm.copyToRealmOrUpdate(item);
                    }
                });
            }
        }

        ArrayList<FeeNotCheckCacheItem> getItemList(){
            ArrayList<FeeNotCheckCacheItem> list = new ArrayList<>();
            RealmResults<FeeNotCheckCacheItem> results = realm.where(FeeNotCheckCacheItem.class).findAll().sort("time",Sort.DESCENDING);
            if(results.size() == 0){
                return null;
            }

            for(FeeNotCheckCacheItem obj : results){
                FeeNotCheckCacheItem item = new FeeNotCheckCacheItem();
                item.fee_number = ((FeeNotCheckCacheItemRealmProxy)obj).realmGet$fee_number();
                list.add(item);
            }
            return list;
        }

        int getSize(){
            return realm.where(FeeNotCheckCacheItem.class).findAll().size();
        }
    }
}
