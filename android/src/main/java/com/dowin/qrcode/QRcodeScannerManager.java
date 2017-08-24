package com.dowin.qrcode;

import android.util.Log;

import com.dowin.qrcode.lib.BarcodeScannerView;
import com.dowin.qrcode.lib.QRCodeScannerView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.zxing.Result;

import javax.annotation.Nullable;

/**
 * Created by dowin on 2017/4/6.
 */

public class QRcodeScannerManager extends SimpleViewManager<QRCodeScannerView> implements LifecycleEventListener, QRCodeScannerView.OnScannerListener {

    final String TAG = "LSScan";
    final String REACT_CLASS = "LSScan";
    private ReactContext context;

    private static final String DEFAULT_TORCH_MODE = "off";
    private static final String DEFAULT_CAMERA_TYPE = "back";

    private QRCodeScannerView qrCodeScannerView;

    public QRcodeScannerManager(ReactApplicationContext reactContext) {
        context = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected QRCodeScannerView createViewInstance(ThemedReactContext reactContext) {
        context = reactContext;
        context.addLifecycleEventListener(this);
        qrCodeScannerView = new QRCodeScannerView(context);
        qrCodeScannerView.setCameraType(DEFAULT_CAMERA_TYPE);
        qrCodeScannerView.setFlash(DEFAULT_TORCH_MODE.equals("on"));
        qrCodeScannerView.setOnScannerListener(this);
        return qrCodeScannerView;
    }


    @ReactProp(name = "descText")
    public void setDescText(QRCodeScannerView view, @Nullable String descText) {
        Log.i(TAG, "setTorchMode:" + descText);
        if (descText != null) {
            view.setDescText(descText);
        }
    }

    @ReactProp(name = "cameraType")
    public void setCameraType(BarcodeScannerView view, @Nullable String cameraType) {
        Log.i(TAG, "setTorchMode:" + cameraType);
        if (cameraType != null) {
            view.setCameraType(cameraType);
        }
    }

    @ReactProp(name = "torchMode")
    public void setTorchMode(QRCodeScannerView view, @Nullable String torchMode) {
        Log.i(TAG, "setTorchMode:" + torchMode);
        if (torchMode != null) {
            view.setFlash(torchMode.equals("on"));
        }
    }

    @Override
    public void onComplete(Result finalRawResult) {
        //Log.i(TAG, "onComplete");
        if (finalRawResult != null) {
            //Log.i(TAG, "onComplete:" + finalRawResult.getText());
            WritableMap event = Arguments.createMap();
            event.putString("information", finalRawResult.getText());
            event.putString("type", finalRawResult.getBarcodeFormat().toString());
            ReactContext reactContext = (ReactContext) qrCodeScannerView.getContext();
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                    qrCodeScannerView.getId(),
                    "topChange",
                    event);
        }
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        qrCodeScannerView.stopCamera();
    }
}
