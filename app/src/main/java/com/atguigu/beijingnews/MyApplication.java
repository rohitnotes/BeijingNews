package com.atguigu.beijingnews;

import android.app.Application;

import com.atguigu.beijingnews.volley.VolleyManager;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by xinpengfei on 2016/10/16.
 * 微信:18091383534
 * Function :代表整个软件
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化xUtils3,不要忘了在功能清单文件中设置name属性！
        x.Ext.init(this);
        x.Ext.setDebug(true);

        //初始化Volley
        VolleyManager.init(this);

        //初始化极光推送
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }
}
