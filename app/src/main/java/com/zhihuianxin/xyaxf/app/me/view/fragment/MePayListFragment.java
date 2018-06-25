package com.zhihuianxin.xyaxf.app.me.view.fragment;

/**
 * Created by Vincent on 2016/10/24.
 */

public class MePayListFragment {
//    private IMePayListContract.IMePayListPresenter mPresenter;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
//    private StickyListHeadersListView mListView;
//    private MePayListAdapter mAdapter;
//
//    public static MePayListFragment getNewInstance(){
//        return new MePayListFragment();
//    }
//
//    @Override
//    protected void initView(View view, Bundle savedInstanceState) {
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
//        mListView = (StickyListHeadersListView) view.findViewById(R.id.stickLView);
//        view.findViewById(R.id.pay_list_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//            }
//        });
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mPresenter.loadPayClosedList();
//            }
//        });
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {}
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0)
//                    mSwipeRefreshLayout.setEnabled(true);
//                else
//                    mSwipeRefreshLayout.setEnabled(false);
//            }
//        });
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.me_paylist_fragment;
//    }
//
//    @Override
//    public void setPayClosedList(ArrayList<PayFeeRecord> payList) {
//        mAdapter = new MePayListAdapter(getActivity());
//        ArrayList<City> cityList = new ArrayList<>();
//        City c1 = new City();
//        c1.name = getString(R.string.fee1);
//        c1.quanpin = "2011-01";
//
//        cityList.add(c1);
//
//        City c2 = new City();
//        c2.name = getString(R.string.fee2);
//        c2.quanpin = "2011-01";
//        cityList.add(c2);
//
//        City c3 = new City();
//        c3.name = getString(R.string.fee3);
//        c3.quanpin = "2011-02";
//        cityList.add(c3);
//
//        City c5 = new City();
//        c5.name = getString(R.string.fee4);
//        c5.quanpin = "2011-02";
//        cityList.add(c5);
//
//        City c4 = new City();
//        c4.name = getString(R.string.fee5);
//        c4.quanpin = "2011-03";
//        cityList.add(c4);
//
////        for (int i = 0; i < 20; i++) {
////            City c = new City();
////            c.name = getString(R.string.nj);
////            c.quanpin = "2017-10-10";
////            cityList.add(c);
////        }
//
//        for (int i = 0; i < cityList.size(); i++) {
//            MePayListAdapter.CityExt ext = new MePayListAdapter.CityExt();
//            ext.city = cityList.get(i);
//
//            ext.category_value = cityList.get(i).quanpin;//.substring(0, 1).toUpperCase();
//            ext.time = "2016-01-01";
//            ext.type = "Already";
//            ext.category = ext.category_value;
//            mAdapter.add(ext);
//        }
//
//        mListView.setAdapter(mAdapter);
//    }
//
//    @Override
//    public void setPresenter(IMePayListContract.IMePayListPresenter presenter) {
//        mPresenter = presenter;
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
//        mSwipeRefreshLayout.setRefreshing(false);
//    }
}
