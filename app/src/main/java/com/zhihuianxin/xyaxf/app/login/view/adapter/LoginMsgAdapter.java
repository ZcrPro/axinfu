package com.zhihuianxin.xyaxf.app.login.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.business.Business;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.message.ActionType;
import com.axinfu.modellib.thrift.message.AxfMesssage;
import com.axinfu.modellib.thrift.message.UPC2BQRPayResultPushContent;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.WebPageActivity;
import com.zhihuianxin.xyaxf.app.ecard.open.EcardOpenActivity;
import com.zhihuianxin.xyaxf.app.ecard.view.EcardActivity;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.fee.check.FeeCheckActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginMsgDetailActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MePayListDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.BusinessRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2016/10/25.
 */
public class LoginMsgAdapter extends ArrayAdapter<AxfMesssage> {
    private Context mContext;
    private int size;
    private Realm realm;

    public LoginMsgAdapter(Context context, Realm realm) {
        super(context, 0);
        mContext = context;
        this.realm = realm;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.login_msg_content, parent, false);
//        if(convertView != null){
//            view = convertView;
//        }
//        else{
//            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.login_msg_content, parent, false);
//        }
        ((TextView) view.findViewById(R.id.title)).setText(getItem(position).title);
        ((TextView) view.findViewById(R.id.content)).setText(getItem(position).content);//goUrl
        int[] timeItems = getItem(position).time != null ? Util.getTimeItems(getItem(position).time) : null;
        ((TextView) view.findViewById(R.id.time_title)).setText(String.format("%04d-%02d-%02d %02d:%02d:%02d",
                timeItems[0], timeItems[1], timeItems[2], timeItems[3], timeItems[4], timeItems[5]));
        ((TextView) view.findViewById(R.id.time)).setText(String.format("%04d-%02d-%02d %02d:%02d:%02d",
                timeItems[0], timeItems[1], timeItems[2], timeItems[3], timeItems[4], timeItems[5]));
        View goUrlView = view.findViewById(R.id.goUrl_view);

