package com.clam.clipphoto;

import android.content.Context;
import android.graphics.Bitmap;
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

    private PointF startPoint = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix currentMaritx = new Matrix();

    private int mode = 0;//用于标记模式
    private static final int DRAG = 1;//拖动
    private static final int ZOOM = 2;//放大
    private float startDis = 0;
    private PointF midPoint;//中心点

    /**
     * 默认构造函数
     * @param context
     */
    public ImageTouchView(Context context){
        super(context);
    }

    /**
     * 该构造方法在静态引入XML文件中是必须的
     * @param context
     * @param paramAttributeSet
     */
    public ImageTouchView(Context context,AttributeSet paramAttributeSet){
        super(context,paramAttributeSet);
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
     * @param event
     * @return
     */
    private static float distance(MotionEvent event){
        //两根手指间的距离
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float)Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * 计算两点之间中心点的位置
     * @param event
     * @return
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

}
