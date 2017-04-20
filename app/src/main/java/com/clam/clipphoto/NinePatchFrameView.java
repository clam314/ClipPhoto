package com.clam.clipphoto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.util.AttributeSet;

/**
 * Created by clam314 on 2017/4/20
 */

public class NinePatchFrameView extends RectFrameView{

    public NinePatchFrameView(Context context) {
        super(context);
    }

    public NinePatchFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NinePatchFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);

        float oneThirdFrameW = frameWidth/3;
        float oneThirdFrameH = frameHeight/3;
        //裁剪框里的两条竖线
        canvas.drawLine(-oneThirdFrameW/2, -frameHeight/2, -oneThirdFrameW/2, frameHeight/2,paint);
        canvas.drawLine(oneThirdFrameW/2, -frameHeight/2, oneThirdFrameW/2, frameHeight/2,paint);
        //裁剪框里的两条横线
        canvas.drawLine(-frameWidth/2, -oneThirdFrameH/2, frameWidth/2, -oneThirdFrameH/2,paint);
        canvas.drawLine(-frameWidth/2, oneThirdFrameH/2, frameWidth/2, oneThirdFrameH/2,paint);
    }
}
