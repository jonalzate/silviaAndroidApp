package com.neurala.silvia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by jalzate on 3/29/17.
 * This class is for drawing on a canvas using touch events. It extends the View class
 * and overwrites the onDraw method.
 */

public class DrawingView extends View
{
    // selection path
    private Path mPath;
    // drawing and canvas paint
    private Paint mPaint, mCanvasPaint;
    // initial color
    private int mPaintColor = 0xFF009900;
    // canvas
    private Canvas mCanvas;
    // canvas bitmap
    private Bitmap mCanvasBitmap;
    // erasing flag
    private boolean erase = false;

    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setupDrawing();
    }

    /* Sets up the drawing area for user interaction */
    private void setupDrawing()
    {
        // initialize drawing objects
        mPath = new Path();
        mPaint = new Paint();
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);

        // set initial color for paint
        mPaint.setColor(mPaintColor);

        // set path properties
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    /* This is called when the size of this view is changed. Initial values at view
       creation are 0 */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        // initialize canvas and bitmap using view size
        mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mCanvasBitmap);
    }

    /* Called when the view should render its contents. Takes in the canvas on
       which background will be drawn */
    @Override
    protected void  onDraw(Canvas canvas)
    {
        // draw view
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mPath, mPaint);
    }

    /* Touch events */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // get touch coordinates
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction())
        {
            // finger touch screen, move pointer to path
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(touchX, touchY);
                break;
//           touch point moved, mark a line along path of movement starting at touch X and Y
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(touchX, touchY);
                break;
//           touch point left screen, draw path
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                break;
            default:
                return false;
        }
//      Forces a redraw of the view
        invalidate();
        return true;
    }

//  Sets that paint color to use
    public void setColor(String newColor)
    {
        // set color
        invalidate();

        // parse and set color for drawing
        mPaintColor = Color.parseColor(newColor);
        mPaint.setColor(mPaintColor);

    }

//  erases canvas currently. todo: add erase mode with finger/brush
    public void eraseCanvas(boolean isErase) {
        // update erase flag

        erase = isErase;

        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

}
