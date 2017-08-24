package com.dowin.qrcode.lib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Canvas.ALL_SAVE_FLAG;

/**
 * Created by dowin on 2017/1/10.
 */

public class BarcodeScannerWriter {

    private Writer multiFormatWriter;
    private int COLOR_WHITE = 0xffffffff; //
    private int COLOR_GRAY = 0xffeae4e4; //

    private int defaultWidth = 500; // 二维码宽度
    private int defaultHeight = 500; // 二维码高度

    private int defaultBarWidth = 500; // 条码宽度
    private int defaultBarHeight = defaultBarWidth / 5 - 15; // 条码高度
    private Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();

    public BarcodeScannerWriter() {
        multiFormatWriter = new MultiFormatWriter();


        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); //编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //容错率 二维码容错率，分四个等级：H、L 、M、 Q
        hints.put(EncodeHintType.MARGIN, 1);  //二维码边框宽度，这里文档说设置0-4

    }

    /**
     * 生成二维码内容<br>
     *
     * @param matrix
     * @return
     */
    public Bitmap parseBitMatrix(BitMatrix matrix, int background) {
        final int QR_WIDTH = matrix.getWidth();
        final int QR_HEIGHT = matrix.getHeight();
        int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
        //this we using qrcode algorithm
        for (int y = 0; y < QR_HEIGHT; y++) {
            for (int x = 0; x < QR_WIDTH; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * QR_WIDTH + x] = 0xff000000;
                } else {
                    pixels[y * QR_WIDTH + x] = background;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
        return bitmap;
    }

    public Bitmap merageQrcode(Bitmap qrcode, Bitmap logo) {

        if (qrcode == null) {
            return logo;
        }

        if (logo == null) {
            return qrcode;
        }
        int srcWidth = qrcode.getWidth();
        int srcHeight = qrcode.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return qrcode;
        }

        final int destLogoWidth = Math.max(logoWidth, logoHeight);
        final int logoLeft = (srcWidth - logoWidth) / 2;
        final int logoTop = (srcHeight - logoHeight) / 2;
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / destLogoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(qrcode, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(Color.WHITE);
            RectF rectF = new RectF((srcWidth - destLogoWidth) / 2, (srcHeight - destLogoWidth) / 2,
                    (srcWidth + destLogoWidth) / 2, (srcHeight + destLogoWidth) / 2);
            int count = canvas.saveLayer(rectF, paint, ALL_SAVE_FLAG);
            canvas.drawRoundRect(rectF, destLogoWidth / 10, destLogoWidth / 10, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(logo, logoLeft, logoTop, paint);
            canvas.restoreToCount(count);
            canvas.save(ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /**
     * 生成QR_CODE二维码PNG图片，返回文件路径
     *
     * @param content
     * @param w
     * @param h
     * @return
     */
    public Bitmap encodeQRCodeBitmap(String content, int w, int h) {
        final int width = w > 0 ? w : defaultWidth;
        final int height = h > 0 ? h : defaultHeight;
        BitMatrix bitMatrix = null;
        if (TextUtils.isEmpty(content)) {
            content = "null";
        }
        try {
            hints.put(EncodeHintType.MARGIN, 1);
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            Bitmap bitmap = parseBitMatrix(bitMatrix, COLOR_WHITE);
            bitMatrix.clear();
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 生成QR_CODE二维码PNG图片，返回文件路径
     *
     * @param content
     * @param filePath
     * @param w
     * @param h
     * @return
     */
    public String encodeQRCodeToFilePath(String content, Bitmap logo, String filePath, int w, int h) {
        try {

            Bitmap bitmap = encodeQRCodeBitmap(content, w, h);
            bitmap = merageQrcode(bitmap, logo);
            if (bitmap != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                bitmap.recycle();
                return filePath;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encodeQRCodeWidthBarToFilePath(String content, Bitmap logo, String filePath, int w, int h) {
        try {

            Bitmap qrcode = encodeQRCodeBitmap(content, w, h);
            Bitmap qcodeAndLogo = merageQrcode(qrcode, logo);
            Bitmap bar = createCodeBar(content, w, h,true);
            Bitmap result = merageCodeBarAndQrcode(qcodeAndLogo, bar);
            qcodeAndLogo.recycle();
            bar.recycle();
            if (result != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                result.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                result.recycle();
                return filePath;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap merageCodeBarAndQrcode(Bitmap codeBar, Bitmap qrcode) {

        if (codeBar == null) {
            return qrcode;
        }

        if (qrcode == null) {
            return codeBar;
        }
        int srcWidth = qrcode.getWidth();
        int srcHeight = qrcode.getHeight();

        int barWidth = codeBar.getWidth();
        int barHeight = codeBar.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (barHeight == 0 || barHeight == 0) {
            return qrcode;
        }

        final int destWidth = Math.max(srcWidth, barWidth);

        final int destHeight = srcHeight + barHeight;

        final int marginTop = destHeight / 10;

        final int codeLeft = (srcWidth - destWidth) / 2;

        final int barLeft = (barWidth - destWidth) / 2;


        Bitmap bitmap = Bitmap.createBitmap(destWidth, destHeight + marginTop, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(codeBar, codeLeft, 0, null);
            canvas.drawBitmap(qrcode, barLeft, marginTop + barHeight, null);

            canvas.save(ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    public Bitmap createCodeBar(String content, int w, int h, boolean ispadding) {
        final int width = w > 0 ? w : defaultBarWidth;
        final int height = h > 0 ? h : defaultBarHeight;
        BitMatrix bitMatrix = null;
        if (TextUtils.isEmpty(content)) {
            content = "null";
        }
        try {
            final int padding = ispadding ? height / 7 : 0;
            hints.put(EncodeHintType.MARGIN, 1);
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.CODE_128, width - padding * 2, height - padding * 2, hints);
            Bitmap bitmap = parseBitMatrix(bitMatrix, COLOR_WHITE);
            bitMatrix.clear();
            if (ispadding) {
                Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap, padding, padding, null);

                canvas.save(ALL_SAVE_FLAG);
                canvas.restore();
                bitmap.recycle();
                return result;
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
