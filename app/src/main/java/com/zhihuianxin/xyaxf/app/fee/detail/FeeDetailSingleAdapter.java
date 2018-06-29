package com.zhihuianxin.xyaxf.app.fee.detail;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import modellib.thrift.fee.SubFee;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.adapter.FeeOtherInfoAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.SubFeeRealmProxy;

/**
 * Created by zcrpro on 2016/11/11.
 */
public class FeeDetailSingleAdapter extends RecyclerAdapter<SubFee> {

    private FeenumChangeListener listener;
    //    private FeeCheckListener checkListener;
    private FeeDetailsListener listener2;
    private HashMap<String, String> hashMap;
    private List<HashMap<String, String>> fees;
    private DecimalFormat decimalFormat;
    private NoPassListener noPassListener;
    private EDListener edListener;

    private List<HashMap<String, String>> pay_fees;
    private HashMap<String, String> pay_hash;
    private PayListener payListener;

    public FeeDetailSingleAdapter(Context context, @Nullable List<SubFee> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        fees = new ArrayList<>();
        hashMap = new HashMap<>();
        pay_fees = new ArrayList<>();
        pay_hash = new HashMap<>();
        decimalFormat = new DecimalFormat("0.00");
    }

    @Override
    protected void convert(final RecyclerAdapterHelper helper, final SubFee subFee) {

        ((RadioButton) helper.getView(R.id.radio)).setChecked(subFee.isSelected());

        helper.setText(R.id.tv_fee_name, ((SubFeeRealmProxy) subFee).realmGet$title());
        helper.setText(R.id.tv_fee_amount, ((SubFeeRealmProxy) subFee).realmGet$amount());

        if (((SubFeeRealmProxy) subFee).realmGet$min_pay_amount() != null && Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$min_pay_amount()) > 0) {
            //可拆分
            helper.getView(R.id.ll_splitable).setVisibility(View.VISIBLE);
            helper.getView(R.id.ll_not_splitable).setVisibility(View.GONE);
            helper.setText(R.id.ed_fee_amount, ((SubFeeRealmProxy) subFee).realmGet$amount());
            helper.getView(R.id.tv_fee_balance).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_fee_balance, "未缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + "0.00元");
        } else {
            helper.getView(R.id.ll_splitable).setVisibility(View.GONE);
            helper.getView(R.id.ll_not_splitable).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_fee_amount, ((SubFeeRealmProxy) subFee).realmGet$amount());
            helper.getView(R.id.tv_fee_balance).setVisibility(View.GONE);
        }

        helper.getItemView().setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                select(helper.getAdapterPosition());
                hashMap.clear();
                pay_hash.clear();
                if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                    if (edListener != null)
                        edListener.edHint(true, ((SubFeeRealmProxy) subFee).realmGet$min_pay_amount());
                    if (listener != null) {
                        listener.feeChanged(((SubFeeRealmProxy) subFee).realmGet$amount());
                    }
