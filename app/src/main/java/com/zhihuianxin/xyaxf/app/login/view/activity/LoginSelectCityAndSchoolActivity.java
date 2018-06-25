package com.zhihuianxin.xyaxf.app.login.view.activity;

/**
 * Created by Vincent on 2016/10/17.
 */

public class LoginSelectCityAndSchoolActivity {//extends BaseActivity implements LoginSelectCityActivity.ISelectSchool{
//    private DisplayMetrics metrics;
//
//    @InjectView(R.id.search_list)
//    ListView mSearchList;
//    @InjectView(R.id.graySearchBg)
//    View mSearchGrayBg;
//    @InjectView(R.id.search_edit)
//    EditText mSearchEdit;
//    @InjectView(R.id.bar)
//    View mActionBarView;
//    @InjectView(R.id.grayBg)
//    View mGrayBg;
//    @InjectView(R.id.backAnimView)
//    View mBackAlertView;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ButterKnife.inject(this);
//        metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//        LoginSelectCityActivity f = LoginSelectCityActivity.getNewInstance();
//        f.setISelectCity(this);
//        new LoginSelectCityPresenter(this,f);
//        addFragment(f);
//
//        mSearchEdit.addTextChangedListener(textWatcher);
//    }
//
//    @OnClick(R.id.grayBg)
//    public void onBtnGreyBgClick(){
//
//    }
//    @OnClick(R.id.search_cancel)
//    public void onBtnSearchCancel(){
//        hideSearchAnim();
//    }
//
//    @OnClick(R.id.back_icon)
//    public void onBtnBack(){
//        showBackAlertAnim();
//    }
//
//    @OnClick(R.id.search_icon)
//    public void onBtnSearch(){
//        showSearchAnim();
//    }
//
//    @OnClick(R.id.click_errorbtn)
//    public void onBtnBackAlertClickError(){
//        hideBackAlertAnim();
//    }
//
//    @OnClick(R.id.search_clear)
//    public void onBtnSearchClear(){
//        mSearchList.setVisibility(View.GONE);
//        mSearchEdit.setText("");
//    }
//
//    @OnClick(R.id.exit)
//    public void onBtnExit(){
//        finish();
//    }
//
//    TextWatcher textWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {}
//        @Override
//        public void afterTextChanged(Editable s) {
//            if(s.length() != 0){
//                showSearchList();
//            }
//        }
//    };
//
//    private void showSearchList(){
//        mSearchList.setItemsCanFocus(true);
//        LoginSearchListAdapter searchListAdapter = new LoginSearchListAdapter(this);
//        searchListAdapter.add("123");
//        searchListAdapter.add("456");
//        mSearchList.setAdapter(searchListAdapter);
//        mSearchList.setVisibility(View.VISIBLE);
//        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(LoginSelectCityAndSchoolActivity.this,"12333",Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void showBackAlertAnim(){
//        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", 0, (int)metrics.density*450);
//        animator2.setDuration(600);
//        animator2.start();
//        mGrayBg.setVisibility(View.VISIBLE);
//    }
//
//    private void hideBackAlertAnim(){
//        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY",(int)metrics.density*450,0);
//        animator2.setDuration(600);
//        animator2.start();
//        mGrayBg.setVisibility(View.GONE);
//    }
//
//    private void showSearchAnim(){
//        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mActionBarView, "translationY",0,-(int)metrics.density*48);
//        animator2.setDuration(600);
//        animator2.start();
//        mSearchEdit.setFocusable(true);
//        mSearchEdit.setFocusableInTouchMode(true);
//        mSearchEdit.requestFocus();
//        mSearchEdit.requestFocusFromTouch();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(mSearchEdit,InputMethodManager.SHOW_FORCED);
//        mSearchGrayBg.setVisibility(View.VISIBLE);
//    }
//
////    @OnClick(R.id.graySearchBg)
////    public void onViewBg(){
////        Toast.makeText(this,"fff",Toast.LENGTH_LONG).show();
////    }
//
//    private void hideSearchAnim(){
//        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mActionBarView, "translationY",-(int)metrics.density*48,0);
//        animator2.setDuration(600);
//        animator2.start();
//        mSearchEdit.setFocusable(true);
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
//        mSearchGrayBg.setVisibility(View.GONE);
//        mSearchList.setVisibility(View.GONE);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            showBackAlertAnim();
//        }
//        return false;
//    }
//
//    @Override
//    public void changeToSelectSchool(LoginSelectCityAdapter.CityExt cityExt) {
//        LoginSelectSchoolFragment f = new LoginSelectSchoolFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(LoginSelectSchoolFragment.EXTRA_CITY_CODE,cityExt.cityCode);
//        f.setArguments(bundle);
//        new LoginSelectSchoolPresenter(this,f, SchedulerProvider.getInstance());
//        addFragment(f);
//    }
//
//    @Override
//    protected int getContentViewId() {
//        return R.layout.login_select_all_activity;
//    }
//
//    @Override
//    protected int getFragmentContentId() {
//        return R.id.contentView;
//    }

}
