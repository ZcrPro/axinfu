package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.CustomerBaseInfo;
import com.axinfu.modellib.thrift.resource.UploadFileAccess;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.RealNameAuthStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.api.QiNiuStorateAPI;
import com.zhihuianxin.xyaxf.app.me.contract.IMeMsgAvatarContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeMsgAvatarPresenter;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CustomerBaseInfoRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Vincent on 2016/10/18.
 */

public class MeMsgActivity extends BaseRealmActionBarActivity implements IMeMsgAvatarContract.IMeMsgAvatarView{
    public static final String TAG = "MeMsgActivity";
    public static final int REQUEST_CUSTOMER_INFO_TAKE_AVATAR = 5000;
    public static final int AVATAR_WIDTH = 120;
    public static final int AVATAR_HEIGHT = 120;

    @InjectView(R.id.avatar_id)
    ImageView mAvatarImg;
    @InjectView(R.id.rl_photo)
    RelativeLayout rlPhoto;
    @InjectView(R.id.nickname_value)
    TextView mNickName;
    @InjectView(R.id.genderText)
    TextView mGenderText;
    @InjectView(R.id.mobile_value)
    TextView mMobileText;
    @InjectView(R.id.name_value)
    TextView mRealNameValueTxt;
    @InjectView(R.id.idText)
    TextView mRealIdValueTxt;
    @InjectView(R.id.realnameview)
    View realNameView;

    private IMeMsgAvatarContract.IMeMsgAvatarPresenter mPresenter;
    private String mAvatarPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new MeMsgAvatarPresenter(this,this);
        rlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TakePictureActivity.class);
                intent.putExtra(TakePictureActivity.EXTRA_FROM,TakePictureActivity.EXTRA_FROM_AVATAR);
                //intent.putExtra(TakePictureActivity.EXTRA_REQUIRED_SIZE, new int[]{AVATAR_WIDTH, AVATAR_HEIGHT});
                startActivityForResult(intent, REQUEST_CUSTOMER_INFO_TAKE_AVATAR);
            }
        });
        setQiniuPicUrlToUI();

        mPresenter.getRealName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CUSTOMER_INFO_TAKE_AVATAR) {
            if (resultCode == Activity.RESULT_OK) {

                if(data.getExtras() != null && data.getExtras().getSerializable(TakePictureActivity.EXTRA_OUTPUT_PATH_MUTI) != null){
                    ArrayList<String> mutiPath = (ArrayList<String>) data.getExtras().getSerializable(TakePictureActivity.EXTRA_OUTPUT_PATH_MUTI);
                    for(String url : mutiPath){
                        mAvatarPath = url;
                    }
                } else{
                    mAvatarPath = data.getStringExtra(TakePictureActivity.EXTRA_OUTPUT_PATH);
                }
                mPresenter.getQiNiuAccess("Avatar","jpg",1);
            } else if (resultCode == CropImageActivity.RESULT_CAN_NOT_LOAD_IMAGE_URI || resultCode == CropImageActivity.RESULT_CAN_NOT_CREATE_OUTPUT_FILE) {
                Log.w(TAG, "can not load image: ", (Exception) data.getSerializableExtra(CropImageActivity.EXTRA_ERROR));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews(){
        RealmResults<Customer> realmResults =realm.
                where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if(realmResults.size() <= 0){
            return;
        }
        String nickName = ((CustomerBaseInfoRealmProxy)(((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$nickname();
        mNickName.setText(Util.isEmpty(nickName)?"":nickName);

        String gender = ((CustomerBaseInfoRealmProxy)(((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$gender();
        mGenderText.setText(Util.isEmpty(gender)?"男":(gender.equals("Male")?"男":"女"));

        mMobileText.setText(App.mAxLoginSp.getUserMobil().substring(0,3)+"****"+App.mAxLoginSp.getUserMobil().substring(7));
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_msg_activity;
    }

    @OnClick(R.id.nickname)
    public void onBtnNickName() {
        startActivity(new Intent(this, MeMsgNameModifyActivity.class));
    }

    @OnClick(R.id.gender)
    public void onBtnGender() {
        startActivity(new Intent(this, MeMsgGenderModifyActivity.class));
    }

    @OnClick(R.id.mobile)
    public void onBtnMobile() {
        startActivity(new Intent(this, MeMsgModifyMobileActivity.class));
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

    @Override
    public void modifyBaseInfoSuccess(final Customer customer) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                customer.mobile = App.mAxLoginSp.getUserMobil();
                bgRealm.copyToRealmOrUpdate(customer);
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
        Toast.makeText(this,"修改成功",Toast.LENGTH_LONG).show();
    }

    @Override
    public void getQiNiuAccessSuccess(RealmList<UploadFileAccess> accesses) {
        UploadToQiniuTask task = new UploadToQiniuTask(accesses, mAvatarPath,this);
        mUploadToQiniuDialog = new LoadingDialog(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mUploadToQiniuDialog.show();
    }

    @Override
    public void getRealNameResult(RealName realName) {
        if(realName.status != null && realName.status.equals(RealNameAuthStatus.OK.name())){
            realNameView.setVisibility(View.VISIBLE);
            String xx = "";
            for(int i = 0;i < realName.name.length()-1;i++){
                xx += "*";
            }
            mRealNameValueTxt.setText(xx + realName.name.substring(realName.name.length()-1));
            mRealIdValueTxt.setText(realName.id_card_no.substring(0,2)
                    +"**************"+realName.id_card_no.substring(realName.id_card_no.length()-2));
        } else{
            realNameView.setVisibility(View.GONE);
        }

    }

    private LoadingDialog mUploadToQiniuDialog;
    class UploadToQiniuTask extends AsyncTask<Object,Object,String> {
        private Context context;
        private RealmList<UploadFileAccess> accesses;
        private String mFilePath;

        public UploadToQiniuTask(RealmList<UploadFileAccess> data,String path,Context c){
            accesses = data;
            context = c;
            mFilePath = path;
        }

        @Override
        protected String doInBackground(Object[] params) {
            String url;
            try {
                url = QiNiuStorateAPI.uploadFile(accesses.get(0),mFilePath);
            } catch (QiNiuStorateAPI.QiNiuUploadException e) {
                e.printStackTrace();
                return null;
            }
            return url;
        }

        @Override
        protected void onPostExecute(String url) {
            if(Util.isEmpty(url)){
                Toast.makeText(context,"上传失败",Toast.LENGTH_LONG).show();
                return;
            }
            App.mAxLoginSp.setAvatarUrl(url);
            mUploadToQiniuDialog.dismiss();
            setQiniuPicUrlToUI();
            modifyBaseInfo();
        }
    }

    private void modifyBaseInfo(){
        Customer customer =  realm.where(Customer.class).findAll().get(0);
        if(customer.base_info == null){
            CustomerBaseInfo baseInfo = new CustomerBaseInfo();
            baseInfo.avatar = App.mAxLoginSp.getAvatarUrl();
            customer.base_info = baseInfo;
        } else{
            customer.base_info.avatar = App.mAxLoginSp.getAvatarUrl();
        }
        mPresenter.modifyCustomerBaseInfo(customer.base_info);
    }

    private void setQiniuPicUrlToUI(){
        final String url = App.mAxLoginSp.getAvatarUrl();
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(url, config,new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                if(!Util.isEmpty(url)){
                    mAvatarImg.setBackground(null);
                }
                mAvatarImg.setImageBitmap(loadedImage);
            }
        });
    }

    @Override
    public void setPresenter(IMeMsgAvatarContract.IMeMsgAvatarPresenter presenter) {
        mPresenter = presenter;
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
}
