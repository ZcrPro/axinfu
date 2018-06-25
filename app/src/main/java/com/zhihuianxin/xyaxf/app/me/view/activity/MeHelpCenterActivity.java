package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.app.QuestionAnswer;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.WebPageActivity;
import com.zhihuianxin.xyaxf.app.me.contract.IMeHelpCenterContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeHelpCenterPresenter;
import com.zhihuianxin.xyaxf.app.me.view.adapter.HelpCenterAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.QuestionAnswerRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Vincent on 2016/11/11.
 */

public class MeHelpCenterActivity extends BaseRealmActionBarActivity implements IMeHelpCenterContract.IMeHelpCenterView{
    private IMeHelpCenterContract.IMeHelpCenterPresenter mPresenter;
    @InjectView(R.id.swipeView)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.question_list)
    ListView mListView;
    private HelpCenterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        new MeHelpCenterPresenter(this,this);

        initView();
    }

    private void initView(){
        mAdapter = new HelpCenterAdapter(this);
        mListView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getQuestion();
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);
            }
        });
        mListView.setOnItemClickListener(itemClickListener);

        RealmResults<QuestionAnswer> questionAnswers = realm.where(QuestionAnswer.class).findAll();
        if(questionAnswers.size() == 0){
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            mPresenter.getQuestion();
        } else{
            setDBDataToUI(questionAnswers);
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            QuestionAnswer questionAnswer = (QuestionAnswer) view.getTag();
            if(questionAnswer.type != null){
                if(questionAnswer.type.equals("Text")){
                    Intent intent = new Intent(MeHelpCenterActivity.this,MeHelpCenterDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MeHelpCenterDetailActivity.EXTRA_QUESTION, questionAnswer);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if(questionAnswer.type.equals("Web")){
                    Intent intent = new Intent(MeHelpCenterActivity.this, WebPageActivity.class);
                    intent.putExtra(WebPageActivity.EXTRA_URL, questionAnswer.content);
                    intent.putExtra(WebPageActivity.EXTRA_TITLE, questionAnswer.question);
                    startActivity(intent);
                } else{
                    //do nothing
                }
            }
        }
    };

    private void setDBDataToUI(RealmResults<QuestionAnswer> dataList){
        for (QuestionAnswer obj : dataList){
            QuestionAnswer answer = new QuestionAnswer();
            answer.question = ((QuestionAnswerRealmProxy)obj).realmGet$question();
            answer.content = ((QuestionAnswerRealmProxy)obj).realmGet$content();
            answer.create_time = ((QuestionAnswerRealmProxy)obj).realmGet$create_time();
            answer.id = ((QuestionAnswerRealmProxy)obj).realmGet$id();
            answer.type = ((QuestionAnswerRealmProxy)obj).realmGet$type();
            mAdapter.add(answer);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void saveDataInDB(final RealmList<QuestionAnswer> questionAnswers){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(questionAnswers);
            }
        });

    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_help_center_activity;
    }

    @Override
    public void getQuestionSuccess(final RealmList<QuestionAnswer> questionAnswers) {
        mAdapter.clear();
        if(questionAnswers.size() == 0){
            Toast.makeText(this,"暂无数据！",Toast.LENGTH_LONG).show();
            return;
        }
        for (QuestionAnswer obj : questionAnswers){
            mAdapter.add(obj);
        }
        mAdapter.notifyDataSetChanged();
        saveDataInDB(questionAnswers);
    }


    @Override
    public void setPresenter(IMeHelpCenterContract.IMeHelpCenterPresenter presenter) {
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
        mSwipeRefreshLayout.setRefreshing(false);
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
