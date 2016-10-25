package com.atguigu.beijingnews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Created by xinpengfei on 2016/10/22.
 * 微信:18091383534
 * Function :本地缓存
 */
public class LocalCacheUtils {

    private final MemoryCacheUtils memoryCacheUtils;

    public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils) {
        this.memoryCacheUtils = memoryCacheUtils;
    }

    /**
     * 获取图片
     *
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmap(String imageUrl) {

        //http:/19lkllsl.hslo.jpg-->MD5加密-->sllkkklskklkks(文件的名称)
        try {
            String fileName = MD5Encoder.encode(imageUrl);
            //mnt/sdcard/beijingnews/file/sllkkklskklkks
            File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/file/" + fileName);

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);

                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                if(bitmap != null) {
                    memoryCacheUtils.putBitmap(imageUrl,bitmap);
                }
                fis.close();

                return bitmap;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 保存图片
     *
     * @param imageUrl
     * @param bitmap
     */
    public void putBitmap(String imageUrl, Bitmap bitmap) {
        //http:/19lkllsl.hslo.jpg-->MD5加密-->sllkkklskklkks(文件的名称)
        try {
            String fileName = MD5Encoder.encode(imageUrl);
            //mnt/sdcard/beijingnews/file/sllkkklskklkks
            File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/file/" + fileName);

            File parent = file.getParentFile();////mnt/sdcard/beijingnews/file/
            if (!parent.exists()) {
                parent.mkdirs();//创建多层目录
            }

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
