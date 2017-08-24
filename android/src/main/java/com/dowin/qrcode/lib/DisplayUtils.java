package com.dowin.qrcode.lib;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dowin.qrcode.R;
import com.facebook.react.bridge.ReactContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DisplayUtils {
    public static Point getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenResolution = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenResolution);
        } else {
            screenResolution.set(display.getWidth(), display.getHeight());
        }

        return screenResolution;
    }

    public static int getScreenOrientation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int orientation;
        if (display.getWidth() == display.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (display.getWidth() < display.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean save(Context context, Bitmap bitmap) {

        try {
            File file = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file.exists()) {
                file = context.getCacheDir();
            }
            File imageFile = new File(file, System.currentTimeMillis() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            final Bitmap head = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
            final Bitmap result = createImage(bitmap, head, "用飞马钱包扫一扫付款");
            result.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            result.recycle();

            MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), null);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFile.getAbsolutePath()));
            context.sendBroadcast(intent);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean save2(Context context, Bitmap bitmap, String value, String desc) {

        try {
            File file = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file.exists()) {
                file = context.getCacheDir();
            }
            File imageFile = new File(file, System.currentTimeMillis() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            final Bitmap image = BitmapFactory.decodeStream(context.getResources().openRawResource(TextUtils.isEmpty(value) ? R.raw.fm_2 : R.raw.fm_1));
            final Bitmap result = createImage2(bitmap, image, value, desc);
            result.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            result.recycle();

            MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), null);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFile.getAbsolutePath()));
            context.sendBroadcast(intent);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static Bitmap createImage2(Bitmap qrcode, Bitmap image, String value, String message) {

        final int width = image.getWidth();
        final int height = image.getHeight();

        final int bottom = TextUtils.isEmpty(value) ? (int) (height * 0.20) : (int) (height * 0.15);

        final int padding = 7;
        final int valuePadding = (int) (width * 0.0667);
        final int left = (int) (width * 0.15) + padding;
        final int codeWidth = width - left * 2;
        final int top = height / 3 + padding;

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        canvas.drawBitmap(image, 0, 0, null);

        RectF dst = new RectF(left, top, left + codeWidth, top + codeWidth);
        canvas.drawBitmap(qrcode, null, dst, null);


        if (!TextUtils.isEmpty(value)) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(30);
            paint.setColor(Color.BLACK);
            canvas.drawText(value, width / 2, height - bottom - valuePadding, paint);
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);
        paint.setColor(Color.WHITE);
        canvas.drawText(message, width / 2, height - bottom * 2 / 3, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return result;
    }

    private static Bitmap createImage(Bitmap qrcode, Bitmap head, String message) {

        final int headHeight = head.getHeight();

        final int top = headHeight * 24 / 63;
        final int bottom = headHeight * 32 / 63;


        final int width = (int) (head.getWidth() * 1.15);
        final int height = head.getWidth() + headHeight;
        final int padding = (width - (height - headHeight - top - bottom)) / 2;
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, width, height, paint);
            canvas.drawBitmap(head, (width - head.getWidth()) / 2, 0, null);

            paint.setColor(Color.argb(255, 209, 35, 24));
            canvas.drawRect(0, headHeight, width, height, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawLine(0, headHeight, width, headHeight, paint);

            RectF dst = new RectF(padding, headHeight + top, width - padding, height - bottom);
            canvas.drawBitmap(qrcode, null, dst, null);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(72);
            paint.setColor(Color.WHITE);
            canvas.drawText(message, width / 2, height - bottom / 2, paint);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            result = qrcode;
            e.getStackTrace();
        }
        return result;
    }

    public static void showImage(Activity activity, Bitmap bitmap) {
        final Dialog dialog = new Dialog(activity, R.style.ImageDialog);
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);
        ImageView view = new ImageView(activity);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(view, params);
        view.setImageBitmap(bitmap);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(linearLayout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lay = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View decorView = activity.getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(rect);
        lay.height = dm.heightPixels - rect.top;
        lay.width = dm.widthPixels;
        dialog.show();
    }

    public static Bitmap getLogo(String logoUrl) {
        Bitmap logo = null;
        if (!TextUtils.isEmpty(logoUrl)) {
            try {
                URL url = new URL(logoUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    logo = BitmapFactory.decodeStream(inputStream);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logo;
    }

    public static void showDialog(ReactContext context, String msg) {
        TextView textView = new TextView(context);
        textView.setText("保存二维码图片成功");
        textView.setGravity(Gravity.CENTER);
        new AlertDialog.Builder(context.getCurrentActivity()).setView(textView)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null).create().show();
    }
}