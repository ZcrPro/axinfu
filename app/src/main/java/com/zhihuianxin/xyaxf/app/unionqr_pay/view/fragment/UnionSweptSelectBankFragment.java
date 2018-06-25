package com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.UPQRService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.base.PayMethod;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionSweptContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionSweptEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionSweptMainPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Vincent on 2017/12/4.
 */

public class UnionSweptSelectBankFragment extends BaseRealmFragment implements IunionSweptContract.IunionSweptView,View.OnTouchListener{
    public static final String EXTRA_SWEPT_ENTITY = "extraSweptEntity";
    public static Fragment newInstance(UnionSweptEntity entity) {
        UnionSweptSelectBankFragment fragment = new UnionSweptSelectBankFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_SWEPT_ENTITY,entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    public interface ISweptBankInterface{
        void sweptSelectBankFragClose();
        void sweptSelectBack();
        void swepGotoAddBank();
    }

    private ISweptBankInterface iSweptBankInterface;
    private IunionSweptContract.IunionSweptPresenter presenter;
    private View backView;
    private UnionSweptEntity sweptEntity;
    private LinearLayout linearLayout;
    private View addCardView;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        sweptEntity = (UnionSweptEntity) getArguments().getSerializable(EXTRA_SWEPT_ENTITY);
        new UnionSweptMainPresenter(getActivity(),this);

        get_pay_methods(view);

    }

    View.OnClickListener bankItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setSelect(v.getTag());
            UPBankCard card = sweptEntity.getBankCards().get((Integer) v.getTag());
            App.mAxLoginSp.setUnionSelBankId(card.getId());

            iSweptBankInterface.sweptSelectBack();
        }
    };

    private void setSelect(Object index){
        for(int i = 0;i < sweptEntity.getBankCards().size();i++){
            if(linearLayout.getChildAt(i).getTag() == index){
                linearLayout.getChildAt(i).findViewById(R.id.alreadySelect).setVisibility(View.VISIBLE);
            } else{
                linearLayout.getChildAt(i).findViewById(R.id.alreadySelect).setVisibility(View.GONE);
            }
        }
    }

    private void setBankIcon(final String url, final ImageView imageView){
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(url, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (Util.isEmpty(url)) {
                    imageView.setBackgroundResource(R.drawable.defaulf_circle);
                } else{
                    imageView.setImageBitmap(loadedImage);
                }
            }
        });
    }

    public void setOnSweptBankListClickListener(ISweptBankInterface iSweptBankInterface){
        this.iSweptBankInterface = iSweptBankInterface;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.unionpay_ar_select_bank;
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {}
    @Override
    public void JudgePayPwdResult(PaymentConfig config) {}
    @Override
    public void getC2BCodeResult(String qr_code) {}
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
    public boolean onTouch(View v, MotionEvent event) {return true;}
    @Override
    public void setPresenter(IunionSweptContract.IunionSweptPresenter presenter) {this.presenter = presenter;}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}


    private void get_pay_methods(final View view){
            RetrofitFactory.setBaseUrl(AppConstant.URL);
            Map<String, Object> map = new HashMap<>();
            UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
            upqrService.get_pay_methods(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<Object>(getActivity(), true, null) {
                        @Override
                        public void onNext(Object o) {
                            payResponse payResponse = new Gson().fromJson(o.toString(), UnionSweptSelectBankFragment.payResponse.class);
                            backView = view.findViewById(R.id.backView);
                            addCardView = view.findViewById(R.id.addSelect);
                            addCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    iSweptBankInterface.swepGotoAddBank();
                                }
                            });
                            for (int i = 0; i < payResponse.pay_methods.size(); i++) {
                                if (payResponse.pay_methods.get(i).purpose!=null&&payResponse.pay_methods.get(i).card==null&&!TextUtils.isEmpty(payResponse.pay_methods.get(i).promotion_hint)){
                                    view.findViewById(R.id.couponText).setVisibility(View.VISIBLE);
                                    ((TextView)view.findViewById(R.id.couponText)).setText(payResponse.pay_methods.get(i).promotion_hint);
                                }
                            }
                            backView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    iSweptBankInterface.sweptSelectBankFragClose();
                                }
                            });
                            linearLayout = (LinearLayout) view.findViewById(R.id.itemContainer);

                            int select = 0;
                            for(int i = 0;i < sweptEntity.getBankCards().size();i++){
                                if(sweptEntity.getBankCards().get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())){
                                    select = i;
                                }
                                View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.unionbanklist_item,null);
                                ((TextView)itemView.findViewById(R.id.bankName)).setText(sweptEntity.getBankCards().get(i).getIss_ins_name()+" "+
                                        sweptEntity.getBankCards().get(i).getCard_type_name());
                                ((TextView)itemView.findViewById(R.id.cardNum)).setText("("+sweptEntity.getBankCards().get(i).getCard_no()+")");
                                setBankIcon(sweptEntity.getBankCards().get(i).getIss_ins_icon(), (ImageView) itemView.findViewById(R.id.bankIcon));
                                for (int j = 0; j <payResponse.pay_methods.size(); j++) {
                                    if (payResponse.pay_methods.get(j).card!=null&&payResponse.pay_methods.get(j).card.getId().equals(sweptEntity.getBankCards().get(i).getId())){
                                        if (!TextUtils.isEmpty(payResponse.pay_methods.get(j).promotion_hint)){
                                            ((TextView)itemView.findViewById(R.id.couponText)).setVisibility(View.VISIBLE);
                                            ((TextView)itemView.findViewById(R.id.couponText)).setText(payResponse.pay_methods.get(j).promotion_hint);
                                        }
                                    }
                                }
                                linearLayout.addView(itemView);
                                itemView.setTag(i);
                                itemView.setOnClickListener(bankItemListener);
                            }
                            setSelect(select);
                            view.setOnTouchListener(UnionSweptSelectBankFragment.this);
                        }
                    });
    }

    public static class payResponse {
        public BaseResponse resp;
        public List<PayMethod> pay_methods;
    }
}
