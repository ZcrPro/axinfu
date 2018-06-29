package com.zhihuianxin.xyaxf.app.pay.guiyang;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.LoanService;
import modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ProcessingActivity;
import com.zhihuianxin.xyaxf.app.payment.EnumFields;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.DataYearDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/11/1.
 * 预授信信息补全界面
 */

public class PreApprovalActivity extends BaseRealmActionBarActivity {
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.ed_ruxue_year)
    TextView edRuxueYear;
    @InjectView(R.id.ed_peiyang_cengci)
    Spinner edPeiyangCengci;
    @InjectView(R.id.ed_peiyang_cengci2)
    TextView edPeiyangCengci2;
    @InjectView(R.id.ed_tuixue_year)
    TextView edTuixueYear;

    private Calendar showDate;

    @InjectView(R.id.tv_student_no)
    TextView tvStudentNo;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.ed_idcard)
    EditText edIdcard;
    @InjectView(R.id.cengciicon)
    ImageView cengciImg;
    @InjectView(R.id.yearicon)
    ImageView yearIcon;
    @InjectView(R.id.rxyearicon)
    ImageView rxImag;

    private PreApprovalInfo preApprovalInfo;

    private String Sp_education_level; //选择的培养层次

    public class PreApprovalInfo {
        public String student_no;                // 学生学号
        public String major;                    // 专业
        public String enrollment_year;            // 入学年份
        public String graduation_year;            // 毕业年份
        public String education_level;        // 培养层次

        @Override
        public String toString() {
            return "PreApprovalInfo{" +
                    "student_no='" + student_no + '\'' +
                    ", major='" + major + '\'' +
                    ", enrollment_year='" + enrollment_year + '\'' +
                    ", graduation_year='" + graduation_year + '\'' +
                    ", education_level='" + education_level + '\'' +
                    '}';
        }
    }

    public List<EnumFields> spiner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getEnumData();
        showDate = Calendar.getInstance();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断条件
                if (TextUtils.isEmpty(edName.getText().toString())) {
                    Toast.makeText(PreApprovalActivity.this, "请输入学号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edIdcard.getText().toString())) {
                    Toast.makeText(PreApprovalActivity.this, "请输入专业", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edRuxueYear.getText().toString())||edRuxueYear.getText().toString().equals("请选择入学年份")) {
                    Toast.makeText(PreApprovalActivity.this, "请选择入学年份", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edPeiyangCengci2.getText().toString()) || edPeiyangCengci2.getText().toString().equals("请选择培养层次")) {
                    Toast.makeText(PreApprovalActivity.this, "请选择培养层次", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edTuixueYear.getText().toString()) || edTuixueYear.getText().toString().equals("请选择毕业年份")) {
                    Toast.makeText(PreApprovalActivity.this, "请选择毕业年份", Toast.LENGTH_SHORT).show();
                } else {
                    if (Sp_education_level != null) {
                        preApprovalInfo = new PreApprovalInfo();
                        preApprovalInfo.student_no = edName.getText().toString().trim();
                        preApprovalInfo.major = edIdcard.getText().toString().trim();
                        preApprovalInfo.enrollment_year = edRuxueYear.getText().toString().trim();
                        preApprovalInfo.graduation_year = edTuixueYear.getText().toString().trim();
                        preApprovalInfo.education_level = Sp_education_level;
                        commitInfo(preApprovalInfo);
                    } else {
                        Toast.makeText(PreApprovalActivity.this, "培养层次获取失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        edRuxueYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DataYearDialog(PreApprovalActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        showDate.set(Calendar.YEAR, year);
                        edRuxueYear.setText(year+"");
                        rxImag.setBackgroundResource(R.drawable.icon_down);
                    }
                },showDate.get(Calendar.YEAR), showDate.get(Calendar.MONTH), showDate.get(Calendar.DATE));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                rxImag.setBackgroundResource(R.drawable.icon_up);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        rxImag.setBackgroundResource(R.drawable.icon_down);
                    }
                });
            }
        });

        edTuixueYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DataYearDialog(PreApprovalActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        showDate.set(Calendar.YEAR, year);
                        edTuixueYear.setText(year+"");

                        yearIcon.setBackgroundResource(R.drawable.icon_down);
                    }
                },showDate.get(Calendar.YEAR), showDate.get(Calendar.MONTH), showDate.get(Calendar.DATE));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                yearIcon.setBackgroundResource(R.drawable.icon_up);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        yearIcon.setBackgroundResource(R.drawable.icon_down);
                    }
                });

            }
        });


        edPeiyangCengci2.setText("请选择培养层次");
        edTuixueYear.setText("请选择毕业年份");
        edRuxueYear.setText("请选择入学年份");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.approval_activity;
    }

    private void commitInfo(PreApprovalInfo pre_approval_info) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pre_approval_info", pre_approval_info);
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.pre_approval_register(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final YushouResponse yushouResponse = new Gson().fromJson(o.toString(), YushouResponse.class);
                        if (yushouResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            PreApprovalActivity.this.startActivity(new Intent(PreApprovalActivity.this, ProcessingActivity.class));
                            finish();
                        }
                    }
                });
    }

    boolean isSpinnerFirst = true ;
    private void getEnumData() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<String>();
        list.add("educationLevel");
        map.put("key_types", list);
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_enum_data(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final MyResponse myResponse = new Gson().fromJson(o.toString(), MyResponse.class);
                        if (myResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            spiner = new ArrayList<>();
                            for (int i = 0; i < myResponse.enum_data.educationLevel.size(); i++) {
                                EnumFields enumFields = new EnumFields();
                                enumFields.name = myResponse.enum_data.educationLevel.get(i).name;
                                enumFields.value = myResponse.enum_data.educationLevel.get(i).value;
                                enumFields.remark = myResponse.enum_data.educationLevel.get(i).remark;
                                spiner.add(enumFields);
                                final MajorAdapter majorAdapter = new MajorAdapter(PreApprovalActivity.this, spiner);
                                edPeiyangCengci.setAdapter(majorAdapter);
                                edPeiyangCengci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int pos, long id) {
                                        if (!isSpinnerFirst) {
                                            edPeiyangCengci2.setText(((EnumFields) majorAdapter.getItem(pos)).name);
                                        }
                                        isSpinnerFirst = false ;
                                        Sp_education_level = ((EnumFields) majorAdapter.getItem(pos)).value;
                                        preApprovalInfo = new PreApprovalInfo();
                                        preApprovalInfo.education_level = ((EnumFields) majorAdapter.getItem(pos)).value;
                                        preApprovalInfo.student_no = edName.getText().toString().trim();
                                        preApprovalInfo.major = edIdcard.getText().toString().trim();
                                        preApprovalInfo.enrollment_year = edRuxueYear.getText().toString().trim();
                                        preApprovalInfo.graduation_year = edTuixueYear.getText().toString().trim();

                                        cengciImg.setBackgroundResource(R.drawable.icon_down);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Another interface callback
                                        cengciImg.setBackgroundResource(R.drawable.icon_down);
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public static class MyResponse extends RealmObject {
        public BaseResponse resp;
        public educationLevel enum_data;
    }

    public static class YushouResponse extends RealmObject {
        public BaseResponse resp;
    }

    public class educationLevel {
        List<EnumFields> educationLevel;
    }

}
