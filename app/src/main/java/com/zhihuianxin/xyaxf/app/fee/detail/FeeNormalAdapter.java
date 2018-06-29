package com.zhihuianxin.xyaxf.app.fee.detail;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import modellib.thrift.fee.SubFee;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.xyaxf.axpay.Util;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.adapter.FeeOtherInfoAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.SubFeeRealmProxy;

/**
 * Created by zcrpro on 2017/1/8.
 */

public class FeeNormalAdapter extends RecyclerAdapter<SubFee> {

    private List<HashMap<String, String>> pay_fees;
    private HashMap<String, String> pay_hash;
    private HashMap<String, String> hashMap;
    private List<HashMap<String, String>> fees;
    private float totalNormal;
    private NoPassListener noPassListener;

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    private FeeCheckListener feeCheckListener;
    private CheckAllListener checkAllListener;
    private FeeDetailsListener listener;
    private PayListener payListener;
    private EDListener edListener;

    private HashMap<String, Float> hash_item_amount;

    private HashMap<View, TextWatcher> viewTextWatcherMap = new HashMap<>();

    //选中项目的计数
    private int num;

    private int priority_num;

    public FeeNormalAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public FeeNormalAdapter(Context context, @Nullable List<SubFee> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        fees = new ArrayList<>();
        pay_fees = new ArrayList<>();
        hashMap = new HashMap<>();
        pay_hash = new HashMap<>();
        hash_item_amount = new HashMap<>();
        isSelected = new HashMap<Integer, Boolean>();

        assert data != null;
        for (int i = 0; i < data.size(); i++) {
            if (((SubFeeRealmProxy) data.get(i)).realmGet$is_priority()) {
                getIsSelected().put(i, true);
                totalNormal = totalNormal + Util.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount(), -2f);
                num = num + 1;
                priority_num = priority_num + 1;
            } else {
                getIsSelected().put(i, false);
            }

            hash_item_amount.put(((SubFeeRealmProxy) data.get(i)).realmGet$id(), Float.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount()));
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        FeeNormalAdapter.isSelected = isSelected;
    }

    @Override
    protected void convert(final RecyclerAdapterHelper helper, final SubFee subFee) {
        View amtView = helper.getView(R.id.ed_fee_amount);
        if (viewTextWatcherMap.containsKey(amtView)) {
            ((EditText) amtView).removeTextChangedListener(viewTextWatcherMap.get(amtView));
            viewTextWatcherMap.remove(amtView);
        }

        ((EditText) helper.getView(R.id.ed_fee_amount)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    //外派当前正在编辑的金额
                    if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                        if (((EditText) helper.getView(R.id.ed_fee_amount)).hasFocus()) {
                            if (edListener != null)
                                edListener.edHint(true, ((SubFeeRealmProxy) subFee).realmGet$min_pay_amount());
                        }
                    }
                }
            }
        });

        TextWatcher watcher = new TextWatcher() {
            private String lastInput;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void innerOnTextChanged(CharSequence s) {

//                            if (s.toString().trim().equals("\\.") && ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().contains("\\.")) {
//                                return;
//                            }

                if (!TextUtils.isEmpty(s.toString().trim())) {
                    hash_item_amount.put(((SubFeeRealmProxy) subFee).realmGet$id(), Util.parseFloat(s.toString().trim(), -2f));
                    if (getIsSelected().get(helper.getAdapterPosition())) {
                        hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(),
                                ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + (Float.parseFloat(s.toString().trim())) + "元");
                        pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + (s.toString().trim()) + ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
                    }
                    if (Float.parseFloat(s.toString()) > Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) {
                        if (noPassListener != null)
                            noPassListener.noPass(false);
                    } else if (Float.parseFloat(s.toString()) < Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$min_pay_amount())) {
                        if (noPassListener != null)
                            noPassListener.noPass(false);
                    } else {
                        if (noPassListener != null)
                            noPassListener.noPass(true);
                    }


                    if (Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()) - Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) <= 0) {
                        helper.setText(R.id.tv_fee_balance, "未缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余0.00元");
                    } else {
                        helper.setText(R.id.tv_fee_balance, "未缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()) - Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "元");
                    }

                } else {
                    helper.setText(R.id.tv_fee_balance, "未缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元");
                    if (noPassListener != null)
                        noPassListener.noPass(false);
                    hash_item_amount.remove(((SubFeeRealmProxy) subFee).realmGet$id());
                }
            }


            @Override
            public void afterTextChanged(Editable s) {

                String currentInput = s.toString();
                if (currentInput.equals(lastInput)) {
                    return;
                }

                if (s.toString().length() > 0) {
                    try {
                        Float.parseFloat(s.toString().trim());
                    } catch (NumberFormatException e) {
                        if (lastInput == null) {
                            s.replace(0, currentInput.length(), "");
                        } else {
                            s.replace(0, currentInput.length(), lastInput);
                        }
                        return;
                    }
                }

                if (!TextUtils.isEmpty(s.toString().trim()) && Float.parseFloat(s.toString().trim()) > Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) {
                    showToast((Activity) context, "输入的金额不能超出应缴费金额", 500);
                    if (lastInput == null) {
                        s.replace(0, currentInput.length(), "");
                    } else {
                        s.replace(0, currentInput.length(), lastInput);
                    }
                    return;
                }

                innerOnTextChanged(s);

                lastInput = currentInput;

                totalNormal = 0;
                Iterator iter = getIsSelected().entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object val = entry.getValue();
                    if ((boolean) val) {
                        Object key = entry.getKey();
                        if (hash_item_amount.get(((SubFeeRealmProxy) data.get((int) key)).realmGet$id()) != null)
                            totalNormal = totalNormal + hash_item_amount.get(((SubFeeRealmProxy) data.get((int) key)).realmGet$id());
                    }
                }

                if (feeCheckListener != null)
                    feeCheckListener.feeTotalAmount(totalNormal);

                if (TextUtils.isEmpty(s.toString())) {
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

                fees.clear();
                fees.add(hashMap);
                pay_fees.clear();
                pay_fees.add(pay_hash);
                //外派选择的金额出去
                if (listener != null)
                    listener.feeDetailsInfo(fees);
                if (payListener != null)
                    payListener.payInfo(pay_fees);
                if (feeCheckListener != null)
                    feeCheckListener.feeTotalAmount(totalNormal);
            }
        };

        viewTextWatcherMap.put(amtView, watcher);
        ((EditText) helper.getView(R.id.ed_fee_amount)).addTextChangedListener(watcher);

        //关于必缴项
        if (((SubFeeRealmProxy) subFee).realmGet$is_priority()) {
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
            ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon1);
        } else {
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
            ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.fee_check);
        }

        if (getIsSelected() != null)
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(getIsSelected().get(helper.getLayoutPosition()));

        //如果全是必缴项--全选按钮不可点击
        if (priority_num == data.size()) {
            if (checkAllListener != null)
                checkAllListener.isCheckAll(true, false);
        }

        //判断是否可以拆分金额
        if (((SubFeeRealmProxy) subFee).realmGet$min_pay_amount() != null && Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$min_pay_amount(), -2f) > 0) {
            //可拆分
            helper.getView(R.id.ll_splitable).setVisibility(View.VISIBLE);
            helper.getView(R.id.ll_not_splitable).setVisibility(View.GONE);
            if (hash_item_amount.get(((SubFeeRealmProxy) subFee).realmGet$id()) != null) {
                helper.setText(R.id.ed_fee_amount, new DecimalFormat("0.00").format(hash_item_amount.get(((SubFeeRealmProxy) subFee).realmGet$id())) + "");
                Log.d("FeeNormalAdapter", "hash_item_amount.get(helper.getLayoutPosition()):" + hash_item_amount.get(((SubFeeRealmProxy) subFee).realmGet$id()));
            } else {
                hash_item_amount.put(((SubFeeRealmProxy) subFee).realmGet$id(), Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f));
                helper.setText(R.id.ed_fee_amount, new DecimalFormat("0.00").format(Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f)) + "");
                Log.d("FeeNormalAdapter", "hash_item_amount.get(helper.getLayoutPosition())2:" + Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f) + "");
            }
            helper.getView(R.id.tv_fee_balance).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_fee_balance, "未缴" + new DecimalFormat("0.00").format(Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f)) + "元,剩余" + new DecimalFormat("0.00").format((Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f) - Util.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim(), -2f))) + "元");
        } else {
            helper.getView(R.id.ll_splitable).setVisibility(View.GONE);
            helper.getView(R.id.ll_not_splitable).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_fee_balance).setVisibility(View.GONE);
            if (hash_item_amount.get(((SubFeeRealmProxy) subFee).realmGet$id()) != null) {
                helper.setText(R.id.tv_fee_amount, new DecimalFormat("0.00").format(hash_item_amount.get(((SubFeeRealmProxy) subFee).realmGet$id())) + "");
            } else {
                hash_item_amount.put(((SubFeeRealmProxy) subFee).realmGet$id(), Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f));
                helper.setText(R.id.tv_fee_amount, new DecimalFormat("0.00").format(Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f)) + "");
            }
        }

        if (getIsSelected().get(helper.getLayoutPosition())) {
            if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
//                if (hash_item_amount.get(helper.getLayoutPosition()) != null) {
//                    hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + hash_item_amount.get(helper.getLayoutPosition()) + "");
//                    pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + hash_item_amount.get(helper.getLayoutPosition()) + "");
//                } else {
                hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(),
                        ((SubFeeRealmProxy) subFee).realmGet$title() + ":" +
                                Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) + "元");
                pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim() + ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
