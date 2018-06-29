package com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.axinfu.modellib.thrift.unqr.UPCoupon;
import com.axinfu.modellib.thrift.unqr.UPQROrder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionQrMainContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionInputPayPwdActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionServiceProActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSetPayPwdActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;

import io.realm.RealmList;

import static com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment.EXTRA_ENTITY;

/**
 * Created by Vincent on 2017/11/7.
 */

public class UnionCashierSelectBankFragment extends BaseRealmFragment implements View.OnTouchListener,IunionQrMainContract.IGetBankCardInfo{
    private View mMainView;
    private View mBackView;
    private LinearLayout linearLayout;
    private View addBankView;
    private UnionPayEntity entity;
    private TextView couponText;
    private IunionQrMainContract.IGetBankCardInfoPresenter presenter;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public interface ITell {
        void back();
        void finishActivity();
    }

    public ITell iTell;
    public void setItell(ITell itell){
        this.iTell = itell;
    }

    public static Fragment newInstance(UnionPayEntity entity) {
        UnionCashierSelectBankFragment fragment = new UnionCashierSelectBankFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ENTITY,entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        new UnionQrMainPresenter(getActivity(),this);
        entity = (UnionPayEntity) getArguments().getSerializable(UnionCashierFragment.EXTRA_ENTITY);

        mMainView = view;
        couponText = (TextView) mMainView.findViewById(R.id.couponText);
        addBankView = mMainView.findViewById(R.id.addSelect);
        mBackView = mMainView.findViewById(R.id.backView);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iTell.back();
            }
        });
        linearLayout = (LinearLayout) mMainView.findViewById(R.id.itemContainer);

        addBankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.hasBankCard){
                    Intent intent = new Intent(getActivity(), UnionHtmlActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, true);
                    bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    startActivity(new Intent(getActivity(), UnionSweptEmptyCardActivity.class));
                }

//                Intent intent = new Intent(getActivity(),UnionServiceProActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(UnionServiceProActivity.EXTRA_SHOW_BTN,"1");
//                intent.putExtras(bundle);
//                startActivityForResult(intent,UnionServiceProActivity.REQUEST_SURE_PRO);
            }
        });

        int select = 0;
        for(int i = 0;i < entity.getBankCards().size();i++){
            if(entity.getBankCards().get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())){
                select = i;
            }
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.unionbanklist_item,null);
            ((TextView)itemView.findViewById(R.id.bankName)).setText(entity.getBankCards().get(i).getIss_ins_name()+" "+
                    entity.getBankCards().get(i).getCard_type_name());
            ((TextView)itemView.findViewById(R.id.cardNum)).setText("("+entity.getBankCards().get(i).getCard_no()+")");
            setBankIcon(entity.getBankCards().get(i).getIss_ins_icon(), (ImageView) itemView.findViewById(R.id.bankIcon));
            linearLayout.addView(itemView);
            itemView.setTag(i);
            itemView.setOnClickListener(bankItemListener);
        }

        setSelect(select);
        view.setOnTouchListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UnionServiceProActivity.REQUEST_SURE_PRO){
            if(resultCode == Activity.RESULT_OK){
                presenter.JudgePayPwd();
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

    @Override
    public void judgePayPwdResult(PaymentConfig config) {
        // todo
        if(config.has_pay_password){
            Intent i = new Intent(getActivity(),UnionInputPayPwdActivity.class);
            Bundle b = new Bundle();
            b.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
            i.putExtras(b);
            startActivity(i);
        } else{
            Intent i = new Intent(getActivity(),UnionSetPayPwdActivity.class);
            Bundle b = new Bundle();
            b.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
            i.putExtras(b);
            startActivity(i);
        }
    }

    View.OnClickListener bankItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setSelect(v.getTag());
            UPBankCard card = entity.getBankCards().get((Integer) v.getTag());
            App.mAxLoginSp.setUnionSelBankId(card.getId());

            iTell.back();
//            Intent i = new Intent(getActivity(), UnionPayActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
//            bundle.putBoolean(UnionCashierFragment.EXTRA_SHOW_UNIONCASHIER,true);
//            i.putExtras(bundle);
//            startActivity(i);
        }
    };

    private void setSelect(Object index){
        for(int i = 0;i < entity.getBankCards().size();i++){
            if(linearLayout.getChildAt(i).getTag() == index){
                linearLayout.getChildAt(i).findViewById(R.id.alreadySelect).setVisibility(View.VISIBLE);
            } else{
                linearLayout.getChildAt(i).findViewById(R.id.alreadySelect).setVisibility(View.GONE);
            }
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.unionpay_ar_select_bank;
    }
    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {}
    @Override
    public void applyBankCardResult(String addCardUrl) {}
    @Override
    public void removeBankCardResult() {}
    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {}
    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) { }
    @Override
    public void setPresenter(IunionQrMainContract.IGetBankCardInfoPresenter presenter) {this.presenter = presenter;}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
