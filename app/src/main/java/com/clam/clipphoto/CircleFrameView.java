package com.clam.clipphoto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by clam314 on 2017/4/20
 */

public class CircleFrameView extends View implements ClipFrameView {
    private float frameWidth;
    private float frameHeight;
    private float frameScale; //width/height
    private float frameStrokeWidth;
    private float mWidth;
    private float mHeight;

    private Paint paint;
    private Path globalPath;
    private Path framePath;
    private PorterDuffXfermode xfermode;

    public CircleFrameView(Context context) {
        this(context,null);
    }

    public CircleFrameView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        globalPath = new Path();
        framePath = new Path();
        frameStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getContext().getResources().getDisplayMetrics());
        frameScale = 2f/3;

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        frameWidth = mWidth/5*3;
        frameHeight = frameWidth;
        globalPath.addRect(-w/2,-h/2,w/2,h/2, Path.Direction.CW);//顺时针
        framePath.addCircle(0,0,frameHeight/2, Path.Direction.CW);
//        framePath.addRect(-frameWidth/2,-frameHeight/2,frameWidth/2,frameHeight/2, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制整个画布的阴影
        canvas.translate(mWidth/2,mHeight/2);
        paint.setColor(Color.parseColor("#333333"));
        paint.setAlpha(255/3*2);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(globalPath,paint);
        //擦除框内的阴影
        paint.setXfermode(xfermode);
        canvas.drawPath(framePath,paint);
        paint.setXfermode(null);
        //描出边框
        paint.setColor(Color.YELLOW);
        paint.setAlpha(255);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(frameStrokeWidth);
        canvas.drawPath(framePath,paint);
    }


    @Override
    public float getFrameScale() {
        return frameScale;
    }

    @Override
    public float getFrameWidth() {
        return frameWidth;
    }

    @Override
    public float getFrameHeight() {
        return frameHeight;
    }

    @Override
    public PointF getFramePosition() {
        float top = (mHeight - frameHeight)/2;
        float left = (mWidth - frameWidth)/2;
        return new PointF(left,top);
    }
}
