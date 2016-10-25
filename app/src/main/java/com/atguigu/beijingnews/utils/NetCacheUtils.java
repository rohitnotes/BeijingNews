package com.atguigu.beijingnews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xinpengfei on 2016/10/22.
 * 微信:18091383534
 * Function :网络缓存工具类
 */
public class NetCacheUtils {

    /**
     * 请求成功
     */
    public static final int SUCCESS = 1;
    /**
     * 请求失败
     */
    public static final int FAIL = 2;

    private final Handler handler;
    private final ExecutorService service;

    /**
     * 本地缓存工具类
     */
    private final LocalCacheUtils localCacheUtils;

    /**
     * 内存缓存工具类
     */
    private final MemoryCacheUtils memoryCacheUtils;

    public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils,MemoryCacheUtils memoryCacheUtils) {
        this.handler = handler;
        service = Executors.newFixedThreadPool(10);
        this.localCacheUtils = localCacheUtils;
        this.memoryCacheUtils = memoryCacheUtils;
    }

    public void getBitmapFromNet(String imageUrl, int position) {
//        new Thread(new MyRunnable(imageUrl,position)).start();
        service.execute(new MyRunnable(imageUrl, position));
    }

    class MyRunnable implements Runnable {

        private final String imageUrl;
        private final int position;

        public MyRunnable(String imageUrl, int position) {
            this.imageUrl = imageUrl;
            this.position = position;
        }

        @Override
        public void run() {
            //联网请求图片
            try {
                URL url = new URL(imageUrl);
                //获取连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);
                conn.connect();
                int requestCode = conn.getResponseCode();
                if (requestCode == 200) {

                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    //请求网络图片，获取图片，显示到控件上
                    //向内存中存一份
                    memoryCacheUtils.putBitmap(imageUrl,bitmap);
                    //向本地文件中存储一份
                    localCacheUtils.putBitmap(imageUrl, bitmap);

                    //发消息
                    Message msg = Message.obtain();
                    msg.obj = bitmap;
                    msg.arg1 = position;
                    msg.what = SUCCESS;
                    handler.sendMessage(msg);

                }

            } catch (Exception e) {
                e.printStackTrace();
                Message msg = Message.obtain();
                msg.what = FAIL;
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        }
    }

}
