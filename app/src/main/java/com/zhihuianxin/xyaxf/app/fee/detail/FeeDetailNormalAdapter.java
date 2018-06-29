package com.zhihuianxin.xyaxf.app.fee.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import modellib.thrift.fee.SubFee;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.SubFeeRealmProxy;

/**
 * Created by zcrpro on 2016/11/12.
 */
public class FeeDetailNormalAdapter extends RecyclerAdapter<SubFee> {

    private float totalNormal;
    private FeeCheckListener feeCheckListener;
    private List<HashMap<String, String>> fees;
    private FeeDetailsListener listener;
    private HashMap<String, String> hashMap;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    private CheckAllListener checkAllListener;
    //选中项目的计数
    private int num;
    private float beforeAmount;
    private DecimalFormat decimalFormat;
    private NoPassListener noPassListener;
    private EDListener edListener;

    private List<HashMap<String, String>> pay_fees;
    private HashMap<String, String> pay_hash;
    private PayListener payListener;
    private Float toa;
    private int x;

    public FeeDetailNormalAdapter(Context context, @Nullable List<SubFee> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        fees = new ArrayList<>();
        hashMap = new HashMap<>();
        pay_fees = new ArrayList<>();
        pay_hash = new HashMap<>();
        isSelected = new HashMap<Integer, Boolean>();
        assert data != null;
        for (int i = 0; i < data.size(); i++) {
//            getIsSelected().put(i, false);
            /**
             * 关于必缴项
             */
            if (((SubFeeRealmProxy) data.get(i)).realmGet$is_priority()) {
                getIsSelected().put(i, true);
                totalNormal = totalNormal + Float.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount());
                num = num + 1;
                x = x + 1;
            } else {
                getIsSelected().put(i, false);
            }
        }
        decimalFormat = new DecimalFormat("0.00");
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        FeeDetailNormalAdapter.isSelected = isSelected;
    }

    @Override
    protected void convert(final RecyclerAdapterHelper helper, final SubFee subFee) {
        /**
         * 关于必缴项
         */
        if (((SubFeeRealmProxy) subFee).realmGet$is_priority()) {
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
            ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon1);
        } else {
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
            ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon2);
        }

        if (x == data.size()) {
            if (checkAllListener != null) {
                checkAllListener.isCheckAll(true, false);
            }
        }

        if (getIsSelected() != null)
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(getIsSelected().get(helper.getAdapterPosition()));

        helper.setText(R.id.tv_fee_name, ((SubFeeRealmProxy) subFee).realmGet$title());

        if (((SubFeeRealmProxy) subFee).realmGet$min_pay_amount() != null && Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$min_pay_amount()) > 0) {
            //可拆分
            helper.getView(R.id.ll_splitable).setVisibility(View.VISIBLE);
            helper.getView(R.id.ll_not_splitable).setVisibility(View.GONE);
            helper.setText(R.id.ed_fee_amount, new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "");
            helper.getView(R.id.tv_fee_balance).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_fee_balance, "应缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + "0.00元");
        } else {
            helper.getView(R.id.ll_splitable).setVisibility(View.GONE);
            helper.getView(R.id.ll_not_splitable).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_fee_balance).setVisibility(View.GONE);
            helper.setText(R.id.tv_fee_amount, new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "");
        }

        //外派选择的金额出去
        if (listener != null)
            listener.feeDetailsInfo(fees);
        if (payListener != null)
            payListener.payInfo(pay_fees);
        if (feeCheckListener != null)
            feeCheckListener.feeTotalAmount(totalNormal);

        if (((CheckBox) helper.getView(R.id.checkbox)).isChecked()) {
            ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon5);
            if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
