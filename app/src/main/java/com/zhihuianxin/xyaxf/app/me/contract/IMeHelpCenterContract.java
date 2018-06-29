package com.zhihuianxin.xyaxf.app.me.contract;

import modellib.thrift.app.QuestionAnswer;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/11/11.
 */

public interface IMeHelpCenterContract {
    interface IMeHelpCenterView extends BaseView<IMeHelpCenterPresenter>{
        void getQuestionSuccess(RealmList<QuestionAnswer> questionAnswers);
    }

    interface IMeHelpCenterPresenter extends BasePresenter{
        void getQuestion();
    }
}
