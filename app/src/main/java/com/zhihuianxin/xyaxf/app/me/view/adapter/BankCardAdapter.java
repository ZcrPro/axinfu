package com.zhihuianxin.xyaxf.app.me.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.axinfu.modellib.thrift.bankcard.BankCard;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zcrprozcrpro on 2017/5/18.
 */

public class BankCardAdapter extends RecyclerAdapter<BankCard> {

    private Listener listener;
    private List<String> id_s;
    // 用来控制CheckBox的选中状况
    private static HashMap<String, Boolean> isSelected;
    private Boolean showDel = false;

    public BankCardAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public BankCardAdapter(Context context, @Nullable List<BankCard> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        id_s = new ArrayList<>();
        isSelected = new HashMap<String, Boolean>();
    }

    public BankCardAdapter(Context context, @Nullable List<BankCard> data, Boolean showDel, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        id_s = new ArrayList<>();
        isSelected = new HashMap<String, Boolean>();
    }

    @Override
    protected void convert(final RecyclerAdapterHelper helper, final BankCard item) {

        if (showDel) {
            ((CheckBox) helper.getView(R.id.checkbox)).setVisibility(View.VISIBLE);
        } else {
            ((CheckBox) helper.getView(R.id.checkbox)).setVisibility(View.GONE);
        }

        helper.setText(R.id.bank_name, item.bank_name);
        helper.setText(R.id.card_no, getIDValue(item.card_no));

        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每次点击都去结算被选项的id
                if (!isSelected.containsKey(item.id)) {
                    isSelected.put(item.id, true);
                    id_s.add(item.id);
                } else {
                    isSelected.remove(item.id);
                    id_s.remove(item.id);
                }

                if (isSelected.containsKey(item.id)) {
                    ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
                    ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.icon_check_box_red_ch);
                } else {
                    ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
                    ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.icon_check_box_red_unch);
                }

                if (listener != null) {
                    listener.getDelList(id_s);
                }

            }
        });

    }

    private String getIDValue(String oriID) {
        if (oriID != null) {
            if (oriID.length() != 18) {
                return oriID;
            } else {
                return oriID.substring(0, 2) + "**************" + oriID.substring(16);
            }
        } else {
            return null;
        }
    }

    /**
     * 正在编辑的提示
     */
    public interface Listener {
        void getDelList(List<String> id_s);

    }

    public void getDel(Listener listener) {
        this.listener = listener;
    }


    public void showDel() {
        showDel = true;
        notifyDataSetChanged();
    }

    public void UnshowDel() {
        showDel = false;
        notifyDataSetChanged();
    }
}
