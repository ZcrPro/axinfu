package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.home.qrcode.QRCodeActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.ScreenShotListenManager;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionSweptContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionSweptEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionSweptMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionSweptSelectBankFragment;
import com.zhihuianxin.xyaxf.app.view.RotateTextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.RealmList;

/**
 * Created by Vincent on 2017/11/30.
 */

public class UnionSweptCodeActivity extends BaseRealmActionBarActivity implements UnionSweptSelectBankFragment.ISweptBankInterface,IunionSweptContract.IunionSweptView{
    public static final String EXTRA_SHOWFRAG = "extraShowFrag";
    final static int WHITE = 0xFFFFFFFF;
    final static int BLACK = 0xFF000000;

    @InjectView(R.id.barcodeimg)
    ImageView barCodeImg;
    @InjectView(R.id.qrcodeimg)
    ImageView qrCodeImg;
    @InjectView(R.id.bigimgView)
    View bigImgView;
    @InjectView(R.id.bigImg)
    ImageView bigImg;
    //    @InjectView(R.id.barcodeinframeTxt)
//    RotateTextView barCodeInFragmeTxt;
    @InjectView(R.id.cardtxt_id)
    TextView mBankTxt;
    @InjectView(R.id.changebank)
    View changeBankTxt;
    @InjectView(R.id.barcode_txt)
    TextView qrCodeTxt;
    @InjectView(R.id.cashier)
    View mCashierView;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.backAnimView)
    View mBackAlertView;
    @InjectView(R.id.click_focus)
    Button mSureExitBtn;
    @InjectView(R.id.backview)
    View backView;
    @InjectView(R.id.scanimgid)
    ImageView scanImg;
    @InjectView(R.id.barcodeTops)
    View barCodeTipsView;
    @InjectView(R.id.pointShowView)
    View pointShowView;
    @InjectView(R.id.brcodeLinView)
    LinearLayout brcodeLinView;

    private IunionSweptContract.IunionSweptPresenter presenter;
    private String qrcode = "";
    private UnionSweptEntity sweptEntity;
    private UnionSweptSelectBankFragment sweptSelectBankFragment;
    private FragmentTransaction ft;
    private ScreenShotListenManager screenShotListenManager;
    private DisplayMetrics metrics;
    private boolean isFromSelectBank = false;

    Timer timer = new Timer();

//    private Subscription rxSubscription;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.addRule(RelativeLayout.LEFT_OF,R.id.bigImg);
                params.setMargins(0,0,-180,0);
                //barCodeInFragmeTxt.setLayoutParams(params);

                //barCodeInFragmeTxt.setTextColor(getResources().getColor(R.color.black));
            } else{
                UPBankCard bankCard = getSelectBank();
                if(!UnionSweptCodeActivity.this.isFinishing()){
                    presenter.getC2BCode(bankCard.getId());
                }
            }
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.union_swept_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        setLight(this,254);
        initViews();
        presenter.getBankCard();

