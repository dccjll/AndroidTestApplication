package com.paiai.android.test.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BitmapUtils {
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // 源图片的高度和宽度   
	    final int height = options.outHeight;   
	    final int width = options.outWidth;   
	    int inSampleSize = 1;   
	    if (height > reqHeight || width > reqWidth) {   
	        // 计算出实际宽高和目标宽高的比率   
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高   
	        // 一定都会大于等于目标的宽和高。   
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;   
	    }   
	    return inSampleSize;   
	} 
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
	    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小   
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;   
	    BitmapFactory.decodeResource(res, resId, options);
	    // 调用上面定义的方法计算inSampleSize值   
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);   
	    // 使用获取到的inSampleSize值再次解析图片   
	    options.inJustDecodeBounds = false;   
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static Bitmap decodeSampledBitmapFromFilePath(String filePath, int reqWidth, int reqHeight) {
	    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小   
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
//	    InputStream is = new FileInputStream(filePath);
//	    LogUtils.i("is=" + is);
	    BitmapFactory.decodeFile(filePath, options);
	    // 调用上面定义的方法计算inSampleSize值   
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);   
	    // 使用获取到的inSampleSize值再次解析图片   
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}  
	
	/**
     * 旋转图片
     * @param bitmap Bitmap
     * @param orientationDegree 旋转角度
     * @return 旋转之后的图片
     */
    public static Bitmap rotate(Bitmap bitmap, int orientationDegree) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(orientationDegree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    /**
     * 保存bitmap图片
     * @param bitmap
     * @param outFile
     * @return
     * @throws IOException
     */
    public static boolean save(Bitmap bitmap, String outFile)
            throws IOException {
        if (TextUtils.isEmpty(outFile) || bitmap == null)
            return false;
        byte[] data = bitmap2byte(bitmap);
        return save(data, outFile);
    }

    /**
     * 保存图片字节
     * @param bitmapBytes
     * @param outFile
     * @return
     * @throws IOException
     */
    private static boolean save(byte[] bitmapBytes, String outFile)
            throws IOException {
        FileOutputStream output = null;
        FileChannel channel = null;
        try {
        	File tmpFile = new File(outFile);
            tmpFile.delete();
            FileUtils.createFile(outFile);
            output = new FileOutputStream(outFile);
            channel = output.getChannel();
            ByteBuffer buffer = ByteBuffer.wrap(bitmapBytes);
            channel.write(buffer);
            return true;
        } finally {
            IOUtils.close(channel);
            IOUtils.close(output);
        }
    }

    /**
     * 将Bitmap转化为字节数组
     * @param bitmap
     * @return byte[]
     * @throws IOException
     */
    private static byte[] bitmap2byte(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] array = baos.toByteArray();
            baos.flush();
            return array;
        } finally {
            IOUtils.close(baos);
        }
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap    要放大的图片
     * @param dstWidth  目标宽
     * @param dstHeight 目标高
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int dstWidth, int dstHeight) {
        if(bitmap==null){
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) dstWidth / width);
        float scaleHeight = ((float) dstHeight / height);
        matrix.postScale(scaleWidht, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * 获得圆角图片的方法
     *
     * @param drawable
     * @param roundPx  4脚幅度
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Drawable drawable, float roundPx) {
        Bitmap bitmap = drawableToBitmap(drawable);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获得圆角图片的方法
     *
     * @param bitmap
     * @param roundPx 4脚幅度
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获取指定圆角的图片
     * @param bitmap 原图
     * @param roundPx 圆角半径
     * @param topLeft 左上角是否圆角
     * @param topRight 右上角是否圆角
     * @param bottomLeft 左下角是否圆角
     * @param bottomRight 右下角是否圆角
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx,Boolean topLeft,Boolean topRight,Boolean bottomLeft,Boolean bottomRight) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        if(!topLeft){
            canvas.drawRect(0, 0, roundPx, roundPx, paint);
        }
        if(!topRight){
            canvas.drawRect(bitmap.getWidth()-roundPx, 0, bitmap.getWidth(), roundPx, paint);
        }
        if(!bottomLeft){
            canvas.drawRect(0, bitmap.getHeight()-roundPx, roundPx, bitmap.getHeight(), paint);
        }
        if(!bottomRight){
            canvas.drawRect(bitmap.getWidth()-roundPx, bitmap.getHeight()-roundPx, bitmap.getWidth(), bitmap.getHeight(), paint);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获得圆形图片的方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        if(bitmap==null){
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = width > height ? height : width;
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(width / 2, height / 2, r / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return output;
    }

    /**
     * 获得圆形图片的方法
     *
     * @param drawable
     * @return
     */
    public static Bitmap getCircleBitmap(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = width > height ? height : width;
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(width / 2, height / 2, r / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return output;
    }

    /**
     * 获得带倒影的图片方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }
}
