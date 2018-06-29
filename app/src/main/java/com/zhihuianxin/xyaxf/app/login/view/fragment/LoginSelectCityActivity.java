package com.zhihuianxin.xyaxf.app.login.view.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.axinfu.modellib.thrift.resource.City;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginSelectCityContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginSelectCityPresenter;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginInputMobilActivityNew;
import com.zhihuianxin.xyaxf.app.login.view.adapter.LoginSearchCityListAdapter;
import com.zhihuianxin.xyaxf.app.login.view.adapter.LoginSelectCityAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CityRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Vincent on 2016/10/20.
 */

public class LoginSelectCityActivity extends BaseRealmActionBarActivity implements ILoginSelectCityContract.ISelectCityView {
    public static final String EXTRA_FROM_LOGIN = "from_login";

    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.stickListView)
    StickyListHeadersListView mListView;
    @InjectView(R.id.search_list)
    ListView mSearchList;
    @InjectView(R.id.graySearchBg)
    View mSearchGrayBg;
    @InjectView(R.id.search_edit)
    EditText mSearchEdit;
    @InjectView(R.id.bar)
    View mActionBarView;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.backAnimView)
    View mBackAlertView;
    @InjectView(R.id.side_bar)
    WaveSideBar sideBar;

    private DisplayMetrics metrics;
    private ILoginSelectCityContract.ISelectCityPresenter mPresenter;
    private LoginSelectCityAdapter mAdapter;
    private LoginSearchCityListAdapter searchListAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.login_select_city_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        findViewById(R.id.action_bar).setVisibility(View.GONE);
        new LoginSelectCityPresenter(this, this);
        initView();
        ActivityCollector.addActivity(this);
    }

    private void initView() {
        mAdapter = new LoginSelectCityAdapter(this);
        mListView.setAdapter(mAdapter);
        mSearchEdit.addTextChangedListener(textWatcher);

        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        mListView.setOnScrollListener(scrollListener);
        mListView.setOnItemClickListener(itemClickListener);

        final RealmResults<City> results = realm.where(City.class).findAll();
        if (results.size() == 0)
            mPresenter.loadCity();
        else {
            sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
                @Override
                public void onSelectIndexItem(final String index) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < results.size(); i++) {
                                if (((CityRealmProxy) results.get(i)).realmGet$quanpin().substring(0, 1).toUpperCase().equals(index)) {
                                    mListView.smoothScrollToPosition(i,i);
                                    return;
                                }
                            }
                        }
                    }, 100);

                }
            });
            setDBDataToList(results);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() != 0) {
                showSearchList(s.toString().trim());
            } else {
                clearSearchListAndHide();
            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.loadCity();
        }
    };

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0)
                mSwipeRefreshLayout.setEnabled(true);
            else
                mSwipeRefreshLayout.setEnabled(false);
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(LoginSelectCityActivity.this, LoginSelectSchoolActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(LoginSelectSchoolActivity.EXTRA_CITY_CODE, ((LoginSelectCityAdapter.CityExt) view.getTag()).cityCode);
            i.putExtras(bundle);
            startActivity(i);
        }
    };

    private void setDBDataToList(RealmResults<City> cities) {
        for (City city : cities) {
            LoginSelectCityAdapter.CityExt ext = new LoginSelectCityAdapter.CityExt();
            ext.cityCode = ((CityRealmProxy) city).realmGet$code();
            ext.cityName = ((CityRealmProxy) city).realmGet$name();
            ext.category_value = ((CityRealmProxy) city).realmGet$quanpin().substring(0, 1).toUpperCase();
            ext.category = ext.category_value;
            mAdapter.add(ext);
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.graySearchBg)
    public void onBtnGraySearchBgClick() {

    }

    @Override
    public void setCityData(final RealmList<City> cityData) {
        mAdapter.clear();
        for (int i = 0; i < cityData.size(); i++) {
            LoginSelectCityAdapter.CityExt ext = new LoginSelectCityAdapter.CityExt();
            ext.cityCode = cityData.get(i).code;
            ext.cityName = cityData.get(i).name;
            ext.category_value = cityData.get(i).quanpin.substring(0, 1).toUpperCase();
            ext.category = ext.category_value;
            mAdapter.add(ext);
        }
        mAdapter.notifyDataSetChanged();
        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(final String index) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < cityData.size(); i++) {
                            if (cityData.get(i).quanpin.substring(0, 1).toUpperCase().equals(index)) {
                                mListView.smoothScrollToPosition(i,i);
                                return;
                            }
                        }
                    }
                }, 100);

            }
        });
        saveCityDataInDB(cityData);
    }

    private void saveCityDataInDB(final RealmList<City> cityData) {
        /**
         * 存入customer
         */
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(cityData);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.d("MSGGender", "存储customer数据成功!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("MSGGender", "存储customer数据失败!");
            }
        });
    }

    @OnClick(R.id.grayBg)
    public void onBtnGreyBgClick() {

    }

    @OnClick(R.id.search_cancel)
    public void onBtnSearchCancel() {
        hideSearchAnim();
    }

    @OnClick(R.id.back_icon)
    public void onBtnBack() {
        showBackAlertAnim();
    }

    @OnClick(R.id.search_icon)
    public void onBtnSearch() {
        showSearchAnim();
    }

    @OnClick(R.id.click_errorbtn)
    public void onBtnBackAlertClickError() {
        hideBackAlertAnim();
    }

    @OnClick(R.id.search_clear)
    public void onBtnSearchClear() {
        mSearchEdit.setText("");
        clearSearchListAndHide();
    }

    @OnClick(R.id.exit_view)
    public void onBtnExit() {
        if (getIntent().getExtras() != null && !Util.isEmpty(getIntent().getExtras().getString(EXTRA_FROM_LOGIN))) {
            startActivity(new Intent(this, LoginInputMobilActivityNew.class));
        }
        finish();
    }

    private void showSearchList(String key) {
        searchListAdapter = new LoginSearchCityListAdapter(this, key);
        mSearchList.setAdapter(searchListAdapter);
        RealmResults<City> searchResults = realm.where(City.class).contains("quanpin", key).or().contains("name", key).findAll();
        mSearchList.setItemsCanFocus(true);
        for (City city : searchResults) {
            City c = new City();
            c.name = ((CityRealmProxy) city).realmGet$name();
            c.quanpin = ((CityRealmProxy) city).realmGet$quanpin();
            c.code = ((CityRealmProxy) city).realmGet$code();
            searchListAdapter.add(c);
        }

        searchListAdapter.notifyDataSetChanged();

        mSearchList.setVisibility(View.VISIBLE);
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(LoginSelectCityActivity.this, LoginSelectSchoolActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(LoginSelectSchoolActivity.EXTRA_CITY_CODE, ((City) view.getTag()).code);
                i.putExtras(bundle);

                clearSearchListAndHide();
                hideSearchAnim();
                mSearchEdit.setText("");
                startActivity(i);
            }
        });
    }

    private void clearSearchListAndHide() {
        if (searchListAdapter == null) {
            searchListAdapter = new LoginSearchCityListAdapter(this, "");
        }
        //mSearchGrayBg.setVisibility(View.GONE);
        searchListAdapter.clear();
        searchListAdapter.notifyDataSetChanged();
        mSearchList.setVisibility(View.GONE);
    }

    private void showBackAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 150;//470 430 455 450
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", 0,
                halfScreen + 70, halfScreen + 30, halfScreen + 55, halfScreen + 50);
        animator2.setDuration(700);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);
    }

    private void hideBackAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 150;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", halfScreen + 50, 0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);
    }

    private void showSearchAnim() {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mActionBarView, "translationY", 0, -(int) metrics.density * 100);
        animator2.setDuration(600);
        animator2.start();
        mSearchEdit.setFocusable(true);
        mSearchEdit.setFocusableInTouchMode(true);
        mSearchEdit.requestFocus();
        mSearchEdit.requestFocusFromTouch();
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");//Context.INPUT_METHOD_SERVICE
        imm.showSoftInput(mSearchEdit, InputMethodManager.SHOW_FORCED);
        mSearchGrayBg.setVisibility(View.VISIBLE);
    }

    private void hideSearchAnim() {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mActionBarView, "translationY", -(int) metrics.density * 100, 0);
        animator2.setDuration(600);
        animator2.start();
        mSearchEdit.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");//Context.INPUT_METHOD_SERVICE
        imm.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
        mSearchGrayBg.setVisibility(View.GONE);
        mSearchList.setVisibility(View.GONE);
        //mSearchEdit.setText("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showBackAlertAnim();
        }
        return false;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public void setPresenter(ILoginSelectCityContract.ISelectCityPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loadStart() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void loadError(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
