# ClipPhoto
### 一个图片裁剪工具

![矩形裁剪框](https://github.com/clam314/Image/blob/master/clip1.png)

![裁剪结果](https://github.com/clam314/Image/blob/master/clip2.png)

![圆形裁剪框](https://github.com/clam314/Image/blob/master/clip3.png)

![裁剪结果](https://github.com/clam314/Image/blob/master/clip4.png)

![自适应](https://github.com/clam314/Image/blob/master/clip5.png)

项目中总是有裁剪图片的的需求，这次单抽出来做个小demo

#### 实现的成员比较简单：
![](http://upload-images.jianshu.io/upload_images/1699916-3d9693d8e573a7b4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 各个类的职责
- ImageTouchView，负责图片的显示，单指移动图片，双指缩放图片，自适应裁剪框，最后根据ClipFrameView的接口获取裁剪框的位置和大小进行截图。
- ClipFrameView，裁剪框需要实现的接口，提供裁剪框的大小和位置
- RectFrameView、CircleFrameView、NinePatchFrameView，都是具体裁剪框的实现，主要就是绘制中间的裁剪框和框外的蒙版
- ShowActivity，单纯的负责展示裁剪后的图片

这里裁剪框和图片的裁剪进行了分离，ImageTouchView只需要知道裁剪框的位置和大小即可。具体裁剪框只需要实现ClipFrameView接口提供裁剪框位置和大小即可。自己可以实现ClipFrameView，实现更多的裁剪框样式

 详细实现介绍：
 http://www.jianshu.com/p/0820c42da114
