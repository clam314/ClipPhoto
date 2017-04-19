package com.clam.clipphoto;

import android.app.usage.UsageEvents;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
    private ImageView imageView;
    private ClipFrameView clipFrameView;


    private PointF startPoint = new PointF();
    private float oldDis;
    private int pointMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.image_view);
        clipFrameView = (ClipFrameView)findViewById(R.id.frame_view);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView)v;
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                startPoint.set(event.getX(),event.getY());
                pointMode = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDis = spacing(event);
                pointMode += 1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointMode = 0;
                break;
            case MotionEvent.ACTION_UP:
                pointMode = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointMode >= 2){
                    float newDis = spacing(event);
                    if(newDis > 100f){
                        zoom(view,newDis/oldDis);
                    }
                }else if(pointMode == 1){
                    translate(view,event.getX()-startPoint.x,event.getY()-startPoint.y);
                }
                break;
        }
        return true;
    }

    private float spacing(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void zoom(ImageView view,float scale){
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    private void translate(ImageView view,float tX,float tY){
        view.setTranslationX(tX);
        view.setTranslationY(tY);
    }
}
