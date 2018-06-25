package com.zhihuianxin.xyaxf.app.home.qrcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.CameraPreview;
import cn.bingoogolapple.qrcode.core.ProcessDataTask;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.core.ScanBoxView;

/**
 * Created by zcrpro on 2018/1/15.
 */

public abstract class QZview extends RelativeLayout implements Camera.PreviewCallback, ProcessDataTask.Delegate {
    private static Context context;
    protected Camera mCamera;
    protected CameraPreview mPreview;
    protected ScanBoxView mScanBoxView;
    protected QRCodeView.Delegate mDelegate;
    protected Handler mHandler;
    protected boolean mSpotAble = false;
    protected ProcessDataTask mProcessDataTask;
    private int mOrientation;

    private float oldDist = 1f;

    float x;

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    public static int currunt;


    public QZview(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        this.context=context;
    }

    public QZview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        mHandler = new Handler();
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPreview = new CameraPreview(getContext());

        mScanBoxView = new ScanBoxView(getContext());
        mScanBoxView.initCustomAttrs(context, attrs);
        mPreview.setId(cn.bingoogolapple.qrcode.core.R.id.bgaqrcode_camera_preview);
        addView(mPreview);
        LayoutParams layoutParams = new LayoutParams(context, attrs);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, mPreview.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mPreview.getId());
        addView(mScanBoxView, layoutParams);

        mOrientation = BGAQRCodeUtil.getOrientation(context);

    }

    /**
     * 设置扫描二维码的代理
     *
     * @param delegate 扫描二维码的代理
     */
    public void setDelegate(QRCodeView.Delegate delegate) {
        mDelegate = delegate;
    }

    public ScanBoxView getScanBoxView() {
        return mScanBoxView;
    }

    /**
     * 显示扫描框
     */
    public void showScanRect() {
        if (mScanBoxView != null) {
            mScanBoxView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏扫描框
     */
    public void hiddenScanRect() {
        if (mScanBoxView != null) {
            mScanBoxView.setVisibility(View.GONE);
        }
    }

    /**
     * 打开后置摄像头开始预览，但是并未开始识别
     */
    public void startCamera() {
        startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * 打开指定摄像头开始预览，但是并未开始识别
     *
     * @param cameraFacing
     */
    public void startCamera(int cameraFacing) {
        if (mCamera != null) {
            return;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                startCameraById(cameraId);
                break;
            }
        }
    }

    private void startCameraById(int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
            mPreview.setCamera(mCamera);
        } catch (Exception e) {
            if (mDelegate != null) {
                mDelegate.onScanQRCodeOpenCameraError();
            }
        }
    }

    /**
     * 关闭摄像头预览，并且隐藏扫描框
     */
    public void stopCamera() {
        try {
            stopSpotAndHiddenRect();
            if (mCamera != null) {
                mPreview.stopCameraPreview();
                mPreview.setCamera(null);
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 延迟1.5秒后开始识别
     */
    public void startSpot() {
        startSpotDelay(1500);
    }

    /**
     * 延迟delay毫秒后开始识别
     *
     * @param delay
     */
    public void startSpotDelay(int delay) {
        mSpotAble = true;

        startCamera();
        // 开始前先移除之前的任务
        mHandler.removeCallbacks(mOneShotPreviewCallbackTask);
        mHandler.postDelayed(mOneShotPreviewCallbackTask, delay);
    }

    /**
     * 停止识别
     */
    public void stopSpot() {
        cancelProcessDataTask();

        mSpotAble = false;

        if (mCamera != null) {
            try {
                mCamera.setOneShotPreviewCallback(null);
            } catch (Exception e) {
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mOneShotPreviewCallbackTask);
        }
    }

    /**
     * 停止识别，并且隐藏扫描框
     */
    public void stopSpotAndHiddenRect() {
        stopSpot();
        hiddenScanRect();
    }

    /**
     * 显示扫描框，并且延迟1.5秒后开始识别
     */
    public void startSpotAndShowRect() {
        startSpot();
        showScanRect();
    }

    /**
     * 打开闪光灯
     */
    public void openFlashlight() {
        mPreview.openFlashlight();
    }

    /**
     * 关闭散光灯
     */
    public void closeFlashlight() {
        mPreview.closeFlashlight();
    }

    /**
     * 销毁二维码扫描控件
     */
    public void onDestroy() {
        stopCamera();
        mHandler = null;
        mDelegate = null;
        mOneShotPreviewCallbackTask = null;
    }

    /**
     * 取消数据处理任务
     */
    protected void cancelProcessDataTask() {
        if (mProcessDataTask != null) {
            mProcessDataTask.cancelTask();
            mProcessDataTask = null;
        }
    }

    /**
     * 切换成扫描条码样式
     */
    public void changeToScanBarcodeStyle() {
        if (!mScanBoxView.getIsBarcode()) {
            mScanBoxView.setIsBarcode(true);
        }
    }

    /**
     * 切换成扫描二维码样式
     */
    public void changeToScanQRCodeStyle() {
        if (mScanBoxView.getIsBarcode()) {
            mScanBoxView.setIsBarcode(false);
        }
    }

    /**
     * 当前是否为条码扫描样式
     *
     * @return
     */
    public boolean getIsScanBarcodeStyle() {
        return mScanBoxView.getIsBarcode();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        if (mSpotAble) {
            cancelProcessDataTask();
            mProcessDataTask = new ProcessDataTask(camera, data, this, mOrientation) {
                @Override
                protected void onPostExecute(String result) {
                    if (mSpotAble) {
                        if (mDelegate != null && !TextUtils.isEmpty(result)) {
                            try {
                                mDelegate.onScanQRCodeSuccess(result);
                            } catch (Exception e) {
                            }
                        } else {
                            try {
                                camera.setOneShotPreviewCallback(QZview.this);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }.perform();
        }
    }

    private Runnable mOneShotPreviewCallbackTask = new Runnable() {
        @Override
        public void run() {
            if (mCamera != null && mSpotAble) {
                try {
                    mCamera.setOneShotPreviewCallback(QZview.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public interface Delegate {
        /**
         * 处理扫描结果
         *
         * @param result
         */
        void onScanQRCodeSuccess(String result);

        /**
         * 处理打开相机出错
         */
        void onScanQRCodeOpenCameraError();
    }


    public boolean onTouchEvent(final MotionEvent event) {

        int pointerCount = event.getPointerCount();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            try {
                //当手指按下的时候
                x1 = event.getX();
                y1 = event.getY();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            if (pointerCount == 1) {

                //当手指离开的时候
                x2 = event.getX();
                y2 = event.getY();
                if (y1 - y2 > 30) {
                    handleZoom(true, mCamera);
                } else if (y2 - y1 > 30) {
                    handleZoom(false, mCamera);
                } else if (x1 - x2 > 0) {
                } else if (x2 - x1 > 0) {
                }

            } else if (pointerCount == 2) {
                try {
                    float newDist = getFingerSpacing(event);
                    if (newDist > oldDist) {
                        handleZoom(true, mCamera);
                    } else if (newDist < oldDist) {
                        handleZoom(false, mCamera);
                    }
                    oldDist = newDist;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public static void handleZoom(boolean isZoomIn, Camera camera) {
        try {
            Camera.Parameters params = camera.getParameters();
            if (params.isZoomSupported()) {
                int maxZoom = params.getMaxZoom();
                int zoom = params.getZoom();
                if (isZoomIn && zoom < maxZoom) {
                    zoom = zoom + 2;
                    params.setZoom(zoom);
                    try {
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    QRCodeActivity.vs.setProgress(zoom);
                    currunt = zoom;
                } else if (zoom > 0) {
                    zoom = zoom - 2;
                    params.setZoom(zoom);
                    camera.setParameters(params);
                    QRCodeActivity.vs.setProgress(zoom);
                    currunt = zoom;
                }
            } else {
            }
        } catch (Exception e) {
            Toast.makeText(context, "请开启相机权限", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void handleFocusMetering(MotionEvent event, Camera camera) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f, viewWidth, viewHeight);
        Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f, viewWidth, viewHeight);

        camera.cancelAutoFocus();
        Camera.Parameters params = camera.getParameters();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
        }
        if (params.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, 800));
            params.setMeteringAreas(meteringAreas);
        } else {
        }
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(params);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
            }
        });
    }


    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private static Rect calculateTapArea(float x, float y, float coefficient, int width, int height) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / width * 2000 - 1000);
        int centerY = (int) (y / height * 2000 - 1000);

        int halfAreaSize = areaSize / 2;
        RectF rectF = new RectF(clamp(centerX - halfAreaSize, -1000, 1000)
                , clamp(centerY - halfAreaSize, -1000, 1000)
                , clamp(centerX + halfAreaSize, -1000, 1000)
                , clamp(centerY + halfAreaSize, -1000, 1000));
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

}
