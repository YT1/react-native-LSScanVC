package com.dowin.qrcode;

import android.view.View;

import com.dowin.qrcode.lib.BarcodeScannerView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.zxing.Result;

import javax.annotation.Nullable;

public class BarcodeScannerManager extends ViewGroupManager<BarcodeScannerView> implements LifecycleEventListener,BarcodeScannerView.OnScannerListener {
    private static final String REACT_CLASS = "RNBarcodeScannerView";

    private static final String DEFAULT_TORCH_MODE = "off";
    private static final String DEFAULT_CAMERA_TYPE = "back";

    private BarcodeScannerView mScannerView;
    private boolean mScannerViewVisible;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name = "cameraType")
    public void setCameraType(BarcodeScannerView view, @Nullable String cameraType) {
      if (cameraType != null) {
          view.setCameraType(cameraType);
      }
    }

    @ReactProp(name = "torchMode")
    public void setTorchMode(BarcodeScannerView view, @Nullable String torchMode) {
        if (torchMode != null) {
            view.setFlash(torchMode.equals("on"));
        }
    }

    @Override
    public BarcodeScannerView createViewInstance(ThemedReactContext context) {
        context.addLifecycleEventListener(this);
        mScannerView = new BarcodeScannerView(context);
        mScannerView.setCameraType(DEFAULT_CAMERA_TYPE);
        mScannerView.setFlash(DEFAULT_TORCH_MODE.equals("on"));
        mScannerViewVisible = true;
        return mScannerView;
    }

    @Override
    public void onHostResume() {
        mScannerView.onResume();
    }

    @Override
    public void onHostPause() {
        mScannerView.onPause();
    }

    @Override
    public void onHostDestroy() {
        mScannerView.stopCamera();
    }

    @Override
    public void addView(BarcodeScannerView parent, View child, int index) {
        parent.addView(child, index + 1);   // index 0 for camera preview reserved
    }

    @Override
    public void onComplete(Result finalRawResult) {
        WritableMap event = Arguments.createMap();
        event.putString("data", finalRawResult.getText());
        event.putString("type", finalRawResult.getBarcodeFormat().toString());
        ReactContext reactContext = (ReactContext) mScannerView.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                mScannerView.getId(),
                "topChange",
                event);
    }
}
