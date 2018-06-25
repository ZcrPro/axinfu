package com.zhihuianxin.xyaxf.app.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.business.Business;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.WebPageActivity;

import java.util.List;

/**
 * Created by zcrpro on 2016/10/30.
 */
public class HomeGridAdapter extends RecyclerAdapter<Business> {

    public HomeGridAdapter(Context context, @Nullable List<Business> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, final Business item) {

        if (item.container.equals("AnXinFu")) {
            if (item.status.equals("Gray")) {
                helper.setText(R.id.tv_name, item.title);
                ((TextView) (helper.getView(R.id.tv_name))).setTextColor(Color.parseColor("#9a9a9a"));
                helper.setImageUrl(R.id.image, item.icon_gray);
                helper.getItemView().setOnClickListener(null);
                helper.getItemView().setVisibility(View.VISIBLE);
            } else {
                helper.getItemView().setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_name, item.title);
                helper.setImageUrl(R.id.image, item.icon);
                ((TextView) (helper.getView(R.id.tv_name))).setTextColor(Color.parseColor("#666666"));
                helper.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!item.status.equals("Disabled")) {
                            if (item.type.equals("Web")) {
                                Intent intent = new Intent(context, WebPageActivity.class);
                                intent.putExtra(WebPageActivity.EXTRA_URL, item.arg);
                                intent.putExtra(WebPageActivity.EXTRA_TITLE, item.title);
                                context.startActivity(intent);
                            }
                            if (item.type.equals("Unuseable")) {
                                Toast.makeText(context, "尚未开通，敬请期待!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "尚未开通，敬请期待!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
