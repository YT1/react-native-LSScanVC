package com.dowin.qrcode.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.zxing.Result;

/**
 * Created by dowin on 2016/12/16.
 */

public class BarcodeScannerActivity extends Activity implements BarcodeScannerView.OnScannerListener {
    private static final String DEFAULT_TORCH_MODE = "off";
    private static final String DEFAULT_CAMERA_TYPE = "back";

    public static final String RESULT_TEXT = "text";
    public static final String RESULT_TYPE = "type";
    public static final String RESULT_DATA = "data";

    private BarcodeScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         mScannerView = new BarcodeScannerView(this);
        mScannerView.setCameraType(DEFAULT_CAMERA_TYPE);
        mScannerView.setFlash(DEFAULT_TORCH_MODE.equals("on"));
        mScannerView.setOnScannerListener(this);
        if(mScannerView.getCancel()!=null){
            mScannerView.getCancel().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        setContentView(mScannerView);
    }

    @Override
    public void onComplete(Result finalRawResult) {
        if(finalRawResult!=null){
            Intent intent = getIntent();
            intent.putExtra(RESULT_TEXT,finalRawResult.getText());
            intent.putExtra(RESULT_TYPE,finalRawResult.getBarcodeFormat().toString());
            intent.putExtra(RESULT_DATA,finalRawResult.getRawBytes());
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }
}
