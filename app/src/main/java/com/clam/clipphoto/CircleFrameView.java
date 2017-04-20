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
    private float frameWidth;//裁剪框的宽
    private float frameHeight;//裁剪框的高
    private float frameScale; //裁剪框的宽高比例，width/height
    private float frameStrokeWidth;//裁剪宽的边宽
    private float mWidth;//整个蒙版的宽
    private float mHeight;//整个蒙版的高

    private Paint paint;
    private Path globalPath;//整个蒙版的path
    private Path framePath;//裁剪框的path
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
        //设置裁剪框的边框2dp
        frameStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getContext().getResources().getDisplayMetrics());
        frameScale = 1f;
        //关闭硬件加速，不然部分机型path的绘制会无效
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        int length = w>h ? h : w;//选择宽高中最小的为标准
        frameWidth = length/5*4;//获取4/5的高度
        frameHeight = frameWidth/frameScale;//圆形，故框高一致
        //view的中心为原点，根据view的大小添加整个蒙版的路径
        globalPath.addRect(-w/2,-h/2,w/2,h/2, Path.Direction.CW);
        //view的中心为原点，根据框高添加一个圆形的路径
        framePath.addCircle(0,0,frameHeight/2, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制整个画布的阴影
        canvas.translate(mWidth/2,mHeight/2);
        paint.setColor(Color.parseColor("#333333"));
        paint.setAlpha(255/3*2);
        paint.setStyle(Paint.Style.FILL);//填充模式
        canvas.drawPath(globalPath,paint);
        //擦除框内的阴影，给画笔设置成擦除的模式
        paint.setXfermode(xfermode);
        canvas.drawPath(framePath,paint);
        paint.setXfermode(null);//清除擦除模式
        //描出边框
        paint.setColor(Color.YELLOW);
        paint.setAlpha(255);
        paint.setStyle(Paint.Style.STROKE);//边界模式
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
        //返回裁剪框左上角的坐标
        float top = (mHeight - frameHeight)/2;
        float left = (mWidth - frameWidth)/2;
        return new PointF(left,top);
    }
}
