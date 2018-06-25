package com.zhihuianxin.xyaxf.app.ocp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.adapter.StrategyAdapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by zcrpro on 2017/11/24.
 */

public class StrategyDialog extends Dialog {

    private Button next;
    private TextView pay_amount;
    private TextView org_amount;
    private TextView total_amount;
    private ImageView back;
    private LinearLayout ll_total_amount;
    private RecyclerView recyclerview;
    private Context context;

    private List<PricingStrategy> pricingStrategies;

    private float num;
    private String orgAmount;
    private String payAmount;

    private OnNextCilckListener listener;


    public StrategyDialog(Context context, List<PricingStrategy> pricingStrategies, String orgAmount, String payAmount) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.pricingStrategies = pricingStrategies;
        this.orgAmount = orgAmount;
        this.payAmount = payAmount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_window);
        initViews();
        recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerview.setHasFixedSize(true);
        StrategyAdapter strategyAdapter = new StrategyAdapter(context, pricingStrategies, R.layout.strategy_list_item);
        recyclerview.setAdapter(strategyAdapter);
        strategyAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        next = (Button) findViewById(R.id.next);
        pay_amount = (TextView) findViewById(R.id.pay_amount);
        org_amount = (TextView) findViewById(R.id.org_amount);
        back = (ImageView) findViewById(R.id.back);
        total_amount = (TextView) findViewById(R.id.total_amount);
        ll_total_amount = (LinearLayout) findViewById(R.id.ll_total_amount);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);

        if (pricingStrategies != null && pricingStrategies.size() <= 1) {
            ll_total_amount.setVisibility(View.GONE);
        } else {
            ll_total_amount.setVisibility(View.VISIBLE);
            for (int i = 0; i < pricingStrategies.size(); i++) {
                num = num + Float.parseFloat(pricingStrategies.get(i).float_amt);
            }
            total_amount.setText("" + totalMoney(num));
        }

        if(Float.parseFloat(payAmount)<=0){
            pay_amount.setText("¥ 0.00");
        }else {
            pay_amount.setText("¥ " + new DecimalFormat("0.00").format(Float.parseFloat(payAmount)));
        }
        org_amount.setText(orgAmount);
        org_amount.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        //全减免的情况
        if (num == Float.parseFloat(payAmount)) {
            //全部减免的情况
            next.setText("确认");
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (num == Float.parseFloat(payAmount)||Float.parseFloat(payAmount)<0) {
                        //全部减免的情况
                        listener.next("0");
                    } else {
                        listener.next(payAmount);
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setListener(OnNextCilckListener listener) {
        this.listener = listener;
    }

    public interface OnNextCilckListener {
        void next(String payAmount);
    }

    /**
     * 四舍五入保留两位
     *
     * @param money
     * @return
     */
    public static double totalMoney(double money) {
        BigDecimal bigDec = new BigDecimal(money);
        double total = bigDec.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.parseDouble(df.format(total));
    }
}
