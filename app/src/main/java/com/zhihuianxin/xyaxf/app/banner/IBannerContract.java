package com.zhihuianxin.xyaxf.app.banner;

import com.axinfu.modellib.thrift.message.Advertise;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.List;

/**
 * Created by Vincent on 2016/10/11.
 */

public interface IBannerContract {

    interface IBannerView extends BaseView<IBannerPresenter>{
        void bannerSuccess(List<Advertise> list);
        void bannerFailure();
    }

    interface  IBannerPresenter extends BasePresenter{
        void loadBannerData();
    }
}
