package com.zhihuianxin.xyaxf.app.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;

import com.zhihuianxin.xyaxf.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Vincent on 2018/4/24.
 */

public class SwipeRefreshAndLoadMorePayNewListLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {

    private int mTouchSlop; //滑动到最下面时的上拉操作
    private StickyListHeadersListView mListView;
    private SwipeRefreshAndLoadMoreLayoutStick.OnLoadListener mOnLoadListener; //上拉加载监听
    private View mListViewFooter; //底部加载时的布局
    private int mYDown; //按下时的y坐标
    private int mLastY; //抬起时的y坐标
    private boolean isLoading; //是否正在加载


    public SwipeRefreshAndLoadMorePayNewListLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshAndLoadMorePayNewListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //表示触发事件的最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footerstick, null, false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //初始化listview对象
        if (mListView == null) {
            for(int i = 0;i < getChildCount();i++){
                View childView = getChildAt(i);
                if (childView instanceof StickyListHeadersListView) {
                    mListView = (StickyListHeadersListView) childView;
                }
            }
        }
    }

    /**
     * onoLayout 中没有对mListView初始化成功的调用此方法.
     */
    public void setStickyListheadersListView(StickyListHeadersListView listheadersListView){
        this.mListView = listheadersListView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (canLoad())
                    loadData();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY = (int) ev.getRawY();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoadFromUser = false;
    public void setCanload(boolean canload){
        this.canLoadFromUser = canload;
    }

    //判断是否可以加载更多
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp() && canLoadFromUser;
    }
    //判断是否到达了底部
    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }
    //判断是否是上拉操作
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    //加载操作
    private void loadData() {
        if (mOnLoadListener != null) {
            setLoading(true);
            mOnLoadListener.onLoad();
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if(mListView == null){
            return;
        }
        if (isLoading) {
            mListView.addFooterView(mListViewFooter);
        }
        else {
            mListView.removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
        }
    }

    public void setOnLoadListener(SwipeRefreshAndLoadMoreLayoutStick.OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        //滚动到底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    //加载更多的监听器
    public static interface OnLoadListener {
        public void onLoad();
    }
}

