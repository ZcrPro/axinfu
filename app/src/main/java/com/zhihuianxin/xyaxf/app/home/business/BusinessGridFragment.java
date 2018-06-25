package com.zhihuianxin.xyaxf.app.home.business;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.axinfu.basetools.base.BaseFragment;
import com.axinfu.modellib.thrift.business.Business;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.home.adapter.HomeGridAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmResults;

/**
 * Created by zcrpro on 2016/10/31.
 */
public class BusinessGridFragment extends BaseFragment implements BusinessGridContract.IBusinessGridView {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;

    private HomeGridAdapter homeGridAdapter;

    private BusinessGridContract.IBusinessGridPresenter presenter;
//    private BusinessGridPresenter businessGridPresenter;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
//        businessGridPresenter = new BusinessGridPresenter(this, getActivity(), SchedulerProvider.getInstance());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.business_grid_fragment;
    }

    @Override
    public void success(final RealmResults<Business> businesses) {

        if (recyclerview == null || getActivity() == null) {
            return;
        }
//        assert recyclerview != null;
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        if (businesses != null)
            homeGridAdapter = new HomeGridAdapter(getActivity(), businesses, R.layout.home_bus_item);
        recyclerview.setAdapter(homeGridAdapter);
        if (homeGridAdapter != null)
            homeGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void failure() {

    }

    @Override
    public void setPresenter(BusinessGridContract.IBusinessGridPresenter presenter) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        presenter.loadBusinessData();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