//                totalNormal = totalNormal + Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
            } else if (helper.getView(R.id.ll_not_splitable).getVisibility() == View.VISIBLE) {
//                totalNormal = totalNormal + Float.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
                hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
                pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
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
            toa = totalNormal;
        }else {
            ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon2);
        }

        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((SubFeeRealmProxy) subFee).realmGet$is_priority()) {
                    if (((CheckBox) helper.getView(R.id.checkbox)).isChecked()) {
                        ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
                        ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon2);
                        //删除掉这个position下的选中状态
                        getIsSelected().put(helper.getAdapterPosition(), false);
                        num--;
                        hashMap.remove(((SubFeeRealmProxy) subFee).realmGet$id());
                        pay_hash.remove(((SubFeeRealmProxy) subFee).realmGet$id());
                        //存储相应的缴费项
                        if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                            if (!TextUtils.isEmpty(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString())) {
                                totalNormal = totalNormal - Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                            } else {
                                totalNormal = totalNormal - 0;
                            }
                        } else if (helper.getView(R.id.ll_not_splitable).getVisibility() == View.VISIBLE) {
                            totalNormal = totalNormal - Float.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
                        }
                        //如果取消一个,则不是全选
                        if (checkAllListener != null) {
                            x = 0;
                            checkAllListener.isCheckAll(false, true);
                        }

                    } else {
                        num++;
                        getIsSelected().put(helper.getAdapterPosition(), true);
//                        ((EditText) helper.getView(R.id.ed_fee_amount)).setText(new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "");
                        ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
                        ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon5);
                        if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                            if (!TextUtils.isEmpty(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString())) {
                                totalNormal = totalNormal + Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                            } else {
                                ((EditText) helper.getView(R.id.ed_fee_amount)).setText(new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "");
//                                totalNormal = totalNormal + Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount());
                            }
                            hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                            pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim());
                        } else if (helper.getView(R.id.ll_not_splitable).getVisibility() == View.VISIBLE) {
                            totalNormal = totalNormal + Float.parseFloat(((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
                            hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
                            pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + ((TextView) helper.getView(R.id.tv_fee_amount)).getText().toString().trim());
                        }

                        //添加这个的选中状态
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
                    if (feeCheckListener != null) {
                        if (totalNormal <= 0) {
                            feeCheckListener.feeTotalAmount(0.00f);
                        } else {
                            feeCheckListener.feeTotalAmount(totalNormal);
                        }
                    }
                }
            }
        });


        ((EditText) helper.getView(R.id.ed_fee_amount)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                toa = totalNormal;
                if (hasFocus) {
                    ((EditText) helper.getView(R.id.ed_fee_amount)).addTextChangedListener(
                            new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    if (((CheckBox) helper.getView(R.id.checkbox)).isChecked()) {
                                        if (!s.toString().equals("")) {
                                            try {
                                                float x = Float.parseFloat(s.toString());
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                return;
                                            }
                                            beforeAmount = Float.parseFloat(s.toString().trim());
                                        } else {
                                            beforeAmount = 0;
                                        }
                                    }
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    ((EditText) helper.getView(R.id.ed_fee_amount)).setFilters(new InputFilter[]{new InputFilterMinMax(context, 0f, Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()))});
                                    if (((CheckBox) helper.getView(R.id.checkbox)).isChecked()) {
                                        if (!TextUtils.isEmpty(s.toString())) {
                                            try {
                                                float x = Float.parseFloat(s.toString());
                                                if (Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) > Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) {
                                                    if (noPassListener != null)
                                                        noPassListener.noPass(false);
                                                } else if (Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) < Float.parseFloat(decimalFormat.format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$min_pay_amount()))) && Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) < Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) {
                                                    if (noPassListener != null)
                                                        noPassListener.noPass(false);
                                                } else {
                                                    if (noPassListener != null)
                                                        noPassListener.noPass(true);
                                                }

                                                if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                                                    if (toa != null) {
                                                        if (num == data.size()) {
                                                            Log.d("FeeDetailNormalAdapter", "totalNormal0:" + totalNormal);
                                                            totalNormal = toa - Float.parseFloat(((SubFeeRealmProxy) data.get(helper.getAdapterPosition())).realmGet$amount()) + Float.parseFloat(s.toString());
                                                            Log.d("FeeDetailNormalAdapter", "toa:" + toa);
                                                            Log.d("FeeDetailNormalAdapter", "subFee:" + Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()));
                                                            Log.d("FeeDetailNormalAdapter", "float string:" + Float.parseFloat(s.toString()));
                                                            Log.d("FeeDetailNormalAdapter", "totalNormal1:" + totalNormal);
                                                        } else {
//                                                if (beforeAmount < totalNormal){
                                                            Log.d("FeeDetailNormalAdapter", "totalNormal1.5:" + totalNormal);
                                                            totalNormal = totalNormal - beforeAmount + Float.parseFloat(s.toString());
                                                            Log.d("FeeDetailNormalAdapter", "before:" + beforeAmount);
                                                            Log.d("FeeDetailNormalAdapter", "totalNormal2:" + totalNormal);
//                                                }else {
//                                                    Log.d("FeeDetailNormalAdapter", "totalNormal3:" + totalNormal);
//                                                }
                                                        }
                                                    } else {
                                                        Log.d("FeeDetailNormalAdapter", "totalNormal4:" + totalNormal);
                                                        totalNormal = totalNormal - beforeAmount + Float.parseFloat(s.toString());
                                                        Log.d("FeeDetailNormalAdapter", "totalNormal5:" + totalNormal);
                                                    }
                                                    hashMap.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$title() + ":" + decimalFormat.format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "");
                                                    pay_hash.put(((SubFeeRealmProxy) subFee).realmGet$id(), ((SubFeeRealmProxy) subFee).realmGet$amount() + ":" + decimalFormat.format(Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "");
                                                    helper.getView(R.id.tv_fee_balance).setVisibility(View.VISIBLE);
                                                    if (Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()) - Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim()) <= 0) {
                                                        helper.setText(R.id.tv_fee_balance, "应缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余0.00元");
                                                    } else {
                                                        helper.setText(R.id.tv_fee_balance, "应缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + decimalFormat.format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount()) - Float.parseFloat(((EditText) helper.getView(R.id.ed_fee_amount)).getText().toString().trim())) + "元");
                                                    }

                                                }

                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                return;
                                            }

                                        } else {

                                            if (toa != null) {
                                                if (num == data.size()) {
                                                    totalNormal = toa - Float.parseFloat(((SubFeeRealmProxy) data.get(helper.getAdapterPosition())).realmGet$amount());
                                                    if (feeCheckListener != null)
                                                        feeCheckListener.feeTotalAmount(totalNormal);
                                                } else {
                                                    totalNormal = totalNormal - beforeAmount;
                                                    if (feeCheckListener != null)
                                                        feeCheckListener.feeTotalAmount(totalNormal);
                                                }
                                            } else {
                                                totalNormal = totalNormal - beforeAmount;
                                                if (feeCheckListener != null)
                                                    feeCheckListener.feeTotalAmount(totalNormal);
                                            }

                                            if (noPassListener != null)
                                                noPassListener.noPass(false);
                                            if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                                                helper.setText(R.id.tv_fee_balance, "应缴" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元,剩余" + new DecimalFormat("0.00").format(Float.parseFloat(((SubFeeRealmProxy) subFee).realmGet$amount())) + "元");
                                            }
                                        }

                                        //外派当前正在编辑的金额
                                        if (helper.getView(R.id.ll_splitable).getVisibility() == View.VISIBLE) {
                                            if (edListener != null)
                                                edListener.edHint(true, ((SubFeeRealmProxy) subFee).realmGet$min_pay_amount());
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

                                @Override
                                public void afterTextChanged(Editable s) {
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
                            }
                    );
                }
            }
        });

        if (getIsSelected() != null)
            ((CheckBox) helper.getView(R.id.checkbox)).setChecked(getIsSelected().get(helper.getAdapterPosition()));

    }

    public interface FeeCheckListener {
        void feeTotalAmount(float amounts);
    }

    public void feeCheckd(FeeCheckListener feeCheckListener) {
        this.feeCheckListener = feeCheckListener;
    }

    public interface FeeDetailsListener {
        void feeDetailsInfo(List<HashMap<String, String>> details);
    }

    public void feeDetails(FeeDetailsListener listener) {
        this.listener = listener;
    }

    /**
     * 全选按钮
     */
    public void selectedAll() {
        num = data.size();
        totalNormal = 0;
        for (int i = 0; i < data.size(); i++) {
            getIsSelected().put(i, true);
            totalNormal = totalNormal + Float.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount());
            hashMap.put(((SubFeeRealmProxy) data.get(i)).realmGet$id(), ((SubFeeRealmProxy) data.get(i)).realmGet$title() + ":" + decimalFormat.format(Float.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount())));
            pay_hash.put(((SubFeeRealmProxy) data.get(i)).realmGet$id(), ((SubFeeRealmProxy) data.get(i)).realmGet$amount() + ":" + decimalFormat.format(Float.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount())));
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
        toa = totalNormal;
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
                totalNormal = totalNormal + Float.parseFloat(((SubFeeRealmProxy) data.get(i)).realmGet$amount());
                num = num + 1;
            } else {
                x = 0;
                getIsSelected().put(i, false);
                hashMap.remove(((SubFeeRealmProxy) data.get(i)).realmGet$id());
                pay_hash.remove(((SubFeeRealmProxy) data.get(i)).realmGet$id());
            }
        }
        notifyDataSetChanged();
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


    /**
     * 正在编辑的提示
     */
    public interface PayListener {
        void payInfo(List<HashMap<String, String>> payfees);
    }

    public void getPayinfo(PayListener payListener) {
        this.payListener = payListener;
    }

}
