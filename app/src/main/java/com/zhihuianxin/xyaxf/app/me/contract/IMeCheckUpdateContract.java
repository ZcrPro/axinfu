package com.zhihuianxin.xyaxf.app.me.contract;

import modellib.thrift.app.PluginInfo;
import modellib.thrift.app.Update;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016/11/16.
 */

public interface IMeCheckUpdateContract {
    interface IMeCheckUpdateView extends BaseView<IMeCheckUpdatePresenter>{
        void checkUpdateSuccess(Update update, ArrayList<Update> plugin_updates);
        void judgePayPwdResult(PaymentConfig config);
        void getRealNameResult(RealName realName);// 获取实名认证状态
    }

    interface IMeCheckUpdatePresenter extends BasePresenter{
        void checkUpdate(ArrayList<PluginInfo> pluginInfos);
        void JudgePayPwd();
        void getRealName();// 获取实名认证状态
    }
}
