package com.clam.clipphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity{
    private ImageView imageView;
    private ClipFrameView clipFrameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView instanceof  ImageTouchView){
                    Bitmap bitmap = ((ImageTouchView) imageView).getBitmap(clipFrameView);
                    ShowActivity.save = bitmap;
                    Intent i = new Intent(MainActivity.this,ShowActivity.class);
                    startActivity(i);
                }
            }
        });
        imageView = (ImageView)findViewById(R.id.image_view);
        clipFrameView = (ClipFrameView)findViewById(R.id.frame_view);
    }
}
