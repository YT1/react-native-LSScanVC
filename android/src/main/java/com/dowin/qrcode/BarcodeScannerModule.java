package com.dowin.qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.dowin.qrcode.lib.BarcodeScannerActivity;
import com.dowin.qrcode.lib.BarcodeScannerWriter;
import com.dowin.qrcode.lib.DisplayUtils;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileNotFoundException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by dowin on 2016/12/16.
 */

public class BarcodeScannerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    public static final String RCT_CLASS = "RCTLSScan";
    public static final String TAG = "RCTLSScan";
    private Promise scannerPromise;
    private Context context;
    private Activity mActivity;
    private BarcodeScannerWriter barcodeScannerWriter;

    public BarcodeScannerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext.getBaseContext();
        reactContext.addActivityEventListener(this);
        barcodeScannerWriter = new BarcodeScannerWriter();
    }

    @Override
    public String getName() {
        return RCT_CLASS;
    }

    @ReactMethod
    public void openLsScan(Promise promise) {
        scannerPromise = promise;
        mActivity = getCurrentActivity();
        mActivity.startActivityForResult(new Intent(mActivity, BarcodeScannerActivity.class), 99);
    }

    @ReactMethod
    public void makeLsScan(ReadableMap map, Promise promise) {

        Log.i(TAG, "makeLsScan:" + map);
        String content = map.getString("content");
        String path = map.getString("path");
        String logoUrl = map.getString("imageUrl");

        File file;
        if (TextUtils.isEmpty(path)) {
            if (Environment.getExternalStorageState().equals(MEDIA_MOUNTED)) {
                file = new File(Environment.getExternalStorageDirectory(), "qrcode.png");
            } else {
                file = new File(context.getCacheDir(), "qrcode.png");
            }

        } else {
            file = new File(path);
        }

        file.deleteOnExit();

        Log.i(TAG, "file:" + file.getAbsolutePath());

        Bitmap logo = DisplayUtils.getLogo(logoUrl);
        if (logo == null) {
//            logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.scan_open);
        }
        String result = barcodeScannerWriter.encodeQRCodeToFilePath(content, logo, file.getAbsolutePath(), -1, -1);

        if (result != null) {

            String mediaPath = result;
            try {
                mediaPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), result, "title", "description");
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getParent())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            promise.resolve(mediaPath);
        } else {
            promise.reject("error", "error");
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == 99) {
            String type = data.getStringExtra(BarcodeScannerActivity.RESULT_TYPE);
            String text = data.getStringExtra(BarcodeScannerActivity.RESULT_TEXT);
            WritableMap map = Arguments.createMap();
            map.putString("type", type);
            map.putString("text", text);
            scannerPromise.resolve(text);
//            byte[] dataBytes = data.getByteArrayExtra(BarcodeScannerActivity.RESULT_DATA);
        }

    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
