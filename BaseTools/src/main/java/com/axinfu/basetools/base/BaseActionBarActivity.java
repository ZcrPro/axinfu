package com.axinfu.basetools.base;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.axinfu.basetools.R;

/**
 * Created by zcrpro on 2016/10/12.
 */

public class BaseActionBarActivity extends BaseActivity {
    private View mBtnLeft;
    private View mBtnRight;
    private ImageView mIcoLeft;
    private ImageView mIcoRight;
    private TextView mTxtLeft;
    private TextView mTxtRight;

    private ViewGroup mContainerView;
    private TextView mTitle;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if(hasActionBar()){
            createContainerView();
            View contentView = createChildView(layoutResID);
            packContentView(contentView);

            super.setContentView(mContainerView);
        }
        else{
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View contentView) {
        if(hasActionBar()){
            createContainerView();
            packContentView(contentView);

            super.setContentView(mContainerView);
        }
        else{
            super.setContentView(contentView);
        }
    }

    @Override
    public void setContentView(View contentView, ViewGroup.LayoutParams params) {
        if(hasActionBar()){
            createContainerView();
            packContentView(contentView, params);

            super.setContentView(mContainerView);
        }
        else{
            super.setContentView(contentView, params);
        }
    }

    private View createChildView(int layoutResID) {
        return getLayoutInflater().inflate(layoutResID, mContainerView, false);
    }

    private void packContentView(View contentView) {
        packContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void packContentView(View contentView, ViewGroup.LayoutParams params) {
        mContainerView.addView(contentView, params);
    }

    private void createContainerView() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        View actionBar = getLayoutInflater().inflate(getActionBarID(), container, false);
        actionBar.setId(R.id.action_bar);
        container.addView(actionBar);

        mContainerView = container;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initializeCustomActionBar();
    }

    private void initializeCustomActionBar(){
        if(hasActionBar()){
            View actionBarView = findViewById(R.id.action_bar);
            mTitle = (TextView) actionBarView.findViewById(R.id.title);
            mBtnLeft = actionBarView.findViewById(R.id.btn_left);
            mBtnRight = actionBarView.findViewById(R.id.btn_right);

            mIcoLeft = (ImageView) actionBarView.findViewById(R.id.ico_left);
            mIcoRight = (ImageView) actionBarView.findViewById(R.id.ico_right);

            mTxtLeft = (TextView) actionBarView.findViewById(R.id.txt_left);
            mTxtRight = (TextView) actionBarView.findViewById(R.id.txt_right);
            mTxtRight.setTextSize(14);

            mTitle.setText(getTitle());
            mTitle.setTextSize(15);

            mBtnLeft.setVisibility(leftButtonEnabled()? View.VISIBLE: View.GONE);
            mBtnRight.setVisibility(rightButtonEnabled()? View.VISIBLE: View.GONE);

            if(leftButtonEnabled()){
                mIcoLeft.setImageResource(getLeftButtonImageId());
                mTxtLeft.setText(getLeftButtonText());
                mBtnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLeftButtonClick(v);
                    }
                });
            }

            if(rightButtonEnabled()){
                mIcoRight.setImageResource(getRightButtonImageId());
                mTxtRight.setText(getRightButtonText());
                mBtnRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightButtonClick(v);
                    }
                });
            }
        }
    }

    public int getLeftButtonImageId(){
        return android.R.color.transparent;
    }

    public String getLeftButtonText(){
        return null;
    }

    public int getRightButtonImageId(){
        return android.R.color.transparent;
    }

    public String getRightButtonText(){
        return null;
    }

    public boolean leftButtonEnabled(){
        return false;
    }

    public boolean rightButtonEnabled(){
        return false;
    }

    public void onLeftButtonClick(View view){

    }

    public void onRightButtonClick(View view){

    }


    public View getLeftButton(){
        return mBtnLeft;
    }

    public View getRightButton(){
        return mBtnRight;
    }

    public Activity getActivity(){
        return this;
    }

    public ImageView getRightIcon(){
        return mIcoRight;
    }

    public View getActionBarView(){
        return findViewById(R.id.action_bar);
    }

    public boolean hasActionBar(){
        return true;
    }

    public int getActionBarID(){
        return R.layout.action_bar;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        if (mTitle != null){
            mTitle.setText(title);
        }
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }
}
