package com.zhihuianxin.xyaxf.app.login.view.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.axinfu.modellib.thrift.business.Business;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.axinfu.modellib.thrift.resource.School;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.home.HomeFragment;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginSelectSchoolContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginSelectSchoolPresenter;
import com.zhihuianxin.xyaxf.app.login.view.adapter.LoginSearchSchoolListAdapter;
import com.zhihuianxin.xyaxf.app.login.view.adapter.LoginSelectSchoolAdapter;
import com.zhihuianxin.xyaxf.app.view.GifView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.SchoolRealmProxy;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Vincent on 2016/10/20.
 */

public class LoginSelectSchoolActivity extends BaseRealmActionBarActivity implements ILoginSelectSchoolContract.ISelectSchoolView {
    public static final String EXTRA_CITY_CODE = "city_code";

    @InjectView(R.id.search_list)
    ListView mSearchList;
    @InjectView(R.id.graySearchBg)
    View mSearchGrayBg;
    @InjectView(R.id.search_edit)
    EditText mSearchEdit;
    @InjectView(R.id.bar)
    View mActionBarView;
    @InjectView(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.stickListView)
    StickyListHeadersListView mListView;
    @InjectView(R.id.side_bar)
    WaveSideBar sideBar;
    private LoginSelectSchoolAdapter mAdapter;
    @InjectView(R.id.bottomView)
    View mSelectView;
    @InjectView(R.id.school_logo)
    ImageView mSchoolLogo;
    @InjectView(R.id.gif_bg_view)
    View mGitbg;
    @InjectView(R.id.gif_view)
    GifView mGitView;

    private DisplayMetrics metrics;
    private String mCityCode;
    private School mSelectSchool;
    private ILoginSelectSchoolContract.ISelectSchoolPresenter mPresenter;
    private LoginSearchSchoolListAdapter searchListAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.login_select_school_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metrics = new DisplayMetrics();

