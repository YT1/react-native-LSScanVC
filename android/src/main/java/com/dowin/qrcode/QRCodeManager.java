package com.dowin.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dowin.qrcode.lib.BarcodeScannerWriter;
import com.dowin.qrcode.lib.DisplayUtils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by dowin on 2017/1/10.
 */

public class QRCodeManager extends SimpleViewManager<ImageView> {

    final String TAG = "BarQRCode";
    final String REACT_CLASS = "BarQRCode";

    private ImageView imageView;
    private BarcodeScannerWriter writer;
    private String sourcesInfo;
    private Bitmap qrcode;
    private Bitmap imageLogo;
    private Bitmap bitmap;
    private Context context;

    public QRCodeManager(ReactApplicationContext reactContext) {
        context = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ImageView createViewInstance(ThemedReactContext reactContext) {

        this.context = reactContext;
        writer = new BarcodeScannerWriter();
        imageView = new ImageView(reactContext);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WritableMap map = Arguments.createMap();
                map.putString("information", sourcesInfo);
                ((ReactContext) imageView.getContext()).getJSModule(RCTEventEmitter.class).receiveEvent(imageView.getId(), "topChange", map);

                return false;
            }
        });
        return imageView;
    }

    public void topChange(View viewId, ReadableMap map) {
        Log.i(TAG, "topChange" + map);

    }

    public void topChange(View viewId, WritableArray touches, WritableArray changeIndices) {
        Log.i(TAG, "topChange" + touches + changeIndices);
    }

    @ReactProp(name = "sourcesInfo")
    public void setSourcesInfo(ImageView view, final String sourcesInfo) {

        this.sourcesInfo = sourcesInfo;
        Log.i(TAG, "setSourcesInfo:" + sourcesInfo);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                qrcode = writer.encodeQRCodeBitmap(sourcesInfo, -1, -1);
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                setImage(qrcode, imageLogo);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @ReactProp(name = "imageUrl")
    public void setImageUrl(ImageView view, final String imageUrl) {

        Log.i(TAG, "setImageUrl:" + imageUrl);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                imageLogo = DisplayUtils.getLogo(imageUrl);
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                setImage(qrcode, imageLogo);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void setImage(Bitmap qrcode, Bitmap imageLogo) {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        bitmap = writer.merageQrcode(qrcode, imageLogo);
        imageView.setImageBitmap(bitmap);
    }

    @ReactMethod
    public void save(Promise promise) {
        Log.i(TAG, "save");

        if (bitmap != null) {
            Log.i(TAG, "bitmap");
            DisplayUtils.save(context, bitmap);

            promise.resolve("200");
        } else {
            promise.resolve("201");
        }
    }

}