//        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
//            @Override
//            public void call(Object event) {
//                if (event.equals("fixed_activity_add_bank_def")) {
//                    setLight(UnionSweptCodeActivity.this,254);
//                    initViews();
//                    presenter.getBankCard();
//
//                }
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (!rxSubscription.isUnsubscribed()) {
//            rxSubscription.unsubscribe();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenShotListenManager = ScreenShotListenManager.newInstance(this);
        screenShotListenManager.startListen();
        screenShotListenManager.setListener(new ScreenShotListenManager.OnScreenShotListener() {
            @Override
            public void onShot(String imagePath) {
                showAlertAnim();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        screenShotListenManager.stopListen();
        bigImgView.setVisibility(View.GONE);
        App.addActivities(this);
    }

    private void initViews(){
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

//        try {
//            String command = "chmod 777 " + Environment.getExternalStorageDirectory();;
//            Runtime runtime = Runtime.getRuntime();
//            Process proc = runtime.exec(command);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        new UnionSweptMainPresenter(this,this);
        sweptEntity = new UnionSweptEntity();
        findViewById(R.id.action_bar).setVisibility(View.GONE);
        changeBankTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectFragment();
            }
        });
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scanImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UnionSweptCodeActivity.this,QRCodeActivity.class));
            }
        });

        timer.schedule(new TimerTask() {
            public void run() {
                if(!Util.isEmpty(qrcode) && sweptEntity.getBankCards().size() > 0){
                    handler.sendMessage(new Message());
                }
            }
        } , 0,1000*60);
        mGrayBg.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {}});
    }

    private void initFragments(){
        sweptSelectBankFragment = (UnionSweptSelectBankFragment) UnionSweptSelectBankFragment.newInstance(sweptEntity);
        sweptSelectBankFragment.setOnSweptBankListClickListener(this);
        FragmentManager manager = getSupportFragmentManager();
        ft = manager.beginTransaction();
        if(!sweptSelectBankFragment.isAdded()){
            ft.add(R.id.container, sweptSelectBankFragment);
        }
    }

    private void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    private void showSelectFragment(){
        mCashierView.setVisibility(View.VISIBLE);
        initFragments();
        ft.show(sweptSelectBankFragment).commitAllowingStateLoss();
    }

    View.OnClickListener barCodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            final int height = wm.getDefaultDisplay().getHeight();
            barCodeTipsView.setVisibility(View.VISIBLE);
            showBrCodeToLinearView(qrcode);
            brcodeLinView.setVisibility(View.VISIBLE);
            bigImgView.setVisibility(View.VISIBLE);
            try {
                Bitmap ori = createCode(qrcode, BarcodeFormat.CODE_128, height * 4 / 5, 180);
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                Bitmap newBM = Bitmap.createBitmap(ori, 0, 0, height * 4 / 5, 180, matrix, false);
                bigImg.setImageBitmap(newBM);

                // 设置文字旋转
                Animation rotate = AnimationUtils.loadAnimation(UnionSweptCodeActivity.this, R.anim.barcode_re_txt);
                rotate.setFillAfter(true);
                //barCodeInFragmeTxt.startAnimation(rotate);
            }

            catch (WriterException e) {
                e.printStackTrace();
            }
            bigImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bigImgView.setVisibility(View.GONE);
                }
            });
        }
    };

    private void initCode(){
        qrCodeTxt.setText("点击查看付款码数字");
        qrCodeTxt.setOnClickListener(barCodeClickListener);
        try {
            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            final int width = wm.getDefaultDisplay().getWidth();
            barCodeImg.setImageBitmap(createCode(qrcode,BarcodeFormat.CODE_128,width*4/5,180));
            qrCodeImg.setImageBitmap(createCode(qrcode,BarcodeFormat.QR_CODE, (int) (width*0.47),(int) (width*0.47)));
            barCodeImg.setOnClickListener(barCodeClickListener);
            qrCodeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barCodeTipsView.setVisibility(View.GONE);
                    brcodeLinView.setVisibility(View.GONE);
                    bigImgView.setVisibility(View.VISIBLE);
                    try {
                        bigImg.setImageBitmap(createCode(qrcode,BarcodeFormat.QR_CODE,width*4/5,width*4/5));
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    bigImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bigImgView.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void showBrCodeToLinearView(String code){
        brcodeLinView.removeAllViews();
        for(int i = 0;i < code.length();i++){
            LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.union_barcode_text,null);
            RotateTextView textView = (RotateTextView) layout.findViewById(R.id.text1);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setText(String.valueOf(code.charAt(i)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if((i == 4 || i == 8 || i == 12 || i == 16) && !String.valueOf(code.charAt(i)).equals("*")){
                params.setMargins(0,50,0,0);
            }
            textView.setLayoutParams(params);
            brcodeLinView.addView(layout);
        }
    }

    @OnClick(R.id.pointView)
    public void pointClick(View view){
        mGrayBg.setVisibility(View.VISIBLE);
        pointShowView.setVisibility(View.VISIBLE);
        pointShowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGrayBg.setVisibility(View.GONE);
                pointShowView.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.pointClickRecordView)
    public void pointRecordClick(View view){
        startActivity(new Intent(getActivity(), UnionSweptRecordListActivity.class));
    }

//    @OnClick(R.id.pointClickSetView)
//    public void pointSetClick(View view){
//        startActivity(new Intent(getActivity(), MePaySettingActivity.class));
//    }

    @OnClick(R.id.pointClickShuomingView)
    public void pointShuomingClick(View view){
        startActivity(new Intent(getActivity(),UnionSwepShuomingActivity.class));
    }

    @OnClick(R.id.pointClickCancelView)
    public void pointCancelClick(View view){
        mGrayBg.setVisibility(View.GONE);
        pointShowView.setVisibility(View.GONE);
    }

    private String setQrCodeSpace(String key){
        String res = "";
        char[] c = key.toCharArray();
        for(int i = 0;i < key.length();i++){
            res += c[i]+"";
        }
        return res;
    }

    private void showAlertAnim(){
        pointShowView.setVisibility(View.GONE);
        int halfScreen = (metrics.heightPixels / 2) + 250;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", 0,
                halfScreen,halfScreen + 50,halfScreen + 10,halfScreen + 35,halfScreen + 30);//450 410 435 430
        animator2.setDuration(700);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);

        mSureExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBackAlertAnim();
            }
        });
    }

    private void hideBackAlertAnim(){
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY",halfScreen + 30,0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);

        UPBankCard card = getSelectBank();
        presenter.getC2BCode(card.getId());
    }

    public static Bitmap createCode(String url, BarcodeFormat format, int widthAndHeight,int heightAndHeight)
            throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, "0");   //设置白边

        BitMatrix matrix = new MultiFormatWriter().encode(url, format, widthAndHeight, heightAndHeight,hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        if(format.equals(BarcodeFormat.QR_CODE)){
            //画黑点
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    }
                }
            }
        } else if(format.equals(BarcodeFormat.CODE_128)){
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @OnClick(R.id.refreshCodeView)
    public void refreshOnClick(){
        presenter.getC2BCode(getSelectBank().getId());
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
        if(bankCards == null || bankCards.size() == 0){
            startActivity(new Intent(this,UnionSweptEmptyCardActivity.class));
            finish();
        } else{
            ArrayList<UPBankCard> bankCardsNew = new ArrayList<>();
            for(int i = 0;i < bankCards.size();i++){
                UPBankCard bankCard = new UPBankCard();
                bankCard.setId(Util.isEmpty(bankCards.get(i).getId())?"":bankCards.get(i).getId());
                bankCard.setIss_ins_name(Util.isEmpty(bankCards.get(i).getIss_ins_name())?"":bankCards.get(i).getIss_ins_name());
                bankCard.setCard_no(Util.isEmpty(bankCards.get(i).getCard_no())?"":""+bankCards.get(i).getCard_no());
                bankCard.setCard_type_name(Util.isEmpty(bankCards.get(i).getCard_type_name())?"":bankCards.get(i).getCard_type_name());
                bankCard.setIss_ins_icon(Util.isEmpty(bankCards.get(i).getIss_ins_icon()) ? "" : bankCards.get(i).getIss_ins_icon());
                bankCardsNew.add(bankCard);
            }
            sweptEntity.setBankCards(bankCardsNew);
            UPBankCard bankCard = getSelectBank();
            mBankTxt.setText(bankCard.getIss_ins_name()+bankCard.getCard_type_name()+"("+bankCard.getCard_no()+")");

            presenter.getC2BCode(getSelectBank().getId());
        }
    }

    @Override
    public void JudgePayPwdResult(PaymentConfig config) {
        if(config.has_pay_password){
            Intent intent = new Intent(this, UnionHtmlActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
            intent.putExtras(bundle);
            this.startActivity(new Intent(intent));
//            Intent i = new Intent(getActivity(),UnionInputPayPwdActivity.class);
//            startActivity(i);
        } else{
            Intent i = new Intent(getActivity(),UnionSetPayPwdActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void sweptSelectBankFragClose() {
        mCashierView.setVisibility(View.GONE);
    }

    @Override
    public void sweptSelectBack() {
        isFromSelectBank = true;
        UPBankCard bankCard = getSelectBank();
        mBankTxt.setText(bankCard.getIss_ins_name()+bankCard.getCard_type_name()+"("+bankCard.getCard_no()+")");
        presenter.getC2BCode(bankCard.getId());
        mCashierView.setVisibility(View.GONE);
    }

    @Override
    public void swepGotoAddBank() {
        App.addActivities(this);

        Intent intent = new Intent(this, UnionHtmlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
        bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, true);
        intent.putExtras(bundle);
        this.startActivity(new Intent(intent));

//        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
//
//        if (customers.size() != 0) {
//            if ( ((CustomerBaseInfoRealmProxy)((CustomerRealmProxy) customers.get(0)).realmGet$base_info()).realmGet$protocol()!= null) {
//                if ( ((CustomerBaseInfoRealmProxy)((CustomerRealmProxy) customers.get(0)).realmGet$base_info()).realmGet$protocol().size() > 0) {
//                    //存在同意了的协议
//                    presenter.JudgePayPwd();
//                }else {
//                    Intent intent = new Intent(this, UnionServiceProActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(UnionServiceProActivity.EXTRA_SHOW_BTN, "1");
//                    intent.putExtras(bundle);
//                    startActivityForResult(intent, UnionServiceProActivity.REQUEST_SURE_PRO);
//                }
//            }
//        }
////        Intent intent = new Intent(this,UnionServiceProActivity.class);
////        Bundle bundle = new Bundle();
////        bundle.putString(UnionServiceProActivity.EXTRA_SHOW_BTN,"1");
////        intent.putExtras(bundle);
////        startActivityForResult(intent,UnionServiceProActivity.REQUEST_SURE_PRO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UnionServiceProActivity.REQUEST_SURE_PRO){
            if(resultCode == RESULT_OK){
                presenter.JudgePayPwd();
            }
        }
    }

    private UPBankCard getSelectBank(){
        UPBankCard result = null;
        for(int i = 0;i < sweptEntity.getBankCards().size();i++){
            if(sweptEntity.getBankCards().get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())){
                result = sweptEntity.getBankCards().get(i);
            }
        }
        if(result == null){
            result = sweptEntity.getBankCards().get(0);
        }
        return result;
    }

    @Override
    public void getC2BCodeResult(String qr_code) {
        qrcode = qr_code;
        initCode();

        if(getIntent().getExtras() != null && getIntent().getExtras().getBoolean(EXTRA_SHOWFRAG) && !isFromSelectBank &&
                sweptEntity.getBankCards().size() > 1){// 第一次添加卡不用展示收银台
            showSelectFragment();
        }
    }

    @Override
    public void swepPayPwdResult() {}
    @Override
    public void verifyPayPwdResult(boolean is_match, int left_retry_count) {}
    @Override
    public void payOrderResult(PaymentOrder order) {}
    @Override
    public void setPayList(RealmList<PaymentRecord> payList) {}
    @Override
    public void getRealNameResult(RealName realName) {}
    @Override
    public void c2bQrVerifyPpaymentPasswordResult(boolean is_match, int left_retry_count) {}
    @Override
    public void setPresenter(IunionSweptContract.IunionSweptPresenter presenter) {this.presenter = presenter;}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
