package core.base.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import core.base.log.L;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class ABImageUtil {

    private static final java.lang.String TAG = ABImageUtil.class.getSimpleName();

    public static File compress(Uri fileUri) {
        String path = fileUri.getPath();
        return compress(path);
    }


    /**
     * 这个压缩方法调用发现并没压缩,慎用
     *
     * @param path
     * @return
     */
    public static File compress(String path) {
        File outputFile = new File(path);
        long startTime = System.currentTimeMillis();
        L.i(TAG, "待上传文件大小=" + outputFile.length());
        long fileSize = outputFile.length();
        final long fileMaxSize = 100 * 1024;
        if (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("", "sss ok " + outputFile.length());
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }
        }
        L.i(TAG, "图片压缩后路径=" + outputFile.getAbsolutePath() + "图片压缩后大小=" + outputFile.length() + "   花费时间=" + (System.currentTimeMillis() - startTime) / 1000.);
        return outputFile;
    }


    /**
     * 一切都操作uri
     *
     * @return
     */
    public static Uri createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + Math.random();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }

    public static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //--------------------add 2017-12-22 17:47:44--------------------------

    /**
     * res目录下面的一张图片保存到本地
     *
     * @param id 图片的id
     */
    public static void saveImageToSDCard(Context context, int id) {
        // getFilesDir().getAbsolutePath()+"/image"\
        //在本地创建一个文件夹
        File file = new File(context.getFilesDir().getAbsolutePath() + "/image");
        // File absoluteFile = getFilesDir().getAbsoluteFile();
        //判断本地是否存在，防止每次启动App都要创建
        if (file.exists()) {
            return;
        }
        Log.i(TAG, "----------------------------------------------------------------");
        //使用BitmapFactory把res下的图片转换成Bitmap对象
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        FileOutputStream fos = null;
        try {
            //获得一个可写的输入流
            fos = context.openFileOutput("image", Context.MODE_PRIVATE);
            //使用图片压缩对图片进行处理  压缩的格式  可以是JPEG、PNG、WEBP
            //第二个参数是图片的压缩比例，第三个参数是写入流
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i(TAG, "绝对路径" + context.getFilesDir().getAbsolutePath() + "/image");
    }

    public static String getSDCardImage(Context context) {
        try {
            return context.getFilesDir().getAbsolutePath() + "/image";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //---------------------------add 2018-5-10 10:49:53---------------------------
    public static void compressImage(Context context, String imgPath, OnCompressListener compressListener) {
        Luban.with(context)
                .load(imgPath)
                .ignoreBy(100)
                .setCompressListener(compressListener).launch();
    }
}
