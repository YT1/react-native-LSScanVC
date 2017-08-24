package com.dowin.qrcode;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BarcodeScannerPackage implements ReactPackage {

    final static String TAG = BarcodeScannerPackage.class.getName();
    private QRCodeManager codeManager;
    private BarQRCodeManager barQRCodeManager;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
//        Log.i(TAG, "2createNativeModules");
        if (codeManager == null) {
            codeManager = new QRCodeManager(reactContext);
        }
        if (barQRCodeManager == null) {
            barQRCodeManager = new BarQRCodeManager(reactContext);
        }
        return Arrays.<NativeModule>asList(
                new BarcodeScannerModule(reactContext),
                barQRCodeManager,
                codeManager);
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
//        Log.i(TAG,"3createJSModules");
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
//        Log.i(TAG,"1createViewManagers");
        if (codeManager == null) {
            codeManager = new QRCodeManager(reactContext);
        }
        if (barQRCodeManager == null) {
            barQRCodeManager = new BarQRCodeManager(reactContext);
        }
        return Arrays.<ViewManager>asList(
                new BarcodeScannerManager(),
                new QRcodeScannerManager(reactContext),
                barQRCodeManager,
                codeManager
        );
    }
}
