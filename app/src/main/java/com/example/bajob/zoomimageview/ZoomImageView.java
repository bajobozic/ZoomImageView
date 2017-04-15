package com.example.bajob.zoomimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by bajob on 4/15/2017.
 */

public final class ZoomImageView extends android.support.v7.widget.AppCompatImageView {
    private Bitmap bitmap;//out bitmap that we scale
    private int startImageWidth;//width of image
    private int startImageHeight;//height of image
    private final float minZoom = 1.f;//start zoom factor
    private final float maxZoom = 3.f;//don't scale over max factor
    private float scaleFactor = 1.f;//put scale factor that we get from detector here
    private ScaleGestureDetector scaleGestureDetector;

    public ZoomImageView(Context context) {
        super(context);
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //start scaling from touch point not from uper left corner(default)
        canvas.scale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //this code works only for images with MeasureSpecMode.EXACTLY
        //its not tested for other two modes,use on yor own risk
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int scaledWidth = Math.round(startImageWidth * scaleFactor);
        int scaledHeight = Math.round(startImageHeight * scaleFactor);
        setMeasuredDimension(Math.min(width, scaledWidth), Math.min(height, scaledHeight));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private void init() {
        if (scaleGestureDetector == null) {
            scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        }
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallbig);
        startImageWidth = bitmap.getWidth();
        startImageHeight = bitmap.getHeight();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(minZoom, Math.min(scaleFactor, maxZoom));
            //force call onMeasure() and  onDraw() because scaleFactor has changed
            invalidate();
            requestLayout();
            return super.onScale(detector);
        }
    }
}