//                }
            } else if (helper.getView(R.id.ll_not_splitable).getVisibility() == View.VISIBLE) {
                hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(),
                        ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim() + "元");
                pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim() + ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
            }

            fees.clear();
            fees.add(hashMap);
            pay_fees.clear();
            pay_fees.add(pay_hash);
            //外派选择的金额出去
            if (listener != null)
                listener.feeDetailsInfo(fees);
            if (payListener != null)
                payListener.payInfo(pay_fees);
            if (feeCheckListener != null)
                feeCheckListener.feeTotalAmount(totalNormal);
        }

        //显示缴费标题
        helper.setText(R.id.tv_fee_name, ((SubFeeRealmProxy) subFee).realmGet$title());

        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick(helper, subFee);
            }
        });

        helper.getView(R.id.view_ck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick(helper, subFee);
            }
        });

        if (getIsSelected() != null)
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(getIsSelected().get(helper.getLayoutPosition()));

        fees.clear();
        fees.add(hashMap);
        pay_fees.clear();
        pay_fees.add(pay_hash);
        //外派选择的金额出去
        if (listener != null)
            listener.feeDetailsInfo(fees);
        if (payListener != null)
            payListener.payInfo(pay_fees);
        if (feeCheckListener != null)
            feeCheckListener.feeTotalAmount(totalNormal);


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


        if (((SubFeeRealmProxy) subFee).realmGet$deduct_amount() == null
                && ((SubFeeRealmProxy) subFee).realmGet$paid_amount() == null
                && ((SubFeeRealmProxy) subFee).realmGet$total_amount() == null) {
            ((ImageView) helper.getView(R.id.iv_show_more_info)).setVisibility(View.GONE);
        } else {
            ((ImageView) helper.getView(R.id.iv_show_more_info)).setVisibility(View.VISIBLE);
        }

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
                } else {
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
     * 点击每个item的逻辑
     *
     * @param helper
     * @param subFee
     */
    private void itemClick(RecyclerAdapterHelper helper, SubFee subFee) {
        //确定并不是必选项目
        if (!((SubFeeRealmProxy) subFee).realmGet$is_priority()) {
            //如果是选中的状态
            if (((CheckBox) helper.getView(R.id.checkbox)).isChecked()) {
                ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
                ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon2);
                //删除掉这个position下的选中状态
                getIsSelected().put(helper.getLayoutPosition(), false);
                num--;
                hashMap.remove(((SubFeeRealmProxy) subFee).realmGet$id());
                pay_hash.remove(((SubFeeRealmProxy) subFee).realmGet$id());
                //存储相应的缴费项
                if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                    if (!TextUtils.isEmpty(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString())) {
                        totalNormal = totalNormal - Util.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim(), -2f);
                    } else {
                        totalNormal = totalNormal - 0;
                    }
                } else if (helper.getView(R.id.ll_not_splitable).getVisibility() == View.VISIBLE) {
                    totalNormal = totalNormal - Util.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim(), -2f);
                }
                //如果取消一个,则不是全选
                if (checkAllListener != null) {
                    checkAllListener.isCheckAll(false, true);
                }
            } else {
                //如果是未选中状态
                num++;
                getIsSelected().put(helper.getLayoutPosition(), true);
                ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
                ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon5);
                if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                    if (!TextUtils.isEmpty(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString())) {
                        totalNormal = totalNormal + Util.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim(), -2f);
                        hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(),
                                ((SubFeeRealmProxy) subFee).realmGet$title() + ":" +
                                        new DecimalFormat("0.00").format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "元");
                        pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + new DecimalFormat("0.00").format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
                    } else {
//                        ((EditText) helper.getView(R.id.ed_fee_amount)).setText(new DecimalFormat("0.0").format(Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f)));
//                        totalNormal = totalNormal + Util.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount(), -2f);
//                        hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(),
//                                ((SubFeeRealmProxy) subFee).realmGet$title() + ":" +
//                                        new DecimalFormat("0.00").format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()))+"元");
//                        pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + new DecimalFormat("0.00").format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
                    }

                } else if (helper.getView(R.id.ll_not_splitable).getVisibility() == View.VISIBLE) {
                    totalNormal = totalNormal + Util.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim(), -2f);
                    hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(),
                            ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + new DecimalFormat("0.00").format(Float.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim())) + "元");
                    pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + new DecimalFormat("0.00").format(Float.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim())) + ":" + ((SubFeeRealmProxy) subFee).realmGet$title());
                }

                //遍历所有的data进行判断是否全部选中了
                if (num == data.size()) {
                    if (checkAllListener != null) {
                        checkAllListener.isCheckAll(true, true);
                    }
                }
            }

            fees.clear();
            fees.add(hashMap);
            pay_fees.clear();
            pay_fees.add(pay_hash);
            if (listener != null)
                listener.feeDetailsInfo(fees);
            if (payListener != null)
                payListener.payInfo(pay_fees);
            if (feeCheckListener != null)
                feeCheckListener.feeTotalAmount(totalNormal);
        }

    }

    /**
     * 是否全选的监听事件,控制主界面的全选checkbox
     */
    public interface CheckAllListener {
        void isCheckAll(boolean checkall, boolean enable);
    }

    public void isCheckAll(CheckAllListener checkAllListener) {
        this.checkAllListener = checkAllListener;
    }

    /**
     * 总金额的外派
     */
    public interface FeeCheckListener {
        void feeTotalAmount(float amounts);
    }

    public void feeCheckd(FeeCheckListener feeCheckListener) {
        this.feeCheckListener = feeCheckListener;
    }

    /**
     * 全选按钮
     */
    public void selectedAll() {
        num = data.size();
        totalNormal = 0;
        //遍历存储的item的值
        Iterator iter = hash_item_amount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object val = entry.getValue();
            totalNormal = totalNormal + (Float) val;
        }
        for (int i = 0; i < data.size(); i++) {
            getIsSelected().put(i, true);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void notSelectedAll() {
        num = 0;
        totalNormal = 0;
        // 遍历list的长度，将已选的设为未选，未选的设为已选
        for (int i = 0; i < data.size(); i++) {
            if (((SubFeeRealmProxy) data.get(i)).realmGet$is_priority()) {
                getIsSelected().put(i, true);
                totalNormal = totalNormal + Util.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount(), -2f);
                num = num + 1;
            } else {
                getIsSelected().put(i, false);
                hashMap.remove(((SubFeeRealmProxy) data.get(i)).realmGet$id());
                pay_hash.remove(((SubFeeRealmProxy) data.get(i)).realmGet$id());
            }
        }
        notifyDataSetChanged();
    }

    public interface FeeDetailsListener {
        void feeDetailsInfo(List<HashMap<String, String>> details);
    }

    public void feeDetails(FeeDetailsListener listener) {
        this.listener = listener;
    }

    /**
     * 正在编辑的提示
     */
    public interface PayListener {
        void payInfo(List<HashMap<String, String>> payfees);
    }

    public void getPayinfo(PayListener payListener) {
        this.payListener = payListener;
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


    public static void showToast(final Activity activity, final String word, final long time) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_LONG);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, time);
            }
        });
    }
}
