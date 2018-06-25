package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.customer.Customer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.view.fragment.LoginSelectCityActivity;
import com.zhihuianxin.xyaxf.app.ocp.MeOcpActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CustomerBaseInfoRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.RealmResults;
import io.realm.SchoolRealmProxy;

/**
 * Created by Vincent on 2016/10/18.
 */

public class MeStuMsgActivity extends BaseRealmActionBarActivity {
    @InjectView(R.id.school)
    TextView mSchooltext;
    @InjectView(R.id.stu_name)
    TextView mStuNameText;
    @InjectView(R.id.avatarId)
    ImageView mAvatarImg;
    @InjectView(R.id.nameRightId)
    ImageView mNameModifyImg;
    @InjectView(R.id.ecard_next)
    ImageView mEcardFeeNextImg;
    @InjectView(R.id.fee_next)
    ImageView mFeeNextImg;
    @InjectView(R.id.me_stu_ecard)
    View mEcardView;
    @InjectView(R.id.me_stu_fee)
    View mFeeView;
    @InjectView(R.id.avatar_next)
    ImageView modifySchoolNextImg;
    @InjectView(R.id.avatar_view)
    RelativeLayout avatarView;
    @InjectView(R.id.me_stu_name)
    RelativeLayout meStuName;
    @InjectView(R.id.textView)
    TextView textView;
    @InjectView(R.id.bank_card_next)
    ImageView bankCardNext;
    @InjectView(R.id.me_stu_bank_card)
    RelativeLayout meStuBankCard;
    @InjectView(R.id.me_stu_ocp)
    RelativeLayout meStuOcp;

    private boolean isNameCanModify = false;
    private boolean isEcardCanModify = false;
    private boolean isFeeCanModify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initStatus();
        initSchoolModify();
    }

    private void setQiniuPicUrlToUI() {

        final String url = ((SchoolRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).
                equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$school()).realmGet$logo();
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(url, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(url, view, loadedImage);
                mAvatarImg.setImageBitmap(loadedImage);
            }
        });
    }

    private void initView() {
        RealmResults<Customer> realmResults = realm.
                where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (realmResults.size() <= 0) {
            return;
        }

        if ((((CustomerRealmProxy) realmResults.get(0))).realmGet$school() != null) {
            String schoolName = ((SchoolRealmProxy) (((CustomerRealmProxy)
                    realmResults.get(0))).realmGet$school()).realmGet$name();
            mSchooltext.setText(Util.isEmpty(schoolName) ? "" : schoolName);
        }

        String stuName = ((CustomerBaseInfoRealmProxy) (((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$name();
        if (!Util.isEmpty(stuName)) {
            String xx = "";
            for (int i = 0; i < stuName.length() - 1; i++) {
                xx += "*";
            }
            mStuNameText.setText(xx + stuName.substring(stuName.length() - 1));
        }

        setQiniuPicUrlToUI();


        if (TextUtils.isEmpty(((FeeAccountRealmProxy) ((CustomerRealmProxy) realmResults.get(0)).realmGet$fee_account()).realmGet$student_no())) {
            meStuBankCard.setVisibility(View.GONE);
        } else {
            if (((FeeAccountRealmProxy) ((CustomerRealmProxy) realmResults.get(0)).realmGet$fee_account()).realmGet$collect_bankcard() == true) {
                meStuBankCard.setVisibility(View.VISIBLE);
            } else {
                meStuBankCard.setVisibility(View.GONE);
            }
        }

        meStuBankCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入银行卡管理界面
                startActivity(new Intent(MeStuMsgActivity.this, MeBankCardActivity.class));
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_stu_record_activity;
    }

    private void initStatus() {

        if (null != App.ocpAccount && App.ocpAccount.account.status.equals("OK")) {
            meStuOcp.setVisibility(View.VISIBLE);
            meStuOcp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (App.ocpAccount == null) {
                        Toast.makeText(MeStuMsgActivity.this, "一码通账户信息加载失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(new Intent(MeStuMsgActivity.this, MeOcpActivity.class));
                }
            });
        }

        String ecardStatue = ((ECardAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$ecard_account()).realmGet$status();
        String feeStatue = ((FeeAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account()).realmGet$status();
        String card_id = ((FeeAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account()).realmGet$id_card_no();
        if (ecardStatue.equals("OK")) {
            mEcardView.setVisibility(View.VISIBLE);
            isEcardCanModify = true;// 绑定了可以改
        } else {
            mEcardView.setVisibility(View.GONE);
            isEcardCanModify = false;
        }

        if (feeStatue.equals("OK")) {//|| !Util.isEmpty(card_id)
            mFeeView.setVisibility(View.VISIBLE);
            isFeeCanModify = true;// 绑定了可以改 或者server保存了身份证号也可以改
        } else {
            mFeeView.setVisibility(View.GONE);
            isFeeCanModify = false;
        }

        if (feeStatue.equals("OK") || ecardStatue.equals("OK")) {
            mNameModifyImg.setVisibility(View.INVISIBLE);
            isNameCanModify = false;// 绑定了不可以改
        } else {
            isNameCanModify = true;
            mNameModifyImg.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.avatar_view)
    public void onBtnModifySchoolView() {
        if (isCanModifySchool) {
            startActivity(new Intent(getActivity(), LoginSelectCityActivity.class));
        }
    }

    private boolean isCanModifySchool = true;

    private void initSchoolModify() {
        boolean feeStaueIsNull = ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account() == null;
        boolean eCardStaueIsNull = ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$ecard_account() == null;
        boolean oCpStaue = App.ocpAccount == null;
        if (feeStaueIsNull || eCardStaueIsNull || oCpStaue) {
            isCanModifySchool = true;
            modifySchoolNextImg.setVisibility(View.VISIBLE);
        } else {
            String feeStatus = ((FeeAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account()).realmGet$status();
            String eCardStatus = ((ECardAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$ecard_account()).realmGet$status();
            String oCpStatus = App.ocpAccount.account.status;
            if (((!Util.isEmpty(feeStatus) && feeStatus.equals("OK")) || !Util.isEmpty(eCardStatus) && eCardStatus.equals("OK") || !Util.isEmpty(oCpStatus) && oCpStatus.equals("OK") )) {
                isCanModifySchool = false;
                modifySchoolNextImg.setVisibility(View.INVISIBLE);
            } else {
                isCanModifySchool = true;
                modifySchoolNextImg.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.me_stu_name)
    public void onBtnStuName() {
        if (isNameCanModify) {
            startActivity(new Intent(this, MeStuNameModifyActivity.class));
        }
    }

    @OnClick(R.id.me_stu_ecard)
    public void onBtnStuECard() {
        if (isEcardCanModify) {
            startActivity(new Intent(this, MeStuEcardMsgActivity.class));
        } else {
            Toast.makeText(this, "未绑定一卡通！", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.me_stu_fee)
    public void onBtnStuFee() {
        if (isFeeCanModify) {
            startActivity(new Intent(this, MeStuFeeMsgActivity.class));
        } else {
            Toast.makeText(this, "缴费信息暂未验证！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }

}
