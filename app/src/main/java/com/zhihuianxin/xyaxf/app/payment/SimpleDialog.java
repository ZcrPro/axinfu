package com.zhihuianxin.xyaxf.app.payment;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

/**
 * Created by John on 2015/2/12.
 */
public class SimpleDialog extends Dialog {
	public interface OnButtonClickListener{
		boolean onClick(View view);
	}

	TextView mTitle;
	TextView mMessage;
	LinearLayout mTitleContainer;
	FrameLayout mContentContainer;
	Button mBtnNegative;
	View mButtonSpacing1;
	Button mBtnNature;
	View mButtonSpacing2;
	Button mBtnPositive;
	ImageButton mBtnClose;
	View mButtonContainer;

	private View mContentView;
	public SimpleDialog(Context context) {
		super(context, R.style.Dialog_CustomView);
		super.setContentView(R.layout.base_dialog);

		mTitle = (TextView) findViewById(R.id.title);
		mMessage = (TextView) findViewById(R.id.message);
		mTitleContainer = (LinearLayout) findViewById(R.id.title_container);
		mContentContainer = (FrameLayout) findViewById(R.id.content_container);
		mBtnNegative = (Button) findViewById(R.id.btn_negative);
		mButtonSpacing1 = findViewById(R.id.button_spacing1);
		mBtnNature = (Button) findViewById(R.id.btn_nature);
		mButtonSpacing2 = findViewById(R.id.button_spacing2);
		mBtnPositive = (Button) findViewById(R.id.btn_positive);
		mBtnClose = (ImageButton) findViewById(R.id.btn_close);
		mButtonContainer = findViewById(R.id.button_container);

		mTitleContainer.setVisibility(View.GONE);
		mMessage.setVisibility(View.GONE);
		mButtonContainer.setVisibility(View.GONE);
		mBtnNegative.setVisibility(View.GONE);
		mBtnPositive.setVisibility(View.GONE);
		mBtnNature.setVisibility(View.GONE);
		mButtonSpacing1.setVisibility(View.GONE);
		mButtonSpacing2.setVisibility(View.GONE);

		mBtnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
	}

	public void setTitle(String title){
		mTitleContainer.setVisibility(View.VISIBLE);
		mMessage.setTextColor(getContext().getResources().getColorStateList(R.color.axf_text_content_gray));
		mTitle.setText(title);
	}

	public void setMessage(String message){
		mMessage.setVisibility(View.VISIBLE);
		mMessage.setText(message);
	}

	public void setPositiveButton(String text, final OnButtonClickListener onClickListener){
		mButtonContainer.setVisibility(View.VISIBLE);
		mBtnPositive.setVisibility(View.VISIBLE);
		mBtnPositive.setText(text);
		mBtnPositive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean handled = true;
				if(onClickListener != null){
					handled = onClickListener.onClick(v);
				}

				if(handled){
					dismiss();
				}
			}
		});
		updateButtonSpacing();
	}

	public void setNegativeButton(String text, final OnButtonClickListener onClickListener) {
		mButtonContainer.setVisibility(View.VISIBLE);
		mBtnNegative.setVisibility(View.VISIBLE);
		mBtnNegative.setText(text);
		mBtnNegative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean handled = true;
				if(onClickListener != null){
					 handled = onClickListener.onClick(v);
				}

				if(handled){
					cancel();
				}
			}
		});
		updateButtonSpacing();
	}

	public void setNatureButton(String text, final OnButtonClickListener onClickListener){
		mButtonContainer.setVisibility(View.VISIBLE);
		mBtnNature.setVisibility(View.VISIBLE);
		mBtnNature.setText(text);
		mBtnNature.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean handled = true;
				if(onClickListener != null){
					handled = onClickListener.onClick(v);
				}

				if(handled){
					dismiss();
				}
			}
		});

		updateButtonSpacing();
	}

	private void updateButtonSpacing(){
		boolean leftButtonVisible = mBtnNegative.getVisibility() == View.VISIBLE;
		boolean middleButtonVisible = mBtnNature.getVisibility() == View.VISIBLE;
		boolean rightButtonVisible = mBtnPositive.getVisibility() == View.VISIBLE;

		if(leftButtonVisible && (middleButtonVisible || rightButtonVisible)){
			mButtonSpacing1.setVisibility(View.VISIBLE);
		}

		if(middleButtonVisible && rightButtonVisible){
			mButtonSpacing2.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		mContentView = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
		mContentContainer.addView(mContentView);
	}

	@Override
	public void setContentView(View view) {
		mContentView = view;
		mContentContainer.addView(mContentView);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		mContentView = view;
		mContentContainer.addView(view, params);
	}

	public Button getPositiveButton(){
		return mBtnPositive;
	}

	public Button getNegativeButton(){
		return mBtnNegative;
	}

	public Button getNatureButton(){
		return mBtnNature;
	}
}
