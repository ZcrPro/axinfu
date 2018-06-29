package com.zhihuianxin.xyaxf.app.life.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.business.Business;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.WebPageActivity;
import com.zhihuianxin.xyaxf.app.ecard.open.EcardOpenActivity;
import com.zhihuianxin.xyaxf.app.ecard.view.EcardActivity;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.fee.check.FeeCheckActivity;
import com.zhihuianxin.xyaxf.app.home.qrcode.QRCodeActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionSweptContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionSweptMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptCodeActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptRecordListActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.BusinessRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Vincent on 2016/10/26.
 */

public class LifeFragment extends BaseRealmFragment implements IunionSweptContract.IunionSweptView {
    public static final String EXTRA_TEXT = "extra_text";
    public static final int REQUEST_GESTURE_CODE = 1000;
    public static final String BROADCAST_MEFRAGMENT_UPDATE = "broadcast_mefragemnt_update";
    final private List<LifeButtonEntity> buttonEntities = new ArrayList<LifeButtonEntity>();
    private LifeAdapter adapter;

    @InjectView(R.id.list)
    ListView list;
    @InjectView(R.id.union_swept_scan)
    View scanView;
    @InjectView(R.id.union_swept_scan_qr)
    View qrView;
    @InjectView(R.id.union_swept_scan_record)
    View recordView;

    private IunionSweptContract.IunionSweptPresenter presenter;

