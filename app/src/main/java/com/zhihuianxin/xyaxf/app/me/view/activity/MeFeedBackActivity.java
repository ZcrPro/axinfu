package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.axinfu.basetools.base.BaseActionBarActivity;
import com.axinfu.modellib.thrift.app.Appendix;
import com.axinfu.modellib.thrift.base.Feedback;
import com.axinfu.modellib.thrift.resource.UploadFileAccess;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.api.QiNiuStorateAPI;
import com.zhihuianxin.xyaxf.app.me.contract.IMeFeedBackContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeFeedBackPresenter;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.RealmList;

/**
 * Created by Vincent on 2016/11/10.
 */

public class MeFeedBackActivity extends BaseActionBarActivity implements IMeFeedBackContract.IMeFeedBackView {
    public static final String EXTRA_FEEDBACK_CONTENT = "feedback_content";
    private static final String TAG = "MeFeedBackActivity";
    public static final int REQUEST_FEEDBACK_INFO_TAKE_PIC = 4003;
    public static final int AVATAR_WIDTH = 120;
    public static final int AVATAR_HEIGHT = 120;

    @InjectView(R.id.add_photo)
    ImageView mAddImg;
    @InjectView(R.id.feedback_value)
    EditText mContentText;
    @InjectView(R.id.next)
    Button mFeedBackBtn;
    @InjectView(R.id.photo_list)
    LinearLayout mListPictureView;

    private IMeFeedBackContract.IMeFeedBackPresenter mPresenter;
    private int mAbbreviationSize = 0;
    private DisplayMetrics metrics;
    private PictureListController pictureListController;
    private boolean isEnable = true;
    public static final String isEnableTAG = "isEnableTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new MeFeedBackPresenter(this, this);
        mContentText.addTextChangedListener(watcher);
        mContentText.setText(getFeedBackContent());//getFeedBackContent()
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mAbbreviationSize = getImgWidth();
        pictureListController = new PictureListController(mListPictureView);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            boolean enable = bundle.getBoolean(isEnableTAG);
            if (enable){
                mContentText.setEnabled(false);
            }else {
                mContentText.setEnabled(true);
            }
        }
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

    private int getImgWidth() {
        return (int) (((metrics.widthPixels - 15 * 2 * metrics.density) / 4) - 5 * 2 * metrics.density);
    }

    String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字,标点
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5,.~!@#$%^&*()\\[\\]{};:\'\'\"\"/<>，。～！#￥%……&×（）【】『』；：‘’“”《》/_+=——、|\\\\`·-——？?]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        return m.replaceAll("").trim();
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
//            String editable = s.toString();
//            String str =  stringFilter(s.toString());
//            if(!editable.equals(str)){
//                mContentText.setText(str);
//                mContentText.setSelection(str.length());
//            }

            if (!Util.isEmpty(s.toString())) {
                mFeedBackBtn.setEnabled(true);
            } else {
                mFeedBackBtn.setEnabled(false);
            }
        }
    };


    @OnClick(R.id.add_photo)
    public void onBtnTakePicture() {
        Intent intent = new Intent(getActivity(), TakePictureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(TakePictureActivity.EXTRA_FROM_SIMPLE, "simple");
        bundle.putInt(TakePictureActivity.EXTRA_MAX_MUTI_SELECT, 4 - pictureListController.getPictureSize());
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_FEEDBACK_INFO_TAKE_PIC);
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
                Log.w(TAG, "can not load image: ", (Exception) data.getSerializableExtra(CropImageActivity.EXTRA_ERROR));
            }
        }
    }

    class PictureListController {
        private LinearLayout layout;

        public PictureListController(LinearLayout linearLayout) {
            layout = linearLayout;
        }

        View getItemView(final String url) {
            View view = LayoutInflater.from(MeFeedBackActivity.this).inflate(R.layout.me_feedback_picture_item, null);
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
                mAddImg.setVisibility(View.VISIBLE);
            } else {
                mAddImg.setVisibility(View.GONE);
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

    @OnClick(R.id.next)
    public void onBtnNext() {
        getQiNiuAccess();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_service_feedback;
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

    @Override
    public void feedBackSuccess() {
        Toast.makeText(this, "感谢您的反馈,我们会尽快给您答复！", Toast.LENGTH_LONG).show();
        goBack();
    }

    private void goBack() {
        setResult(RESULT_OK);
        finish();
    }

    private void getQiNiuAccess() {
        mPresenter.getQiNiuAccess("FeedBack", "jpg", pictureListController.getPictureSize());
    }

    @Override
    public void getQiNiuAccessSuccess(RealmList<UploadFileAccess> accesses) {
        UploadToQiniuTask task = new UploadToQiniuTask(accesses, this);
        mUploadToQiniuDialog = new LoadingDialog(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mUploadToQiniuDialog.show();
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
            mPresenter.feedBack(mContentText.getText().toString().trim(), appendixArrayList);
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
}
