package com.dowin.qrcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by dowin on 2017/3/24.
 */

public class BarQRCodeManager extends SimpleViewManager<View> {

    final String TAG = "QRCode";
    final String REACT_CLASS = "QRCode";

    private ImageView qrcodeView;
    private TextView barText;
    private ImageView barView;

    private BarcodeScannerWriter writer;
    private String cruSourcesInfo;
    private String curBarInfo;
    private Bitmap qrcode;
    private Bitmap imageLogo;
    private Bitmap bitmap;
    private ReactContext context;
    private View linearLayout;
    private View barLayout;
    private Matrix matrix = new Matrix();

    public BarQRCodeManager(ReactApplicationContext reactContext) {
        context = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected View createViewInstance(ThemedReactContext reactContext) {
        this.context = reactContext;
        writer = new BarcodeScannerWriter();
        linearLayout = LayoutInflater.from(context).inflate(R.layout.barcode_view, null);
        matrix.setRotate(90);

        barLayout = linearLayout.findViewById(R.id.bar_layout);
        qrcodeView = (ImageView) linearLayout.findViewById(R.id.qr_code);
        qrcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayUtils.showImage(context.getCurrentActivity(), ((BitmapDrawable) qrcodeView.getDrawable()).getBitmap());
            }
        });
        barView = (ImageView) linearLayout.findViewById(R.id.bar_code);
        barView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barLayout.setDrawingCacheEnabled(true);
                Bitmap temp = barLayout.getDrawingCache();
                if (temp != null) {
                    temp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
                }
                barLayout.setDrawingCacheEnabled(false);

                DisplayUtils.showImage(context.getCurrentActivity(), temp);
            }
        });
        barText = (TextView) linearLayout.findViewById(R.id.bar_text);
        qrcodeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WritableMap map = Arguments.createMap();
                map.putString("information", cruSourcesInfo);
                map.putString("barinfo", curBarInfo);
                ((ReactContext) linearLayout.getContext()).getJSModule(RCTEventEmitter.class).receiveEvent(linearLayout.getId(), "topChange", map);
                return true;
            }
        });
        return linearLayout;
    }

    public void topChange(View viewId, ReadableMap map) {
        Log.i(TAG, "topChange" + map);
    }

    public void topChange(View viewId, WritableArray touches, WritableArray changeIndices) {
        Log.i(TAG, "topChange" + touches + changeIndices);
    }

    @ReactProp(name = "sourcesInfo")
    public void setSourcesInfo(View view, final String sourcesInfo) {

        cruSourcesInfo = sourcesInfo;
        Log.i(TAG, "setSourcesInfo:" + sourcesInfo);
        Log.i(TAG, "width:" + linearLayout.getWidth() + "height:" + linearLayout.getHeight());
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                qrcode = writer.encodeQRCodeBitmap(cruSourcesInfo, -1, -1);
                return getImage(qrcode, imageLogo);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                qrcodeView.setImageBitmap(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @ReactProp(name = "paymentCode")
    public void setPaymentCode(View view, String paymentCode) {

        curBarInfo = paymentCode;
        if (TextUtils.isEmpty(paymentCode)) {
            barLayout.setVisibility(View.GONE);
            return;
        }
        Log.i(TAG, "setPaymentCode:" + paymentCode);
        Log.i(TAG, "width:" + linearLayout.getWidth() + "height:" + linearLayout.getHeight());
        barLayout.setVisibility(View.VISIBLE);
        barText.setText(paymentCode);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                Bitmap barcode = writer.createCodeBar(curBarInfo, -1, -1, false);
                return barcode;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                barView.setBackground(new BitmapDrawable(result));

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @ReactProp(name = "imageUrl")
    public void setImageUrl(View view, final String imageUrl) {

        Log.i(TAG, "setImageUrl:" + imageUrl);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                imageLogo = DisplayUtils.getLogo(imageUrl);
                return getImage(qrcode, imageLogo);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                qrcodeView.setImageBitmap(result);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void save2(Promise promise) {
        Log.i(TAG, "save");
        makeImage();
        if (bitmap != null) {
            Log.i(TAG, "bitmap");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getCurrentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                } else {
                    Toast.makeText(context, "请允许保存文件权限", Toast.LENGTH_SHORT).show();
                }
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            DisplayUtils.save(context, bitmap);
            bitmap.recycle();
            bitmap = null;
//            Toast.makeText(context, "保存二维码图片成功", Toast.LENGTH_SHORT).show();
            promise.resolve("200");
        } else {
//            Toast.makeText(context, "保存二维码图片失败", Toast.LENGTH_SHORT).show();
            promise.resolve("201");
        }
    }

    @ReactMethod
    public void save(String value, String desc, Promise promise) {
        Log.i(TAG, "save");
        makeImage();
        if (bitmap != null) {
            Log.i(TAG, "bitmap");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getCurrentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                } else {
                    Toast.makeText(context, "请允许保存文件权限", Toast.LENGTH_SHORT).show();
                }
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            DisplayUtils.save2(context, bitmap, TextUtils.isEmpty(value) ? value : String.format("￥%s", value), String.format("用飞马钱包扫一扫向(%s)转账", desc));
            bitmap.recycle();
            bitmap = null;
//            Toast.makeText(context, "保存二维码图片成功", Toast.LENGTH_SHORT).show();
            promise.resolve("200");
        } else {
//            Toast.makeText(context, "保存二维码图片失败", Toast.LENGTH_SHORT).show();
            promise.resolve("201");
        }
    }

    public void makeImage() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        linearLayout.setDrawingCacheEnabled(true);
        Bitmap temp = linearLayout.getDrawingCache();
        if (temp != null)
            bitmap = Bitmap.createBitmap(temp);
        linearLayout.setDrawingCacheEnabled(false);

    }

    public Bitmap getImage(Bitmap qrcode, Bitmap imageLogo) {
        Log.i(TAG, "getImage");
        try {
            return writer.merageQrcode(qrcode, imageLogo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
