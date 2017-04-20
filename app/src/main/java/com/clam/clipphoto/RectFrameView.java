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
 * Created by clam314 on 2017/4/19
 */

public class RectFrameView extends View implements ClipFrameView {

    protected float frameWidth;
    protected float frameHeight;
    protected float frameScale; //width/height
    protected float frameStrokeWidth;
    protected int frameStrokeColor;
    protected float mWidth;
    protected float mHeight;

    protected Paint paint;
    protected Path globalPath;
    protected Path framePath;
    protected PorterDuffXfermode xfermode;


    public RectFrameView(Context context) {
        this(context,null);
    }

    public RectFrameView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RectFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init(){
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        globalPath = new Path();
        framePath = new Path();
        frameStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getContext().getResources().getDisplayMetrics());
        frameScale = 2f/3;
        frameStrokeColor = Color.YELLOW;

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        int length = w>h?h:w;
        frameWidth = length/5*4;
        frameHeight = frameWidth/frameScale;
        globalPath.addRect(-w/2,-h/2,w/2,h/2, Path.Direction.CW);//顺时针
        framePath.addRect(-frameWidth/2,-frameHeight/2,frameWidth/2,frameHeight/2, Path.Direction.CW);
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
        paint.setColor(frameStrokeColor);
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
