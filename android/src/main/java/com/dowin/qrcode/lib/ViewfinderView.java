package com.dowin.qrcode.lib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dowin on 2016/12/16.
 */

public class ViewfinderView extends View {
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    private Paint paint;
    private Bitmap resultBitmap;
    private int maskColor;
    private int resultColor;
    private int laserColor;
    private int resultPointColor;
    private int scannerAlpha;
    private float scannerBorderScale;
    private int scannerBorderWidth;
    private int scannerBorderLength;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    private Rect frame = new Rect();//中间的取景框
    private Rect previewFrame = new Rect();//zxing官方BarcodeScanner.apk解码成功时左上角存放截图的矩形框

    private Paint textPaint;
    // This constructor is used when the class is built from an XML resource.

    public ViewfinderView(Context context) {
        super(context);
        init();
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG); //开启反锯齿
        Resources resources = getResources();
//        viewFinderBackgroundColor='rgba(0, 0, 0, 0.3)'
//        viewFinderBorderColor="rgba(255, 0, 0, 0.3)"
        maskColor = Color.argb(76, 0, 0, 0);//resources.getColor(R.color.viewfinder_mask);//遮盖层颜色
        resultColor = Color.argb(76, 0, 0, 0);//resources.getColor(R.color.result_view);
        laserColor = Color.argb(76, 255, 0, 0);//resources.getColor(R.color.viewfinder_laser);
        resultPointColor = Color.argb(76, 255, 0, 0);//resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        scannerBorderScale = 0.55F;
        scannerBorderWidth = (int) (scale * 2 + 0.5F);
        scannerBorderLength = (int) (scale * 20 + 0.5F);
        possibleResultPoints = new ArrayList<ResultPoint>(5);
        lastPossibleResultPoints = null;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(18);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int width = right - left;
        int height = bottom - top;

        int centerX = width >> 1;
        int centerY = height >> 1;
        final int rectWidth = (int) (Math.min(width, height) * scannerBorderScale);

        final int rLeft = centerX - rectWidth / 2;
        final int rTop = centerY - rectWidth / 2;
        final int rRight = centerX + rectWidth / 2;
        final int rBottom = centerY + rectWidth / 2;
        frame.set(rLeft, rTop, rRight, rBottom);
        previewFrame.set(left, top, right, bottom);
    }

    int scannerY = 0;

    @Override
    public void onDraw(Canvas canvas) {
//        if (cameraManager == null) {
//            return; // not ready yet, early draw before done configuring
//        }

        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth(); //手机屏幕宽度
        int height = canvas.getHeight();//屏幕高度

        //取景画面中一共分为两块：外边半透明的一片(阴影部分)，中间全透明的一片。外面半透明的画面是由四个矩形组成(扫描框的  上面到屏幕上面，扫描框的下面到屏幕下面,扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边)
        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, frame.top, paint);//上方矩形
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);//左边
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);//右边
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);//下方

        paint.setColor(laserColor);
        paint.setStrokeWidth(scannerBorderWidth);
        paint.setStyle(Paint.Style.STROKE);
        drawRectScanner(canvas, paint, frame, scannerBorderLength);

//        canvas.drawText(desc, 0, desc.length(), (frame.left + frame.right) / 2, frame.top / 2, textPaint);
        scannerY += 10;
        if (scannerY < frame.top || scannerY >= frame.bottom) {
            scannerY = frame.top;
        }
//        canvas.drawLine(frame.left + 5, scannerY, frame.right - 5, scannerY, paint);

        Paint paintLine = new Paint();

        int[] colorArr = {Color.argb(0, 255, 0, 0), Color.argb(200, 255, 0, 0)};
        LinearGradient shader = new LinearGradient(frame.left, 0, frame.left + frame.width() / 2, 0, colorArr, null, Shader.TileMode.MIRROR);
        paintLine.setShader(shader);
        RectF rect = new RectF(frame.left, scannerY, frame.right, scannerY + 3);
        canvas.drawRect(rect, paintLine);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;


//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                synchronized (currentLast) {
                    float radius = POINT_SIZE / 2.0f;
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                radius, paint);
                    }
                }
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    public Rect getFrame() {
        return frame;
    }

    private void drawRectScanner(Canvas canvas, Paint paint, Rect frame, int borderLength) {

        final float topY = frame.top + borderLength;
        final float bottomY = frame.bottom - borderLength;
        final float leftX = frame.left + borderLength;
        final float rightX = frame.right - borderLength;
        Path path = new Path();
        path.moveTo(frame.left, topY);
        path.lineTo(frame.left, frame.top);
        path.lineTo(leftX, frame.top);
//        canvas.drawLine(frame.left + 2, topY, frame.left + 2, frame.top, paint);

//        path = new Path();
        path.moveTo(frame.right, topY);
        path.lineTo(frame.right, frame.top);
        path.lineTo(rightX, frame.top);

        path.moveTo(frame.left, bottomY);
        path.lineTo(frame.left, frame.bottom);
        path.lineTo(leftX, frame.bottom);

        path.moveTo(frame.right, bottomY);
        path.lineTo(frame.right, frame.bottom);
        path.lineTo(rightX, frame.bottom);
        canvas.drawPath(path, paint);
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }
}
