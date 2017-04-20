package com.clam.clipphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by clam314 on 2017/4/20
 */

public class ImageTouchView extends ImageView {
    private float mWidth;
    private float mHeight;

    private PointF startPoint = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix currentMatrix = new Matrix();

    private int mode = 0;//用于标记模式
    private static final int DRAG = 1;//拖动
    private static final int ZOOM = 2;//放大
    private float startDis = 0;
    private PointF midPoint;//中心点

    public ImageTouchView(Context context){
        super(context);
    }

    public ImageTouchView(Context context,AttributeSet paramAttributeSet){
        super(context,paramAttributeSet);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                currentMatrix.set(this.getImageMatrix());//记录ImageView当期的移动位置
                startPoint.set(event.getX(),event.getY());//开始点
                break;
            case MotionEvent.ACTION_MOVE://移动事件
                if (mode == DRAG) {//图片拖动事件
                    float dx = event.getX() - startPoint.x;//x轴移动距离
                    float dy = event.getY() - startPoint.y;//y轴移动距离
                    matrix.set(currentMatrix);//在当前的位置基础上移动
                    matrix.postTranslate(dx, dy);
                } else if(mode == ZOOM){//图片放大事件
                    float endDis = distance(event);//结束距离
                    if(endDis > 10f){
                        float scale = endDis / startDis;//放大倍数
                        Log.v("scale=", String.valueOf(scale));
                        matrix.set(currentMatrix);
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            //有手指离开屏幕，但屏幕还有触点(手指)
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            //当屏幕上已经有触点（手指）,再有一个手指压下屏幕,变成放大模式,计算两点之间中心点的位置
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                startDis = distance(event);//计算得到两根手指间的距离
                if(startDis > 10f){//避免手指上有两个茧
                    midPoint = mid(event);//计算两点之间中心点的位置
                    currentMatrix.set(this.getImageMatrix());//记录当前的缩放倍数
                }
                break;
            }
        this.setImageMatrix(matrix);
        return true;
    }

    /**
     * 两点之间的距离
     */
    private static float distance(MotionEvent event){
        //两根手指间的距离
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float)Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * 计算两点之间中心点的位置
     */
    private static PointF mid(MotionEvent event){
        float midx = event.getX(1) + event.getX(0);
        float midy = event.getY(1) + event.getY(0);
        return new PointF(midx/2, midy/2);
    }

    /**
     *根据裁剪框的位置和大小,截取图片
     *
     *@param frameView 裁剪框
     */
    public Bitmap getBitmap(ClipFrameView frameView) {
        if(frameView == null)return null;
        setDrawingCacheEnabled(true);
        buildDrawingCache();

        //判断裁剪框的区域是否超过了View的大小，避免超过大小而报错
        int left =  frameView.getFramePosition().x > 0 ? (int)frameView.getFramePosition().x : 0;
        int top = frameView.getFramePosition().y > 0 ? (int)frameView.getFramePosition().y : 0;
        int width = left+frameView.getFrameWidth() < mWidth ? (int)frameView.getFrameWidth() : (int)mWidth;
        int height = top+frameView.getFrameHeight() < mHeight ? (int)frameView.getFrameHeight() : (int)mHeight;
        //根据裁剪框的位置和大小,截取图片
        Bitmap finalBitmap = Bitmap.createBitmap(getDrawingCache(),left,top,width,height);
        // 释放资源
        destroyDrawingCache();
        return finalBitmap;
    }

    /**
     *将图片自动缩放到裁剪框的上部
     *
     *@param frameView 裁剪框
     */
    public void autoFillClipFrame(ClipFrameView frameView){
        if(getDrawable() == null || frameView == null)return;

        float left = frameView.getFramePosition().x;
        float top = frameView.getFramePosition().y;
        float width = frameView.getFrameWidth();
        float height = frameView.getFrameHeight();
        RectF dstRect = new RectF(left,top,left+width,top+height);
        RectF srcRect = new RectF(0,0,getDrawable().getIntrinsicWidth(),getDrawable().getMinimumHeight());
        Matrix newMatrix = new Matrix();
        //将源矩阵矩阵填充到目标矩阵，这里选择对其左上，根据需求可以选其他模式
        newMatrix.setRectToRect(srcRect,dstRect, Matrix.ScaleToFit.START);
        setImageMatrix(newMatrix);
        matrix = newMatrix;
        invalidate();
    }


    /**
     *设置图片
     *
     * @param filePath 图片的完整路径
     * @param multiple 倍数，当图片宽或高大于Vie的宽或高multiple倍实行向下采样
     */
    public void setImageFile(final String filePath,final int multiple){
        post(new Runnable() {
            @Override
            public void run() {
                //为了获取view的大小
                Bitmap bitmap = getSmallBitmap(filePath,(int) mWidth,(int)mHeight,multiple);
                if(bitmap != null)setImageBitmap(bitmap);
            }
        });
    }

    //计算图片的缩放大小
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight,int  multiple) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;//1是不缩放，2是缩小1/2,4是缩小1/4等
        if (height > reqHeight *multiple|| width > reqWidth*multiple) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private static Bitmap getSmallBitmap(String filePath, int w, int h,int multiple) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//true的话，不会真的把bitmap加载到内存,但能获取bitmap的大小信息等
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, w, h,multiple);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
