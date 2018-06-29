package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.app.Appendix;
import com.axinfu.modellib.thrift.base.Feedback;
import com.axinfu.modellib.thrift.base.OrderStatus;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.resource.UploadFileAccess;
import com.axinfu.modellib.thrift.unqr.UPQROrderType;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.api.QiNiuStorateAPI;
import com.zhihuianxin.xyaxf.app.me.contract.IMeFeedBackContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeFeedBackPresenter;
import com.zhihuianxin.xyaxf.app.me.view.adapter.FeeInfoAdapter;
import com.zhihuianxin.xyaxf.app.ocp.PricingStrategy;
import com.zhihuianxin.xyaxf.app.ocp.adapter.StrategyAdapter;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.RealmList;

import static com.zhihuianxin.xyaxf.app.me.view.activity.MeFeedBackActivity.EXTRA_FEEDBACK_CONTENT;
import static com.zhihuianxin.xyaxf.app.me.view.activity.MeFeedBackActivity.REQUEST_FEEDBACK_INFO_TAKE_PIC;

/**
 * Created by Vincent on 2016/10/24.
 */

public class MePayListDetailActivity extends BaseRealmActionBarActivity implements IMeFeedBackContract.IMeFeedBackView {
    public static final String EXTRA_DATA = "extra_date";
    @InjectView(R.id.pay_for)
    TextView payFor;
    @InjectView(R.id.pay_amount)
    TextView payAmount;
    @InjectView(R.id.org_amount)
    TextView orgAmount;
    @InjectView(R.id.pay_status)
    TextView payStatus;
    @InjectView(R.id.amount_lable)
    TextView amountLable;
    @InjectView(R.id.amount_value)
    TextView amountValue;
    @InjectView(R.id.up_shop_amount)
    RelativeLayout upShopAmount;
    @InjectView(R.id.rc_strategys)
    RecyclerView rcStrategys;
    @InjectView(R.id.rc_coupon_info)
    RecyclerView rcCouponInfo;
    @InjectView(R.id.pay_way)
    TextView mPayWayText;
    @InjectView(R.id.time_type)
    TextView timeType;
    @InjectView(R.id.time)
    TextView mTimeText;
    @InjectView(R.id.sys_card_id)
    TextView mSysCardIdText;
    @InjectView(R.id.way_card_id)
    TextView mWayCardIdText;
    @InjectView(R.id.ecard_no)
    TextView ecardNo;
    @InjectView(R.id.tv_fee_title)
    TextView tvFeeTitle;
    @InjectView(R.id.rc_fees)
    RecyclerView rcFees;
    @InjectView(R.id.title_mark)
    TextView titleMark;
    @InjectView(R.id.remark)
    TextView remark;
    @InjectView(R.id.rl_mark)
    RelativeLayout rlMark;
    @InjectView(R.id.help_icon)
    ImageView helpIcon;
    @InjectView(R.id.help_text)
    TextView helpText;
    @InjectView(R.id.question)
    RelativeLayout question;
    @InjectView(R.id.feedback_value)
    EditText feedbackValue;
    @InjectView(R.id.photo_list)
    LinearLayout photoList;
    @InjectView(R.id.add_photo)
    ImageView addPhoto;
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.ll_feed_back)
    LinearLayout llFeedBack;
    @InjectView(R.id.ecard_amount)
    TextView ecardAmount;
    @InjectView(R.id.rl_ecard_no)
    RelativeLayout rlEcardNo;
    @InjectView(R.id.rl_ecard_amount)
    RelativeLayout rlEcardAmount;
    @InjectView(R.id.fee_title)
    TextView feeTitle;
    @InjectView(R.id.fee_info)
    LinearLayout feeInfo;
    @InjectView(R.id.rl_type)
    RelativeLayout rlType;
    @InjectView(R.id.voucher_num)
    TextView voucherNum;
    @InjectView(R.id.rl_voucher_num)
    RelativeLayout rlVoucherNum;
    @InjectView(R.id.trade_type)
    TextView tradeType;


    private PaymentRecord mRecord;
    private PictureListController pictureListController;
    private int mAbbreviationSize = 0;
    private DisplayMetrics metrics;
    private IMeFeedBackContract.IMeFeedBackPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        new MeFeedBackPresenter(this, this);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mRecord = (PaymentRecord) getIntent().getExtras().getSerializable(EXTRA_DATA);
        mAbbreviationSize = getImgWidth();
        pictureListController = new PictureListController(photoList);
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
//        mPayTypeText.setText(mRecord.trade_summary);
//        mPayTypeText.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mPayTypeText.getLineCount() > 1) {
//                    mPayTypeText.setGravity(Gravity.LEFT);
//                } else {
//                    mPayTypeText.setGravity(Gravity.RIGHT);
//                }
//            }
//        });

        if (mRecord.pay_remark != null && !TextUtils.isEmpty(mRecord.pay_remark)) {
            rlMark.setVisibility(View.VISIBLE);
            remark.setText(mRecord.pay_remark);
        } else {
            rlMark.setVisibility(View.GONE);
        }

        int[] timeItems = mRecord.order_time != null ? Util.getTimeItems(mRecord.order_time) : null;
        mTimeText.setText(timeItems == null ? "" : String.format("%04d-%02d-%02d %02d:%02d:%02d",
                timeItems[0], timeItems[1], timeItems[2], timeItems[3], timeItems[4], timeItems[5]));
        mSysCardIdText.setText(mRecord.order_no);

        if (mRecord.pay_channel.equals("UnionPay")) {
            mPayWayText.setText("银联在线");
        } else if (mRecord.pay_channel.equals("RFID")) {
            mPayWayText.setText("一卡通支付");
        } else if (mRecord.pay_channel.equals("CCBWapPay")) {
            mPayWayText.setText("建设银行支付");
        } else if (mRecord.pay_channel.equals("NoNeed")) {
            mPayWayText.setText("溢缴款抵扣");
        } else if (mRecord.pay_channel.equals("UPWapPay")) {
            mPayWayText.setText("银联Wap支付");
        } else if (mRecord.pay_channel.equals("ApplePay")) {
            mPayWayText.setText("ApplePay");
        } else if (mRecord.pay_channel.equals("AliPay")) {
            mPayWayText.setText("支付宝");
        } else if (mRecord.pay_channel.equals("WxPay")) {
            mPayWayText.setText("微信支付");
        } else if (mRecord.pay_channel.equals("WxQrPay")) {
            mPayWayText.setText("微信支付");
        } else if (mRecord.pay_channel.equals("WxPubPay")) {
            mPayWayText.setText("微信支付");
        } else if (mRecord.pay_channel.equals("QuickPay")) {
            mPayWayText.setText("快捷支付");
        } else if (mRecord.pay_channel.equals("WxAppPay")) {
            mPayWayText.setText("微信支付");
        } else if (mRecord.pay_channel.equals("AliAppPay")) {
            mPayWayText.setText("支付宝");
        } else if (mRecord.pay_channel.equals("UPQRPay")) {
//            mPayWayText.setText("银联二维码主扫支付");
            if (mRecord.pay_method.card != null && mRecord.pay_method.card.getIss_ins_name() != null && mRecord.pay_method.card.getCard_type_name() != null && mRecord.pay_method.card.getCard_no() != null) {
                mPayWayText.setText(mRecord.pay_method.card.getIss_ins_name() + mRecord.pay_method.card.getCard_type_name() + "(" + mRecord.pay_method.card.getCard_no() + ")");
            } else {
                mPayWayText.setText("银联二维码主扫支付");
            }
        } else if (mRecord.pay_channel.equals("UPQRQuickPay")) {
            if (mRecord.pay_method.card != null && mRecord.pay_method.card.getIss_ins_name() != null && mRecord.pay_method.card.getCard_type_name() != null && mRecord.pay_method.card.getCard_no() != null) {
                mPayWayText.setText(mRecord.pay_method.card.getIss_ins_name() + mRecord.pay_method.card.getCard_type_name() + "(" + mRecord.pay_method.card.getCard_no() + ")");
            } else {
                mPayWayText.setText("银联二维码主扫支付");
            }
        } else if (mRecord.pay_channel.equals("UPTokenPay")) {
            mPayWayText.setText(mRecord.pay_method.card.getIss_ins_name() + mRecord.pay_method.card.getCard_type_name() + "(" + mRecord.pay_method.card.getCard_no() + ")");
        } else {
            mPayWayText.setText("未知支付方式");
        }
