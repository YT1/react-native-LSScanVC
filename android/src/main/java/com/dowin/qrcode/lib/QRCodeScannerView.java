package com.dowin.qrcode.lib;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dowin.qrcode.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by dowin on 2017/4/6.
 */

public class QRCodeScannerView extends FrameLayout implements Camera.PreviewCallback {

    public interface OnScannerListener {
        public void onComplete(Result finalRawResult);
    }

    private CameraPreview mPreview;
    private ViewfinderView viewfinderView;
    private MultiFormatReader mMultiFormatReader;

    private static final String TAG = "QRCodeScannerView";
    private BlockingQueue<byte[]> mFrameDataQueue = new LinkedBlockingDeque<byte[]>(1);
    private DecodeThread mDecoder = null;
    private Rect rect = new Rect();
    private int width;
    private int height;
    private boolean isMaked = true;
    private OnScannerListener onScannerListener;
    private String descText = "";

    private float scale = 1.5F;
    private TextView imageView;
    private TextView desc;
    private Result finalRawResult;
    private long finalTime;

    public QRCodeScannerView(Context context) {
        super(context);

        scale = getContext().getResources().getDisplayMetrics().density;
        mPreview = new CameraPreview(context, this);
        viewfinderView = new ViewfinderView(context);
        mMultiFormatReader = new MultiFormatReader();
        this.addView(mPreview);
        this.addView(viewfinderView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        params.topMargin = (int) (scale * 100 + 0.5F);
        params.leftMargin = (int) (scale * 75 + 0.5F);
        params.rightMargin = (int) (scale * 75 + 0.5F);
        desc = new TextView(context);
        desc.setGravity(Gravity.CENTER_HORIZONTAL);
        desc.setText(descText);
        desc.setTextColor(Color.WHITE);
        this.addView(desc, params);

        params = new FrameLayout.LayoutParams((int) (scale * 30 + 0.5F), (int) (scale * 30 + 0.5F), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        imageView = new TextView(context);
        imageView.setBackgroundResource(R.drawable.scan);
        imageView.setFocusable(true);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !v.isSelected();
                imageView.setSelected(selected);
                imageView.setBackgroundResource(selected ? R.drawable.scan_open : R.drawable.scan_close);
                mPreview.setFlash(selected);
            }
        });

        params.bottomMargin = (int) (scale * 135 + 0.5F);
        this.addView(imageView, params);


        mDecoder = new DecodeThread();
        mDecoder.start();
    }

    public void setDescText(String descText) {
        this.descText = descText;
        desc.setText(descText);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        rect.set(viewfinderView.getFrame());
//        Log.i(TAG, rect.left + "-" + rect.top + "-" + rect.right + "-" + rect.bottom);
//        Log.i(TAG, left + "-" + top + "-" + right + "-" + bottom);
        desc.layout(rect.left, rect.top / 2, rect.right, rect.top);

        int imageViewLeft = (rect.left + rect.right) / 2 - imageView.getWidth() / 2;
        int viewTop = (getHeight() + rect.bottom) / 2;
        int imageViewTop = viewTop - imageView.getHeight() / 2;

        imageView.layout(imageViewLeft, imageViewTop, imageViewLeft + imageView.getWidth(), imageViewTop + imageView.getHeight());


    }

    public void onResume() {
        mPreview.startCamera(); // workaround for reload js
        // mPreview.onResume();
    }

    public void onPause() {
        mPreview.stopCamera();  // workaround for reload js
        // mPreview.onPause();
    }

    public void setCameraType(String cameraType) {
        mPreview.setCameraType(cameraType);
    }

    public void setFlash(boolean flag) {
        mPreview.setFlash(flag);
    }

    public void stopCamera() {
        mPreview.stopCamera();
        isMaked = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        width = size.width;
        height = size.height;
        mFrameDataQueue.offer(data);
    }

    public void makeData(byte[] data) throws Exception {

        boolean reverseHorizontal = false;
        int distWith = width;
        int distHeight = height;
        if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
//            byte[] rotatedData = new byte[data.length];
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++)
//                    rotatedData[x * height + height - y - 1] = data[x + y * width];
//            }
//            data = rotatedData;
//
//            int tmp = width;
//            width = height;
//            height = tmp;
            reverseHorizontal = true;
            distWith = height;
            distHeight = width;
        }
        rect.set(viewfinderView.getFrame());
        float scaleX = distWith * 1.0F / mPreview.getWidth();
        float scaleY = distHeight * 1.0F / mPreview.getHeight();

        final int rectLeft = (int) (rect.left * scaleX);
        final int rectTop = (int) (rect.top * scaleY);
        final int rectWidth = (int) (rect.width() * scaleX);
        final int rectHeight = (int) (rect.height() * scaleY);
//        Log.i(TAG, "[make]=U=" + rect.left + "-" + rect.top + "-" + rect.width() + "-" + rect.height() + "-" + mPreview.getWidth() + "-" + mPreview.getHeight() + "-" + scaleX + "-" + scaleY);
//        Log.i(TAG, "[make]=I=" + rectLeft + "-" + rectTop + "-" + rectWidth + "-" + rectHeight + "-" + width + "-" + height);
        Result rawResult = null;

        PlanarYUVLuminanceSource source;
        if (reverseHorizontal) {
            source = new PlanarYUVLuminanceSource(data, width, height, rectTop, rectLeft, rectHeight, rectWidth, reverseHorizontal);
        } else {
            source = new PlanarYUVLuminanceSource(data, width, height, rectLeft, rectTop, rectWidth, rectHeight, reverseHorizontal);
        }
//        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, reverseHorizontal);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = mMultiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } catch (NullPointerException npe) {
                // This is terrible
            } catch (ArrayIndexOutOfBoundsException aoe) {

            } finally {
                mMultiFormatReader.reset();
            }
        }

        if (rawResult != null) {

            if (finalRawResult != null && rawResult.getText() != null
                    && rawResult.getText().equals(finalRawResult.getText())) {
                //Log.i(TAG, rawResult.getText());
                return;
            }
            finalRawResult = rawResult;
//            isMaked = false;
            post(new Runnable() {
                @Override
                public void run() {
                    onComplete(finalRawResult);
                }
            });
        }

    }

    boolean checkReturn(Result curResult) {
        if (finalRawResult == null) {
            return false;
        }
        final long currTime = System.currentTimeMillis();
        Log.i(TAG, "time:" + finalTime + "--" + currTime);
        if (currTime - finalTime > 1000 * 60) {
            finalTime = System.currentTimeMillis();
            return false;
        } else {

            if (finalRawResult.getText().equals(curResult.getText())) {
                return true;
            }
            return false;
        }
    }

    public void setOnScannerListener(OnScannerListener onScannerListener) {
        this.onScannerListener = onScannerListener;
    }

    public void onComplete(Result finalRawResult) {
        //Log.i(TAG, finalRawResult.getText());

        if (onScannerListener != null) {
            onScannerListener.onComplete(finalRawResult);
        }

    }

    private class DecodeThread extends Thread {
        boolean mHasSuccess = false;

        @Override
        public void run() {
            byte[] imgData = null;
            try {
                while (isMaked) {
                    imgData = mFrameDataQueue.take();
//                    Log.i(TAG, "[make]" + mFrameDataQueue.size());
                    if (imgData != null) {
                        makeData(imgData);
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
                Log.e(TAG, e.toString(), e);
            }
        }
    }
}