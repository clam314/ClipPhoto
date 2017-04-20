package com.clam.clipphoto;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowActivity extends AppCompatActivity {
    public static Bitmap save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ImageView imageView = (ImageView) findViewById(R.id.image_show);
        imageView.setImageBitmap(save);
    }
}
