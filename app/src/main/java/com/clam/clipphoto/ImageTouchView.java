package com.clam.clipphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
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
    private Matrix currentMaritx = new Matrix();

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
                currentMaritx.set(this.getImageMatrix());//记录ImageView当期的移动位置
                startPoint.set(event.getX(),event.getY());//开始点
                break;
            case MotionEvent.ACTION_MOVE://移动事件
                if (mode == DRAG) {//图片拖动事件
                    float dx = event.getX() - startPoint.x;//x轴移动距离
                    float dy = event.getY() - startPoint.y;//y轴移动距离
                    matrix.set(currentMaritx);//在当前的位置基础上移动
                    matrix.postTranslate(dx, dy);
                } else if(mode == ZOOM){//图片放大事件
                    float endDis = distance(event);//结束距离
                    if(endDis > 10f){
                        float scale = endDis / startDis;//放大倍数
                        Log.v("scale=", String.valueOf(scale));
                        matrix.set(currentMaritx);
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
                    currentMaritx.set(this.getImageMatrix());//记录当前的缩放倍数
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

    public Bitmap getBitmap(ClipFrameView frameView) {
        setDrawingCacheEnabled(true);
        buildDrawingCache();

        int left = (int) frameView.getFramePosition().x;
        int top = (int) frameView.getFramePosition().y;
        int width = (int) frameView.getFrameWidth();
        int height = (int) frameView.getFrameHeight();

        Bitmap finalBitmap = Bitmap.createBitmap(getDrawingCache(),left,top,width,height);
        // 释放资源
        destroyDrawingCache();
        return finalBitmap;
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
                Bitmap bitmap = getSmallBitmap(filePath,(int) mWidth,(int)mHeight,multiple);
                if(bitmap != null)setImageBitmap(bitmap);
            }
        });
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight,int  multiple) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight *multiple|| width > reqWidth*multiple) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private static Bitmap getSmallBitmap(String filePath, int w, int h,int multiple) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, w, h,multiple);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
