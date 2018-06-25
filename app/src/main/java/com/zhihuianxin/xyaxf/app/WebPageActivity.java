package com.zhihuianxin.xyaxf.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.view.activity.TakePictureActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by John on 2015/2/12.
 */
public class WebPageActivity extends BaseRealmActionBarActivity implements IWXAPIEventHandler {
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_REQUEST_CODE = "request_code";

    public WebView mWebView;

    private ProgressBar progressBar;
    private int mRequestCode;

    private String mURL;
    private String mTITLE;
    private WebChromeClient mWebChromeClient;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    public static final int REQUEST_SELECT_FILE = 4001;

    private WechatShareManager mShareManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mURL = getIntent().getStringExtra(EXTRA_URL);
        mTITLE = getIntent().getStringExtra(EXTRA_TITLE);
        mRequestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, 0);

        mWebView = (WebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " AXinFu.App.V3");
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.addJavascriptInterface(new axinfuApp(), "axinfuApp");

        mShareManager = new WechatShareManager(this);
        mShareManager.mWXApi.handleIntent(getIntent(), this);


        initAPPInfo();

        if (mTITLE != null)
            setTitle(mTITLE);

        mWebChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    } else {
                        progressBar.setProgress(newProgress);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), TakePictureActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(TakePictureActivity.EXTRA_FROM_SIMPLE, "simple");
                        bundle.putInt(TakePictureActivity.EXTRA_MAX_MUTI_SELECT, 1);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_SELECT_FILE);
