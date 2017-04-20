package com.clam.clipphoto;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import static android.os.Environment.DIRECTORY_PICTURES;

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
                    //TODO 此处静态变量传递参数是错误，这里只是为了方便展示截图的成果，
                    // TODO bitmap最好保存文件再传递路径过去。另外Intent里面塞超过40KB的bitmap就会报错的
                    ShowActivity.save = ((ImageTouchView) imageView).getBitmap(clipFrameView);
                    Intent i = new Intent(MainActivity.this,ShowActivity.class);
                    startActivity(i);
                }
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView instanceof ImageTouchView){
                    ((ImageTouchView) imageView).autoFillClipFrame(clipFrameView);
                }
            }
        });
        imageView = (ImageView)findViewById(R.id.image_view);
        clipFrameView = (ClipFrameView)findViewById(R.id.frame_view);

        //本次demo图片存放的路径file path：/storage/emulated/0/Pictures/image_clip.jpg
        final File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES),"image_clip.jpg");
        if(imageView instanceof  ImageTouchView){
            ((ImageTouchView) imageView).setImageFile(file.getAbsolutePath(),2);
        }


        imageView.post(new Runnable() {
            @Override
            public void run() {
                if(imageView instanceof ImageTouchView){
                    ((ImageTouchView) imageView).autoFillClipFrame(clipFrameView);
                }
            }
        });
    }
}