        findViewById(R.id.action_bar).setVisibility(View.GONE);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        new LoginSelectSchoolPresenter(this, this);
        ButterKnife.inject(this);
        initView();
    }

    protected void initView() {
        App.mAxLoginSp.setSelectSchoolCode("");
        mCityCode = getIntent().getExtras().getString(EXTRA_CITY_CODE);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mAdapter = new LoginSelectSchoolAdapter(this);
        mListView.setAdapter(mAdapter);

        mSearchEdit.addTextChangedListener(textWatcher);
        mSelectView.setOnClickListener(onClickListener);
        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        mListView.setOnScrollListener(scrollListener);
        mListView.setOnItemClickListener(itemClickListener);

        RealmResults<School> results = realm.where(School.class).equalTo("city_code", mCityCode).findAll();
//        if(results.size() == 0)
//            mPresenter.loadSchool(mCityCode);
//        else
//            setDBDataToList(results);
        final RealmResults<School> schools = realm.where(School.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                schools.deleteAllFromRealm();
            }
        });
        mPresenter.loadSchool(mCityCode);
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
                mSearchList.setVisibility(View.GONE);
                if (searchListAdapter!=null){
                    searchListAdapter.clear();
                    searchListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submitSchool();
        }
    };

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.loadSchool(mCityCode);
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
            App.mAxLoginSp.setSelectSchoolCode(((LoginSelectSchoolAdapter.SchoolExt) view.getTag()).school.code);
            mAdapter.notifyDataSetChanged();

            selectSchool(((LoginSelectSchoolAdapter.SchoolExt) view.getTag()).school);
        }
    };

    private void selectSchool(final School school) {
        mSelectSchool = school;

        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(school.logo, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                mSchoolLogo.setImageBitmap(loadedImage);
            }
        });

        mSelectView.setBackgroundResource(R.drawable.btn_axf_blue);
    }

    private void submitSchool() {
        if (mSelectSchool == null) {
            return;
        }
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mGitbg, "translationY", 0, -(int) metrics.density * 200);
        animator2.setDuration(300);
        animator2.start();
        mGitView.setMovieResource(R.raw.gif_loading);

        mPresenter.updateSchool(mSelectSchool.code);
    }

    private void hideGifView() {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mGitbg, "translationY", (int) metrics.density * 200, 0);
        animator2.setDuration(300).start();
    }

    private void resetAnima() {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mGitbg, "translationY", (int) metrics.density * 200, 0);
        animator2.setDuration(300);
        animator2.start();
        mGitView.setPaused(true);
    }

    private void setDBDataToList(RealmResults<School> schools) {// 每次都刷新所以不用了
        for (School school : schools) {
            LoginSelectSchoolAdapter.SchoolExt ext = new LoginSelectSchoolAdapter.SchoolExt();
            School s = new School();
            s.city_code = ((SchoolRealmProxy) school).realmGet$city_code();
            s.code = ((SchoolRealmProxy) school).realmGet$code();
            s.logo = ((SchoolRealmProxy) school).realmGet$logo();
            s.name = ((SchoolRealmProxy) school).realmGet$name();
            s.quanpin = ((SchoolRealmProxy) school).realmGet$quanpin();

            ext.school = s;
            ext.category_value = ((SchoolRealmProxy) school).realmGet$quanpin().substring(0, 1).toUpperCase();
            ext.category = ext.category_value;
            mAdapter.add(ext);
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.graySearchBg)
    public void onBtnGraySearchBgClick() {

    }

    @Override
    public void setSchoolData(RealmList<School> schoolData) {
        mAdapter.clear();
        for (int i = 0; i < schoolData.size(); i++) {
            LoginSelectSchoolAdapter.SchoolExt ext = new LoginSelectSchoolAdapter.SchoolExt();
            ext.school = schoolData.get(i);

            ext.category_value = schoolData.get(i).quanpin.substring(0, 1).toUpperCase();
            ext.category = ext.category_value;
            mAdapter.add(ext);
        }
        saveCityDataInDB(schoolData);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSchoolSuccess(Customer customer) {
        successLogin(customer);
    }

    private void successLogin(final Customer customer) {
        App.mAxLoginSp.setLoginSign(true);
        App.mAxLoginSp.setUserMobil(customer.base_info.mobile);
        App.mAxLoginSp.setRegistSerial(customer.base_info.regist_serial);

        final RealmResults<Customer> oldCustomers = realm.where(Customer.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                oldCustomers.deleteAllFromRealm();
            }
        });
        final RealmResults<Business> oldBusiness = realm.where(Business.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                oldBusiness.deleteAllFromRealm();
            }
        });
        final RealmResults<ECardAccount> oldECardAccount = realm.where(ECardAccount.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                oldECardAccount.deleteAllFromRealm();
            }
        });
        final RealmResults<FeeAccount> oldFeeAccount = realm.where(FeeAccount.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                oldFeeAccount.deleteAllFromRealm();
            }
        });

        updateDataAndGoMainView(customer);
    }

    private void updateDataAndGoMainView(final Customer customer) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                customer.mobile = customer.base_info.mobile;
                bgRealm.copyToRealmOrUpdate(customer);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Intent intentBroadCast = new Intent(HomeFragment.EXTRA_SCHOOL_DATA);
                sendBroadcast(intentBroadCast);
                Log.d("selectcity", "存储customer数据成功!");
                // 跳转到主页
                resetAnima();

                Intent intent = new Intent(HomeFragment.EXTRA_SCHOOL_DATA);
                sendBroadcast(intent);

                startActivity(new Intent(LoginSelectSchoolActivity.this, MainActivity.class));
                ActivityCollector.removeAllActivities();
                finish();
            }
        });
    }

    private void saveCityDataInDB(final RealmList<School> schools) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(schools);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.d("selectcity", "存储customer数据成功!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("selectcity", "存储customer数据失败!");
            }
        });
    }

    @OnClick(R.id.search_cancel)
    public void onBtnSearchCancel() {
        hideSearchAnim();
    }

    @OnClick(R.id.back_icon)
    public void onBtnBack() {
        finish();
    }

    @OnClick(R.id.search_icon)
    public void onBtnSearch() {
        showSearchAnim();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
    }

    @OnClick(R.id.search_clear)
    public void onBtnSearchClear() {
        mSearchList.setVisibility(View.GONE);
        mSearchEdit.setText("");
        searchListAdapter.clear();
        searchListAdapter.notifyDataSetChanged();
    }

    private void showSearchList(String key) {//.equalTo("city_code",mCityCode)
        RealmResults<School> searchResults = realm.where(School.class).contains("quanpin", key).or().contains("name", key).findAll();
        mSearchList.setItemsCanFocus(true);
        searchListAdapter = new LoginSearchSchoolListAdapter(this, key);
        mSearchList.setAdapter(searchListAdapter);

        for (School school : searchResults) {
            School s = new School();
            s.name = ((SchoolRealmProxy) school).realmGet$name();
            s.quanpin = ((SchoolRealmProxy) school).realmGet$quanpin();
            s.code = ((SchoolRealmProxy) school).realmGet$code();
            searchListAdapter.add(s);
        }
        searchListAdapter.notifyDataSetChanged();
        mSearchList.setVisibility(View.VISIBLE);
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectSchool = (School) view.getTag();
                submitSchool();
            }
        });
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
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(realm == null || realm.isClosed()){
//            realm = Realm.getDefaultInstance();
//            realm.beginTransaction();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//       closeRealm();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//    private void closeRealm(){
//        if(realm != null){
//            realm.close();
//        }
//    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public void setPresenter(ILoginSelectSchoolContract.ISelectSchoolPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loadStart() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void loadError(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        hideGifView();
    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
