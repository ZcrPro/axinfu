package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.secure_code.VerifyFor;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionClearPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionClearPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/10.
 * 忘记支付密码，输入短信验证码
 */

public class UnionForgetPayPwdCodeActivity extends BaseRealmActionBarActivity implements IunionClearPwdContract.IunionClearPwd {
    public static final String EXTRA_SHOWIMG = "";

    @InjectView(R.id.editText)
    TextView mMobileEdit;
    @InjectView(R.id.getver)
    Button mVerText;
    @InjectView(R.id.edit_pwd)
    EditText verEdit;
    @InjectView(R.id.edit_id_card)
    EditText editIdCard;
    @InjectView(R.id.next)
    Button next;

    private int mCountDownDuration = 60 * 1000;
    private VerController verController;
    private IunionClearPwdContract.IunionClearPwdPresenter presenter;
    private static final HashMap<String, Long> sStartTicks = new HashMap<String, Long>();

    @Override
    protected int getContentViewId() {
        return R.layout.union_forgetpaypwd_code_activity;
    }

    TextWatcher watcherVer = new TextWatcher() {
        private String lastInput;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.equals(lastInput)) {
                return;
            }

            if (text.length() == 4) {
                @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager) getSystemService("input_method");//Context.INPUT_METHOD_SERVICE
                imm.hideSoftInputFromWindow(verEdit.getWindowToken(), 0);
                verEdit.clearFocus();
            }

            lastInput = text;
        }
    };

    private class VerController {
        private String mNormalText;
        private String mCountDownTag = getClass().getName();

        public VerController() {
            mCountDownTag = getClass().getName();
            mNormalText = mVerText.getText().toString();
        }

        void verStart() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mVerText.setBackground(getResources().getDrawable(R.drawable.axf_common_blue_btn));
            }
            mVerText.setTextColor(getResources().getColor(R.color.white));
            setState(false);
        }

        void reset() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mVerText.setBackground(getResources().getDrawable(R.drawable.axf_check_blue_btn));
            }
            setState(true);
            mVerText.setText(mNormalText);
            mVerText.setTextColor(getResources().getColor(R.color.white));
            sStartTicks.remove(mCountDownTag);
        }

        private void setState(boolean enabled) {
            mVerText.setEnabled(enabled);
        }

        private String getCountDownTag() {
            return mCountDownTag;
        }
    }

    private class Task extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            Long startMilli = sStartTicks.get(verController.getCountDownTag());
            if (startMilli == null || System.currentTimeMillis() - startMilli > mCountDownDuration) {
                startMilli = System.currentTimeMillis();
                sStartTicks.put(verController.getCountDownTag(), startMilli);
            }

            while (true) {
                long currentMilli = System.currentTimeMillis();

                long duration = currentMilli - startMilli;
                long left = mCountDownDuration - duration;
                if (left <= 0) {
                    publishProgress(0l);

                    break;
                }
                publishProgress(left);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            verController.verStart();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            verController.reset();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

            if (values.length > 0 && mVerText != null) {
                long left = (Long) values[0];
                mVerText.setText(Math.round((float) left / 1000f) + "s后重新获取");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initView();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }
        });

//        if (mVerText.isEnabled()){
//            if (Util.isEmpty(App.mAxLoginSp.getUserMobil())) {
//                Toast.makeText(this, "请输入手机号!", Toast.LENGTH_LONG).show();
//                return;
//            }
//            presenter.getVerCode(VerifyFor.ClearPayPassword.name(), App.mAxLoginSp.getUserMobil());
//            Task task = new Task();
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
    }

    private void initView() {
        new UnionClearPayPwdPresenter(this, this);
        verController = new VerController();
        mMobileEdit.setText("手机号：" + App.mAxLoginSp.getUserMobil().substring(0, 3) + "****" + App.mAxLoginSp.getUserMobil().substring(7));
        verEdit.addTextChangedListener(watcherVer);
    }

    @OnClick(R.id.getver)
    public void onBtnGetVer() {
        if (Util.isEmpty(App.mAxLoginSp.getUserMobil())) {
            Toast.makeText(this, "请输入手机号!", Toast.LENGTH_LONG).show();
            return;
        }
        presenter.getVerCode(VerifyFor.ClearPayPassword.name(), App.mAxLoginSp.getUserMobil());
        Task task = new Task();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.next)
    public void onBtnGetNewPwd() {
        String val = editIdCard.getText().toString().trim();
        String verCode = verEdit.getText().toString().trim();
        if (Util.isEmpty(verCode) || verCode.length() != 4) {
            Toast.makeText(UnionForgetPayPwdCodeActivity.this, "请输入正确格式的短信验证码", Toast.LENGTH_LONG).show();
            return;
        }

        if (val.length() != 18) {
            Toast.makeText(this, "请输入正确的身份证号", Toast.LENGTH_LONG).show();
        } else {
            verRealName("", val);
        }

    }

    @Override
    public void getVerCodeResult(String code) {
        Toast.makeText(this, "验证码已发送", Toast.LENGTH_LONG).show();
        if (BuildConfig.AnXinDEBUG) {
            Toast.makeText(this, "测试环境：" + code, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setPayPwdResult() {

    }

    @Override
    public void slearPayPwdResult() {
        startActivity(new Intent(this, UnionSetPayPwdActivity.class));
        finish();
    }

    @Override
    public void setPresenter(IunionClearPwdContract.IunionClearPwdPresenter presenter) {
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

    public void verRealName(String name, String id_card_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id_card_no", id_card_no);
        CustomerService cus = ApiFactory.getFactory().create(CustomerService.class);
        cus.verityPwd(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        VerPwdResponse response = new Gson().fromJson(o.toString(), VerPwdResponse.class);
                        if (response.is_match) {
                            String mobile = App.mAxLoginSp.getUserMobil();
                            String verCode = verEdit.getText().toString().trim();
                            Pattern p = Pattern.compile(BuildConfig.AnXinDEBUG ? Util.REGEX_MOBILE_DEBUG : Util.REGEX_MOBILE);
                            if (!p.matcher(mobile).matches()) {
                                Toast.makeText(UnionForgetPayPwdCodeActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (Util.isEmpty(verCode) || verCode.length() != 4) {
                                Toast.makeText(UnionForgetPayPwdCodeActivity.this, "请输入正确格式的短信验证码", Toast.LENGTH_LONG).show();
                                return;
                            }
                            presenter.slearPayPwd(verCode);
                        } else {
                            Toast.makeText(UnionForgetPayPwdCodeActivity.this, "身份证号匹配失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private class VerPwdResponse {
        public BaseResponse resp;
        public boolean is_match;
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(verEdit, 0);
    }
}