//                    ((EditText) helper.getView(R.id.ed_fee_amount)).setText(decimalFormat.format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())));
                    if (!TextUtils.isEmpty(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString())) {
                        hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(),
                                ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + decimalFormat.format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "元");
                        pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()+ ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
                    }else {
                        ((EditText) helper.getView(R.id.ed_fee_amount)).setText(new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "");
//                        hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + decimalFormat.format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "元");
//                        pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                    }
                } else {
                    listener.feeChanged(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
                    hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + decimalFormat.format(Float.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim())) + "元");
                    pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim()+ ":" + ((SubFeeRealmProxy) subFee).realmGet$title());

                    if (edListener != null)
                        edListener.edHint(false, ((SubFeeRealmProxy) subFee).realmGet$min_pay_amount());
                }
                fees.clear();
                fees.add(hashMap);
                pay_fees.clear();
                pay_fees.add(pay_hash);
                if (payListener != null)
                    payListener.payInfo(pay_fees);
                if (listener2 != null) {
                    listener2.feeDetailsInfo(fees);
                }
            }
        });
        ((EditText) helper.getView(R.id.ed_fee_amount)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try {
                    float x = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ((EditText) helper.getView(R.id.ed_fee_amount)).setFilters(new InputFilter[]{new InputFilterMinMax(context, 0f, Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()))});
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    try {
                        float x = Float.parseFloat(s.toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }

                    if (Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) > Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) {
                        if (noPassListener != null)
                            noPassListener.noPass(false);
                    } else if (Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) < Float.parseFloat(decimalFormat.format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$min_pay_amount())))) {
                        if (noPassListener != null)
                            noPassListener.noPass(false);
                    } else {
                        if (noPassListener != null)
                            noPassListener.noPass(true);
                    }

                    if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                        helper.getView(R.id.tv_fee_balance).setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_fee_balance, "未缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + decimalFormat.format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()) - Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "元");
                    }

                    if (((RadioButton) helper.getView(R.id.radio)).isChecked()) {
                        hashMap.clear();

                        if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                            if (listener != null) {
                                listener.feeChanged(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                            }
                            hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + decimalFormat.format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "元");
                            pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()+ ":" + ((SubFeeRealmProxy) subFee).realmGet$title());


                        } else {
                            hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + decimalFormat.format(Float.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim())) + "元");
                            pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim()+ ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
                        }
                        fees.clear();
                        fees.add(hashMap);
                        pay_fees.clear();
                        pay_fees.add(pay_hash);
                        if (payListener != null)
                            payListener.payInfo(pay_fees);
                        if (listener2 != null) {
                            listener2.feeDetailsInfo(fees);
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.feeChanged("0.00");
                    }
                    if (noPassListener != null)
                        noPassListener.noPass(false);
                    if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                        helper.setText(R.id.tv_fee_balance, "未缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元");
                    }
                }

                if (TextUtils.isEmpty(s.toString())) {
//                                        ((EditText) helper.getView(R.id.ed_fee_amount)).setText(new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "");
                    String temp = s.toString();
                    int posDot = temp.indexOf(".");
                    if (posDot <= 0) return;
                    if (temp.length() - posDot - 1 > 2) {
                        s.delete(posDot + 3, posDot + 4);
                    }
                } else {
                    String temp = s.toString();
                    int posDot = temp.indexOf(".");
                    if (posDot <= 0) return;
                    if (temp.length() - posDot - 1 > 2) {
                        s.delete(posDot + 3, posDot + 4);
                    }
                }
            }
        });

        List<String> subFees = new ArrayList<>();
        if (((SubFeeRealmProxy) subFee).realmGet$amount()!=null&&Math.abs(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) != 0) {
            subFees.add("未缴: " + ((SubFeeRealmProxy) subFee).realmGet$amount());
        }
        if (((SubFeeRealmProxy) subFee).realmGet$total_amount()!=null&&Math.abs(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$total_amount())) != 0) {
            subFees.add("应收: " + ((SubFeeRealmProxy) subFee).realmGet$total_amount());
        }
        if (((SubFeeRealmProxy) subFee).realmGet$deduct_amount()!=null&&Math.abs(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$deduct_amount())) != 0) {
            subFees.add("减免: " + ((SubFeeRealmProxy) subFee).realmGet$deduct_amount());
        }
        if (((SubFeeRealmProxy) subFee).realmGet$paid_amount()!=null&&((SubFeeRealmProxy) subFee).realmGet$loan_amt()!=null&&Math.abs((Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$paid_amount())-Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$loan_amt()))) != 0) {
            subFees.add("已缴: " + (Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$paid_amount())-Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$loan_amt())));
        }
        if (((SubFeeRealmProxy) subFee).realmGet$loan_amt()!=null&&Math.abs(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$loan_amt())) != 0) {
            subFees.add("贷款: " + ((SubFeeRealmProxy) subFee).realmGet$loan_amt());
        }
        if (((SubFeeRealmProxy) subFee).realmGet$refund_amount()!=null&&Math.abs(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$refund_amount())) != 0) {
            subFees.add("退款: " + ((SubFeeRealmProxy) subFee).realmGet$refund_amount());
        }
        ((GridView)helper.getView(R.id.gridview)).setAdapter(new FeeOtherInfoAdapter(context, subFees));
        ((GridView)helper.getView(R.id.gridview)).deferNotifyDataSetChanged();

        ((ImageView) helper.getView(R.id.iv_show_more_info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((LinearLayout) helper.getView(R.id.ll_show_fee_more)).getVisibility() == View.GONE) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            ((LinearLayout) helper.getView(R.id.ll_show_fee_more)).setVisibility(View.VISIBLE);
                            ((ImageView) helper.getView(R.id.iv_show_more_info)).setImageResource(R.drawable.btn_expand_up);
                        }
                    });
                }else {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            ((LinearLayout) helper.getView(R.id.ll_show_fee_more)).setVisibility(View.GONE);
                            ((ImageView) helper.getView(R.id.iv_show_more_info)).setImageResource(R.drawable.btn_expand_down);
                        }
                    });
                }
            }
        });

    }

    /**
     * 选择子项
     *
     * @param position
     */
    private void select(int position) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelected(false);
        }
        if (!data.get(position).isSelected()) {
            data.get(position).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public interface FeenumChangeListener {
        void feeChanged(String total);
    }

    public void feeChanged(FeenumChangeListener listener) {
        this.listener = listener;
    }


    public interface FeeDetailsListener {
        void feeDetailsInfo(List<HashMap<String, String>> details);
    }

    public void feeDetails(FeeDetailsListener listener2) {
        this.listener2 = listener2;
    }

    /**
     * 选择的是否符合大于小于最大最小金额
     */
    public interface NoPassListener {
        void noPass(boolean ispass);

    }

    public void noPass(NoPassListener noPassListener) {
        this.noPassListener = noPassListener;
    }

    /**
     * 正在编辑的提示
     */
    public interface EDListener {
        void edHint(boolean isEd, String minAmount);

    }

    public void getEdHint(EDListener edListener) {
        this.edListener = edListener;
    }

    public interface PayListener {
        void payInfo(List<HashMap<String, String>> payfees);
    }

    public void getPayinfo(PayListener payListener) {
        this.payListener = payListener;
    }

}
