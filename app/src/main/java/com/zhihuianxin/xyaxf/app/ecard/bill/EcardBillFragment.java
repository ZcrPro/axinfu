package com.zhihuianxin.xyaxf.app.ecard.bill;//package com.zhihuianxin.xyaxf.app.ecard.bill;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.axinfu.modellib.thrift.ecard.ECardChargeRecord;
//import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
//import com.zhihuianxin.xyaxf.BaseRealmFragment;
//import com.zhihuianxin.xyaxf.R;
//
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import io.realm.Realm;
//
///**
// * Created by zcrpro on 2016/11/9.
// */
//public class EcardBillFragment extends BaseRealmFragment implements EcardBillContract.EcardBillView {
//
//
//    @InjectView(R.id.tv_oder_amt1)
//    TextView tvOderAmt1;
//    @InjectView(R.id.tv_oder_status_1)
//    TextView tvOderStatus1;
//    @InjectView(R.id.ll_oder_one)
//    LinearLayout llOderOne;
//    @InjectView(R.id.tv_oder_amt2)
//    TextView tvOderAmt2;
//    @InjectView(R.id.tv_oder_status_2)
//    TextView tvOderStatus2;
//    @InjectView(R.id.ll_oder_two)
//    LinearLayout llOderTwo;
//
//    private EcardBillContract.EcardBillPresenter presenter;
//    private EcardBillPresenter ecardBillPresenter;
//    public static final String EXTRA_TEXT = "extra_text";
//
//    @Override
//    protected void initView(View view, Bundle savedInstanceState) {
//        ecardBillPresenter = new EcardBillPresenter(this, getActivity(), SchedulerProvider.getInstance());
//        presenter.loadEccrdBillData(null, null, 0);
//    }
//
//    public static EcardBillFragment newInstance(String text) {
//        EcardBillFragment fragment = new EcardBillFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(EXTRA_TEXT, text);
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//    public EcardBillFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        ecardBillPresenter.unsubscribe();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ecard_bill_fragment;
//    }
//
//    @Override
//    public void ecardBillSuccess(final List<ECardChargeRecord> eCardChargeRecord) {
//
//        /**
//         * 存入ecard记录
//         */
//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm bgRealm) {
//                bgRealm.copyToRealmOrUpdate(eCardChargeRecord);
//            }
//        }, new Realm.Transaction.OnSuccess() {
//            @Override
//            public void onSuccess() {
//                // Transaction was a success.
//                Log.d("EcardBillFragment", "存储一卡通充值记录成功");
//            }
//        }, new Realm.Transaction.OnError() {
//            @Override
//            public void onError(Throwable error) {
//                // Transaction failed and was automatically canceled.
//            }
//        });
//
//        if (eCardChargeRecord.size() == 0) {
//            llOderTwo.setVisibility(View.GONE);
//            llOderOne.setVisibility(View.GONE);
//        } else if (eCardChargeRecord.size() > 1) {
//            llOderTwo.setVisibility(View.VISIBLE);
//            llOderOne.setVisibility(View.VISIBLE);
//            tvOderAmt1.setText(eCardChargeRecord.get(0).amount);
//            tvOderAmt2.setText(eCardChargeRecord.get(1).amount);
//
//            if (eCardChargeRecord.get(0).status.equals("notify_processing")) {
//                tvOderStatus1.setText("已支付");
//            } else if (eCardChargeRecord.get(0).status.equals("notify_success")) {
//                tvOderStatus1.setText("已上账");
//            } else {
//                tvOderStatus1.setText("未知");
//            }
//
//            if (eCardChargeRecord.get(1).status.equals("notify_processing")) {
//                tvOderStatus2.setText("已支付");
//            } else if (eCardChargeRecord.get(1).status.equals("notify_success")) {
//                tvOderStatus2.setText("已上账");
//            } else {
//                tvOderStatus2.setText("未知");
//            }
//        } else if (eCardChargeRecord.size() == 1) {
//            llOderTwo.setVisibility(View.GONE);
//            llOderOne.setVisibility(View.VISIBLE);
//            tvOderAmt1.setText(eCardChargeRecord.get(0).amount);
//            if (eCardChargeRecord.get(0).status.equals("notify_processing")) {
//                tvOderStatus1.setText("已支付");
//            } else if (eCardChargeRecord.get(0).status.equals("notify_success")) {
//                tvOderStatus1.setText("已上账");
//            } else {
//                tvOderStatus1.setText("未知");
//            }
//        }
//    }
//
//    @Override
//    public void ecardBillFailure() {
//
//    }
//
//    @Override
//    public void setPresenter(EcardBillContract.EcardBillPresenter presenter) {
//        this.presenter = presenter;
//    }
//
//    @Override
//    public void loadStart() {
//
//    }
//
//    @Override
//    public void loadError(String errorMsg) {
//
//    }
//
//    @Override
//    public void loadComplete() {
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = super.onCreateView(inflater, container, savedInstanceState);
//        ButterKnife.inject(this, rootView);
//        return rootView;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.reset(this);
//    }
//}