    public static Fragment newInstance(String text) {
        LifeFragment fragment = new LifeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    BroadcastReceiver LifeFragmentReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_MEFRAGMENT_UPDATE)) {
                //查询bu
                buttonEntities.clear();
                final RealmResults<Business> businesses = realm.where(Business.class).findAll();
                for (int i = 0; i < businesses.size(); i++) {
                    if (((BusinessRealmProxy) businesses.get(i)).realmGet$container().equals("Life") && !((BusinessRealmProxy) businesses.get(i)).realmGet$container().equals("Invisible")) {
                        buttonEntities.add(new LifeButtonEntity(businesses.get(i)));
                    }
                }
                Iterator<LifeButtonEntity> it = buttonEntities.iterator();
                while (it.hasNext()) {
                    LifeButtonEntity x = it.next();
                    if (((BusinessRealmProxy) (x.business)).realmGet$status().equals("Life") && ((BusinessRealmProxy) (x.business)).realmGet$status().equals("Invisible")) {
                        it.remove();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        new UnionSweptMainPresenter(getActivity(), this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
        list.setAdapter(null);
        adapter = new LifeAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= list.getHeaderViewsCount();

                LifeButtonEntity item = adapter.getItem(position);

                item.onClick(view);
            }
        });
        //查询bu
        final RealmResults<Business> businesses = realm.where(Business.class).findAll();//equal 要乱序
        for (int i = 0; i < businesses.size(); i++) {
            if (((BusinessRealmProxy) businesses.get(i)).realmGet$container().equals("Life") && !((BusinessRealmProxy) businesses.get(i)).realmGet$container().equals("Invisible")) {
                buttonEntities.add(new LifeButtonEntity(businesses.get(i)));
            }
        }

        Iterator<LifeButtonEntity> it = buttonEntities.iterator();
        while (it.hasNext()) {
            LifeButtonEntity x = it.next();
            if (((BusinessRealmProxy) (x.business)).realmGet$status().equals("Life") && ((BusinessRealmProxy) (x.business)).realmGet$status().equals("Invisible")) {
                it.remove();
            }
        }

        adapter.notifyDataSetChanged();

        IntentFilter filter = new IntentFilter(BROADCAST_MEFRAGMENT_UPDATE);
        getActivity().registerReceiver(LifeFragmentReceive, filter);
        qrView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getBankCard();
            }
        });
        scanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
            }
        });
        recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UnionSweptRecordListActivity.class));
            }
        });
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
        if (bankCards != null && bankCards.size() > 0) {
            startActivity(new Intent(getActivity(), UnionSweptCodeActivity.class));
        } else {
            startActivity(new Intent(getActivity(), UnionSweptEmptyCardActivity.class));
        }
    }

    @Override
    public void JudgePayPwdResult(PaymentConfig config) {
    }

    @Override
    public void getC2BCodeResult(String qr_code) {
    }

    @Override
    public void swepPayPwdResult() {
    }

    @Override
    public void verifyPayPwdResult(boolean is_match, int left_retry_count) {
    }

    @Override
    public void payOrderResult(PaymentOrder order) {
    }

    @Override
    public void setPayList(RealmList<PaymentRecord> payList) {
    }

    @Override
    public void getRealNameResult(RealName realName) {
    }

    @Override
    public void c2bQrVerifyPpaymentPasswordResult(boolean is_match, int left_retry_count) {
    }

    @Override
    public void setPresenter(IunionSweptContract.IunionSweptPresenter presenter) {
        this.presenter = presenter;
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

    class LifeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return buttonEntities.size();
        }

        @Override
        public LifeButtonEntity getItem(int position) {
            return buttonEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView;
            if (convertView == null && getActivity() != null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.life_button, parent, false);
            } else {
                itemView = convertView;
            }

            LifeButtonEntity item = getItem(position);
            if (((BusinessRealmProxy) item.business).realmGet$status().equals("Invisible")) {
                itemView.setVisibility(View.GONE);
            } else {
                itemView.setVisibility(View.VISIBLE);
                final TextView title = (TextView) itemView.findViewById(R.id.title);
                title.setText(item.getTitle());
                ImageView imageView = (ImageView) itemView.findViewById(R.id.img);
                ImageLoader.getInstance().displayImage(item.getLifeIcon(), imageView, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (!Util.isEmpty(imageUri)) {
                            title.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            }

            itemView.setTag(item.getType());
            return itemView;
        }
    }

    class LifeButtonEntity implements View.OnClickListener {
        Business business;

        LifeButtonEntity(final Business business) {
            this.business = business;
        }

        public String getTitle() {
            if (business != null) {
                return ((BusinessRealmProxy) business).realmGet$title();
            }

            return null;
        }

        public String getType() {
            if (business != null) {
                return ((BusinessRealmProxy) business).realmGet$type();
            }

            return null;

        }


        public String getLifeIcon() {
            if (business != null) {
                if (!((BusinessRealmProxy) business).realmGet$status().equals("Invisible") && !((BusinessRealmProxy) business).realmGet$status().equals("Gray")) {
                    return ((BusinessRealmProxy) business).realmGet$icon();
                } else {
                    return ((BusinessRealmProxy) business).realmGet$icon_gray();
                }
            }

            return null;
        }

        @Override
        public void onClick(View v) {
            if (business != null) {
                if (!((BusinessRealmProxy) business).realmGet$status().equals("Invisible") && !((BusinessRealmProxy) business).realmGet$status().equals("Gray")) {
                    final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
                    Intent intent;
                    switch ((String) v.getTag()) {
                        case "Fee":
                            final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();
                            if (((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("OK")) {
                                Bundle bundle = new Bundle();
                                bundle.putString(FeeActivity.ENTER_FLAG, FeeActivity.normal);
                                intent = new Intent(getActivity(), FeeActivity.class);
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                            } else {
                                getActivity().startActivity(new Intent(getActivity(), FeeCheckActivity.class));
                            }
                            break;
                        case "ECard":
                            final ECardAccount eCardAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account();
                            if (((ECardAccountRealmProxy) eCardAccount).realmGet$status().equals("OK")) {
                                getActivity().startActivity(new Intent(getActivity(), EcardActivity.class));
                            } else {
                                getActivity().startActivity(new Intent(getActivity(), EcardOpenActivity.class));
                            }
                            break;
                        case "TdtcFee":
                            break;
                        case "Web":
                            intent = new Intent(getActivity(), WebPageActivity.class);
                            intent.putExtra(WebPageActivity.EXTRA_URL, ((BusinessRealmProxy) business).realmGet$arg());
                            intent.putExtra(WebPageActivity.EXTRA_TITLE, ((BusinessRealmProxy) business).realmGet$title());
                            startActivity(intent);
                            break;
                        case "Unuseable":
                            Toast.makeText(getActivity(), "暂不可用", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(LifeFragmentReceive);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.life_fragment;
    }
}