//                        getActivity().startActivityForResult(new Intent(getActivity(), TakePictureActivity.class),REQUEST_SELECT_FILE);
                    }
                }
                if (mUploadCallbackAboveL == null) {
                    mUploadCallbackAboveL = filePathCallback;
                }
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                getActivity().startActivityForResult(new Intent(getActivity(), TakePictureActivity.class), REQUEST_SELECT_FILE);
            }

        };

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.endsWith("apk")) {
                    try {
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(viewIntent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (url.startsWith("weixin://") || url.startsWith("alipays://") || url.startsWith("mailto://") || url.startsWith("tel://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:getAppInfo('" + new Gson().toJson(initAPPInfo()) + "')");
                super.onPageFinished(view, url);
            }

        });

        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.loadUrl(mURL);

    }

    private AppInfo initAPPInfo() {

        String pkName = this.getPackageName();
        try {
            String versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            AppInfo appInfo = new AppInfo();
            appInfo.platform = "Android";
            appInfo.packageName = pkName;
            appInfo.verson = versionName;
            return appInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    class AppInfo {
        String platform; // Android, iOS
        String verson; // v1.0.0
        String packageName; //
    }

    private void loadSuccessCallBack(Uri result) {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(new Uri[]{result});
            mUploadCallbackAboveL = null;
        }
    }

    private void loadFailerCallBack() {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        } else if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_SELECT_FILE) {
            if (intent != null && !Util.isEmpty(intent.getStringExtra(TakePictureActivity.EXTRA_OUTPUT_PATH))) {
                String mAvatarPath = intent.getStringExtra(TakePictureActivity.EXTRA_OUTPUT_PATH);
                Uri result = getImageContentUri(this, new File(mAvatarPath));
                loadSuccessCallBack(result);
            } else if (intent != null && !Util.isEmpty(intent.getExtras().getSerializable(TakePictureActivity.EXTRA_OUTPUT_PATH_MUTI).toString())) {
                ArrayList<String> mutiPath = (ArrayList<String>) intent.getExtras().getSerializable(TakePictureActivity.EXTRA_OUTPUT_PATH_MUTI);
                Uri result = getImageContentUri(this, new File(mutiPath.get(0)));
                loadSuccessCallBack(result);
            } else {
                loadFailerCallBack();
            }
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.web_view;
    }

    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mShareManager.mWXApi.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(WebPageActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(WebPageActivity.this, "取消分享", Toast.LENGTH_LONG).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(WebPageActivity.this, "分享失败", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(WebPageActivity.this, "分享失败", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public class axinfuApp {

        @JavascriptInterface
        public void accessUrl(String url) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
        }

        @JavascriptInterface
        public void wxShare(String title, String content, String url, String image) {
            Log.d("TAG", url);
            if (!isWebchatAvaliable()) {
                Toast.makeText(WebPageActivity.this, "请先安装微信", Toast.LENGTH_LONG).show();
                return;
            }

            WechatShareManager.ShareContentWebpage shareContentWebpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag(title, content, url, image);
            mShareManager.shareByWebchat(shareContentWebpage, WechatShareManager.WECHAT_SHARE_TYPE_FRENDS);

        }

        @JavascriptInterface
        public void closeView() {
            finish();
        }
    }

    private boolean isWebchatAvaliable() {
        //检测手机上是否安装了微信
        try {
            getPackageManager().getPackageInfo("com.tencent.mm", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    class WechatShareManager {

        public static final int WECHAT_SHARE_WAY_WEBPAGE = 3;  //链接
        public static final int WECHAT_SHARE_TYPE_FRENDS = SendMessageToWX.Req.WXSceneTimeline; //朋友圈

        public static final String WECHAT_APP_ID = "wx16ee1a94ef36490e";

        private ShareContent mShareContentWebpag;
        private IWXAPI mWXApi;

        private WechatShareManager(Context context) {
            initWechatShare(context);
        }

        private void initWechatShare(Context context) {
            mWXApi = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID);
            mWXApi.registerApp(WECHAT_APP_ID);
        }

        /**
         * 通过微信分享
         *
         * @param shareContent 分享的方式（文本、图片、链接）
         * @param shareType    分享的类型（朋友圈，会话）
         */
        public void shareByWebchat(ShareContent shareContent, int shareType) {
            switch (shareContent.getShareWay()) {
                case WECHAT_SHARE_WAY_WEBPAGE:
                    shareWebPage(shareContent, shareType);
                    break;
            }
        }

        private abstract class ShareContent {
            protected abstract int getShareWay();

            protected abstract String getContent();

            protected abstract String getTitle();

            protected abstract String getURL();

            protected abstract String getImage();
        }

        /**
         * 设置分享链接的内容
         *
         * @author chengcj1
         */
        public class ShareContentWebpage extends ShareContent {
            private String title;
            private String content;
            private String url;
            private String pictureResource;

            public ShareContentWebpage(String title, String content, String url, String pictureResource) {
                this.title = title;
                this.content = content;
                this.url = url;
                this.pictureResource = pictureResource;
            }

            @Override
            protected int getShareWay() {
                return WECHAT_SHARE_WAY_WEBPAGE;
            }

            @Override
            protected String getContent() {
                return content;
            }

            @Override
            protected String getTitle() {
                return title;
            }

            @Override
            protected String getURL() {
                return url;
            }

            @Override
            protected String getImage() {
                return pictureResource;
            }
        }

        /*
         * 获取网页分享对象
         */
        public ShareContent getShareContentWebpag(String title, String content, String url, String pictureResource) {
            if (mShareContentWebpag == null) {
                mShareContentWebpag = new ShareContentWebpage(title, content, url, pictureResource);
            }
            return (ShareContentWebpage) mShareContentWebpag;
        }

        /*
         * 分享链接
         */
        private void shareWebPage(ShareContent shareContent, int shareType) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = shareContent.getURL();
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = shareContent.getTitle();
            msg.description = shareContent.getContent();

            try {
                Bitmap thumb = Bitmap.createScaledBitmap(GetLocalOrNetBitmap(shareContent.getImage()), 120, 120, true);//压缩Bitmap
                msg.thumbData = com.tencent.mm.sdk.platformtools.Util.bmpToByteArray(thumb, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = shareType;
            mWXApi.sendReq(req);
        }

        private String buildTransaction(final String type) {
            return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
        }

        /**
         * 把网络资源图片转化成bitmap
         *
         * @param url 网络资源图片
         * @return Bitmap
         */
        public Bitmap GetLocalOrNetBitmap(String url) {
            Bitmap bitmap = null;
            InputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = new BufferedInputStream(new URL(url).openStream(), 1024);
                final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                out = new BufferedOutputStream(dataStream, 1024);
                copy(in, out);
                out.flush();
                byte[] data = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                data = null;
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void copy(InputStream in, OutputStream out)
                throws IOException {
            byte[] b = new byte[1024];
            int read;
            while ((read = in.read(b)) != -1) {
                out.write(b, 0, read);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