//
        mWayCardIdText.setText(mRecord.order_no);

        feedbackValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!Util.isEmpty(editable.toString())) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
            }
        });

        feedbackValue.setText(getFeedBackContent());

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TakePictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TakePictureActivity.EXTRA_FROM_SIMPLE, "simple");
                bundle.putInt(TakePictureActivity.EXTRA_MAX_MUTI_SELECT, 4 - pictureListController.getPictureSize());
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_FEEDBACK_INFO_TAKE_PIC);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQiNiuAccess();
            }
        });

        //新增接口字段
        switch (mRecord.payfor) {
            case "RechargeECard":
                payFor.setText("一卡通充值");
                rlEcardAmount.setVisibility(View.VISIBLE);
                rlEcardNo.setVisibility(View.VISIBLE);
                ecardNo.setText(mRecord.ecard_account);
                ecardAmount.setText(mRecord.orig_amt);
                break;
            case "SchoolFee":
                feeInfo.setVisibility(View.VISIBLE);
                payFor.setText("缴费");
                rcFees.setVisibility(View.VISIBLE);
                rcFees.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                rcFees.setHasFixedSize(true);
                feeTitle.setText(mRecord.fee.title);
                FeeInfoAdapter feeInfoAdapter = new FeeInfoAdapter(this, mRecord.fee.subfees, R.layout.payment_strategy_list_item);
                rcFees.setAdapter(feeInfoAdapter);
                feeInfoAdapter.notifyDataSetChanged();
                break;
            case "ScanPay":
                payFor.setText("扫码支付");
                break;
            case "AxfQRPay":
                payFor.setText("向" + mRecord.merchant_name + "商户付款");
                if (!TextUtils.isEmpty(mRecord.orig_amt)) {
                    upShopAmount.setVisibility(View.VISIBLE);
                    amountValue.setText(mRecord.orig_amt);
                }
                if (mRecord.strategy != null && mRecord.strategy.size() > 0) {
                    rcStrategys.setVisibility(View.VISIBLE);
                    rcStrategys.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    rcStrategys.setHasFixedSize(true);
                    List<PricingStrategy> list = new ArrayList<>();
                    for (int i = 0; i < mRecord.strategy.size(); i++) {
                        PricingStrategy pricingStrategy = new PricingStrategy();
                        pricingStrategy.name = mRecord.strategy.get(i).name;
                        pricingStrategy.id = mRecord.strategy.get(i).id;
                        pricingStrategy.float_amt = mRecord.strategy.get(i).float_amt;
                        list.add(pricingStrategy);
                    }
                    StrategyAdapter strategyAdapter = new StrategyAdapter(this, list, R.layout.payment_strategy_list_item);
                    rcStrategys.setAdapter(strategyAdapter);
                }
                break;
            case "UPQRPay":
                if (mRecord.coupon_info != null && mRecord.coupon_info.size() > 0) {
                    rcCouponInfo.setVisibility(View.VISIBLE);
                    rcCouponInfo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    rcCouponInfo.setHasFixedSize(true);
                    List<PricingStrategy> list = new ArrayList<>();
                    for (int i = 0; i < mRecord.coupon_info.size(); i++) {
                        PricingStrategy pricingStrategy = new PricingStrategy();
                        pricingStrategy.name = mRecord.coupon_info.get(i).desc;
                        pricingStrategy.float_amt = mRecord.coupon_info.get(i).offst_amt;
                        list.add(pricingStrategy);
                    }
                    StrategyAdapter strategyAdapter = new StrategyAdapter(this, list, R.layout.payment_strategy_list_item);
                    rcStrategys.setAdapter(strategyAdapter);
                }
                payFor.setText("向" + mRecord.merchant_name + "商户付款");
                rlType.setVisibility(View.VISIBLE);
                if (mRecord.trade_type != null) {
                    tradeType.setText(getType(mRecord.trade_type));
                }
                rlVoucherNum.setVisibility(View.VISIBLE);
                voucherNum.setText(mRecord.voucher_num);
                break;
            default:
                payFor.setText("未知支付");
                break;
        }

        payAmount.setText(mRecord.pay_amt);
        if (!TextUtils.isEmpty(mRecord.orig_amt) && Float.parseFloat(mRecord.pay_amt) == Float.parseFloat(mRecord.orig_amt)) {
            orgAmount.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(mRecord.orig_amt)) {
                orgAmount.setVisibility(View.VISIBLE);
                orgAmount.setText(mRecord.orig_amt);
                orgAmount.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                orgAmount.setVisibility(View.GONE);
            }
        }
        payStatus.setText(OrderStatus.getOrderStatus(mRecord.order_status));

    }

    private String getType(String key){
        if(Util.isEmpty(key)){
            return "";
        }
        if(key.equals(UPQROrderType.NormalConsumption.toString())){
            return "一般消费";
        } else if(key.equals(UPQROrderType.RestrictCreditConsumption.toString())){
            return "非贷记账户消费";
        } else if(key.equals(UPQROrderType.MiniMerchantConsumption.toString())){
            return "小微商户收款";
        } else if(key.equals(UPQROrderType.ATMEnchashment.toString())){
            return "ATM取现";
        } else if(key.equals(UPQROrderType.Transfer.toString())){
            return "人到人转账";
        } else{
            return "";
        }
    }

    private void getQiNiuAccess() {
        mPresenter.getQiNiuAccess("FeedBack", "jpg", pictureListController.getPictureSize());
    }

    private int getImgWidth() {
        return (int) (((metrics.widthPixels - 15 * 2 * metrics.density) / 4) - 5 * 2 * metrics.density);
    }

    private String getFeedBackContent() {
        String content;
        if (getIntent().getExtras() == null || Util.isEmpty(getIntent().getExtras().getString(EXTRA_FEEDBACK_CONTENT))) {
            content = "";
        } else {
            content = getIntent().getExtras().getString(EXTRA_FEEDBACK_CONTENT).replace("\\n", "\n");
        }
        return content;

    }

    @OnClick(R.id.question)
    public void onBtnQuestion() {
//        Intent intent = new Intent(this,MeFeedBackActivity.class);
//        Bundle bundle = new Bundle();
//        Bundle bundle = new Bundle();
//        bundle.putString(MeFeedBackActivity.EXTRA_FEEDBACK_CONTENT,getFeedBackContext());
//        bundle.putBoolean(MeFeedBackActivity.isEnableTAG,true);
//        intent.putExtras(bundle);
//        startActivity(intent);
        llFeedBack.setVisibility(View.VISIBLE);
    }

    private String getFeedBackContext() {
        return
                "支付金额：" + payAmount.getText() + "\n" +
                        "交易名称：" + payFor.getText() + "\n" +
                        "交易状态：" + payStatus.getText() + "\n" +
                        "创建时间：" + mTimeText.getText() + "\n" +
                        "系统订单号：" + mSysCardIdText.getText() + "\n" +
                        "支付渠道：" + mPayWayText.getText() + "\n" +
                        "我对这一笔交易留有疑问:";
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_paylist_detail_activity;
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

    class PictureListController {
        private LinearLayout layout;

        public PictureListController(LinearLayout linearLayout) {
            layout = linearLayout;
        }

        View getItemView(final String url) {
            View view = LayoutInflater.from(MePayListDetailActivity.this).inflate(R.layout.me_feedback_picture_item, null);
            Bitmap bitmap = decodeSampledBitmapFromFd(url, mAbbreviationSize, mAbbreviationSize);
            ((ImageView) view.findViewById(R.id.image_item)).setImageBitmap(bitmap);
            view.findViewById(R.id.image_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePicture(url);
                }
            });
            view.setTag(url);
            return view;
        }

        void addPicture(String url) {
            layout.addView(getItemView(url));
            controllAddImg();
        }

        void deletePicture(String url) {
            for (int i = 0; i < layout.getChildCount(); i++) {
                if (layout.getChildAt(i).getTag().equals(url)) {
                    layout.removeViewAt(i);
                }
            }
            controllAddImg();
        }

        void controllAddImg() {
            if (layout.getChildCount() < 4) {
                addPhoto.setVisibility(View.VISIBLE);
            } else {
                addPhoto.setVisibility(View.GONE);
            }
        }

        private ArrayList<String> getPicPaths() {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < layout.getChildCount(); i++) {
                list.add(layout.getChildAt(i).getTag().toString());
            }
            return list;
        }

        private int getPictureSize() {
            return layout.getChildCount();
        }
    }


    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }


    private LoadingDialog mUploadToQiniuDialog;

    class UploadToQiniuTask extends AsyncTask<Object, Object, ArrayList<Appendix>> {
        private Context context;
        private RealmList<UploadFileAccess> accesses;

        public UploadToQiniuTask(RealmList<UploadFileAccess> data, Context c) {

            accesses = data;
            context = c;
        }

        @Override
        protected ArrayList<Appendix> doInBackground(Object[] params) {
            ArrayList<Appendix> appendixArrayList = new ArrayList<>();
            for (int i = 0; i < pictureListController.getPictureSize(); i++) {
                try {
                    Appendix appendix = new Appendix();
                    appendix.url = QiNiuStorateAPI.uploadFile(accesses.get(i), pictureListController.getPicPaths().get(i));
                    appendix.id = accesses.get(i).key;
                    appendixArrayList.add(appendix);
                } catch (QiNiuStorateAPI.QiNiuUploadException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return appendixArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Appendix> appendixArrayList) {
            if (appendixArrayList == null) {
                Toast.makeText(context, "上传失败", Toast.LENGTH_LONG).show();
                return;
            }
            mUploadToQiniuDialog.dismiss();
            mPresenter.feedBack(getFeedBackContext() + "\r\n" + feedbackValue.getText().toString().trim(), appendixArrayList);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FEEDBACK_INFO_TAKE_PIC) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras() != null && data.getExtras().getSerializable(TakePictureActivity.EXTRA_OUTPUT_PATH_MUTI) != null) {
                    ArrayList<String> mutiPath = (ArrayList<String>) data.getExtras().getSerializable(TakePictureActivity.EXTRA_OUTPUT_PATH_MUTI);
                    for (String url : mutiPath) {
                        pictureListController.addPicture(url);
                    }

                } else {
                    pictureListController.addPicture(data.getStringExtra(TakePictureActivity.EXTRA_OUTPUT_PATH));
                }

            } else if (resultCode == CropImageActivity.RESULT_CAN_NOT_LOAD_IMAGE_URI || resultCode == CropImageActivity.RESULT_CAN_NOT_CREATE_OUTPUT_FILE) {
                Log.w("TAG", "can not load image: ", (Exception) data.getSerializableExtra(CropImageActivity.EXTRA_ERROR));
            }
        }
    }

    @Override
    public void getFeedBackList(RealmList<Feedback> feedbacks) {
        //no use
    }

    @Override
    public void setPresenter(IMeFeedBackContract.IMeFeedBackPresenter presenter) {
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

    @Override
    public void getQiNiuAccessSuccess(RealmList<UploadFileAccess> accesses) {
        UploadToQiniuTask task = new UploadToQiniuTask(accesses, this);
        mUploadToQiniuDialog = new LoadingDialog(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mUploadToQiniuDialog.show();
    }

    @Override
    public void feedBackSuccess() {
        Toast.makeText(this, "感谢您的反馈,我们会尽快给您答复！", Toast.LENGTH_LONG).show();
        goBack();
    }

    private void goBack() {
        setResult(RESULT_OK);
        finish();
    }

}
