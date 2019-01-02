package core.base.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 位图转化
 * @Author jerome
 * @Date 2017/5/17
 */

public class BitmapUtil {

    /**
     * 根据路径从sd卡读取位图
     *
     * @param filePath
     * @return
     */
    public static Bitmap decodeBitmapSd(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            FileDescriptor fileDescriptor = inputStream.getFD();
            bitmap = decodeBitmap(fileDescriptor);
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("FileNotFound", filePath + "not found");
        } catch (IOException e) {
            Log.e("IOException", filePath + "read error");
        }
        return bitmap;
    }


    /**
     * 图片读取
     *
     * @param desc
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Bitmap decodeBitmap(FileDescriptor desc) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        try {
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFileDescriptor(desc, null, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