        final AxfMesssage axfMesssage = getItem(position);
        if (axfMesssage.action.type.equals(ActionType.ShowText.name())) {
            final TextView content = (TextView) view.findViewById(R.id.content);
            final TextView more = (TextView) view.findViewById(R.id.tv_more);
            content.setText(getItem(position).content);
            content.post(new Runnable() {
                @Override
                public void run() {
                    if (content.getLineCount() >= 5) {
                        more.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.msg_main_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            }
                        });

                    } else {
                        more.setVisibility(View.GONE);
                        ((TextView) view.findViewById(R.id.content)).setText(getItem(position).content);
                    }
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

        } else if (axfMesssage.action.type.equals(ActionType.ShowText.OpenURL.name())) {

            final TextView content = (TextView) view.findViewById(R.id.content);
            final TextView more = (TextView) view.findViewById(R.id.tv_more);
            content.setText(getItem(position).content);
            content.post(new Runnable() {
                @Override
                public void run() {
                    if (content.getLineCount() >= 5) {
                        more.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.msg_main_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            }
                        });

                    } else {
                        more.setVisibility(View.GONE);
                        ((TextView) view.findViewById(R.id.content)).setText(getItem(position).content);
                    }
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

            final String args = axfMesssage.action.args;
            goUrlView.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.goUrl)).setText("点击前往");
            goUrlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WebPageActivity.class);
                    try {
                        intent.putExtra(WebPageActivity.EXTRA_URL, new JSONObject(args).getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mContext.startActivity(intent);
                }
            });
        } else if (axfMesssage.action.type.equals(ActionType.ShowText.OpenBusiness.name())) {

            final TextView content = (TextView) view.findViewById(R.id.content);
            final TextView more = (TextView) view.findViewById(R.id.tv_more);
            content.setText(getItem(position).content);
            content.post(new Runnable() {
                @Override
                public void run() {
                    if (content.getLineCount() >= 5) {
                        more.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.msg_main_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        more.setVisibility(View.GONE);
                        ((TextView) view.findViewById(R.id.content)).setText(getItem(position).content);
                    }
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

            final String args = axfMesssage.action.args;

            goUrlView.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.goUrl)).setText("点击前往");
            goUrlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String businessNo = new JSONObject(args).getString("business_no");
                        goBusiness(businessNo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (axfMesssage.action.type.equals(ActionType.TradeRecords.name())) {
            final TextView content = (TextView) view.findViewById(R.id.content);
            final TextView more = (TextView) view.findViewById(R.id.tv_more);
            final UPC2BQRPayResultPushContent upc2BQRPayResultPushContent = new Gson().fromJson(getItem(position).content, UPC2BQRPayResultPushContent.class);

            int[] timeItem = Util.getTimeItems(getItem(position).time);
            content.setText("退款金额：" + upc2BQRPayResultPushContent.getPay_amt() + "\n" + "退款时间：" + String.format("%04d-%02d-%02d %02d:%02d:%02d",
                    timeItem[0], timeItem[1], timeItem[2], timeItem[3], timeItem[4], timeItem[5]));
            content.post(new Runnable() {
                @Override
                public void run() {
                    if (content.getLineCount() >= 5) {
                        more.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.msg_main_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        more.setVisibility(View.GONE);
                        int[] timeItem = Util.getTimeItems(getItem(position).time);
                        content.setText("退款金额：" + upc2BQRPayResultPushContent.getPay_amt() + "\n" + "退款时间：" + String.format("%04d-%02d-%02d %02d:%02d:%02d",
                                timeItem[0], timeItem[1], timeItem[2], timeItem[3], timeItem[4], timeItem[5]));
                    }
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

            final String args = axfMesssage.action.args;

            goUrlView.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.goUrl)).setText("点击前往");
            goUrlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, MePayListDetailActivity.class);
                    Bundle bundle = new Bundle();
                    PaymentRecord paymentRecord =new Gson().fromJson(String.valueOf(getItem(position).content),PaymentRecord.class);
                    bundle.putSerializable(MePayListDetailActivity.EXTRA_DATA,paymentRecord);
                    i.putExtras(bundle);
                    mContext.startActivity(i);
                }
            });
        } else {
            //Node do nothing.

            final TextView content = (TextView) view.findViewById(R.id.content);
            final TextView more = (TextView) view.findViewById(R.id.tv_more);
            content.setText(getItem(position).content);
            content.post(new Runnable() {
                @Override
                public void run() {
                    if (content.getLineCount() >= 5) {
                        more.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.msg_main_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        more.setVisibility(View.GONE);
                        ((TextView) view.findViewById(R.id.content)).setText(getItem(position).content);
                    }
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LoginMsgDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LoginMsgDetailActivity.EXTRA_DATA, axfMesssage);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
        view.setTag(getItem(position));
        return view;
    }

    private void goBusiness(String no) {
        RealmResults<Business> realmResults = realm.where(Business.class).findAll();
        for (Business obj : realmResults) {
            if (Util.isEmpty(no) || Util.isEmpty(((BusinessRealmProxy) obj).realmGet$no()) || !no.equals(((BusinessRealmProxy) obj).realmGet$no())) {
                continue;
            }

            if (((BusinessRealmProxy) obj).realmGet$type().equals("Fee")) {
                final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
                final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();
                if (((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("OK")) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FeeActivity.ENTER_FLAG, FeeActivity.normal);
                    Intent intent = new Intent(mContext, FeeActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                } else {
                    mContext.startActivity(new Intent(mContext, FeeCheckActivity.class));
                }

            } else if (((BusinessRealmProxy) obj).realmGet$type().equals("ECard")) {
                final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
                final ECardAccount eCardAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account();
                if (((ECardAccountRealmProxy) eCardAccount).realmGet$status().equals("OK")) {
                    mContext.startActivity(new Intent(mContext, EcardActivity.class));
                } else {
                    mContext.startActivity(new Intent(mContext, EcardOpenActivity.class));
                }
            } else if (((BusinessRealmProxy) obj).realmGet$type().equals("Web")) {
                Intent intent = new Intent(mContext, WebPageActivity.class);
                intent.putExtra(WebPageActivity.EXTRA_URL, ((BusinessRealmProxy) obj).realmGet$arg());
                intent.putExtra(WebPageActivity.EXTRA_TITLE, ((BusinessRealmProxy) obj).realmGet$title());
                mContext.startActivity(intent);
            } else {// TdtcFee MobileRecharge MobileFlow Next Version.
                Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
