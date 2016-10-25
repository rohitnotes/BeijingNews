package com.atguigu.androidandh5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.atguigu.androidandh5.R.id.webview;

/**
 * 作者：尚硅谷-杨光福 on 2016/7/28 11:19
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：java和js互调
 */
public class JsCallJavaVideoActivity extends Activity {

    private WebView webView;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_call_java_video);

        webView = (WebView) findViewById(webview);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//设置支持javaScript
        //设置
        webSettings.setBuiltInZoomControls(true);//设置缩放按钮
        webSettings.setUseWideViewPort(true);//设置支持双击页面变大变小，页面要支持

        //这个监听有一个作业，点击页面的连接不会打开到系统的浏览器打开页面
        webView.setWebViewClient(new WebViewClient() {

            //当页面加载完成的时候回调
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        //添加Javascript接口
        webView.addJavascriptInterface(new MyJavascriptInterface(), "android");

//        webView.loadUrl("file:///android_asset/RealNetJSCallJavaActivity.htm");
        webView.loadUrl("http://192.168.1.25:8080/assets/RealNetJSCallJavaActivity.htm");
    }


    private class MyJavascriptInterface {

        @JavascriptInterface
        public void playVideo(int id, String videoUrl, String videoTitle) {
            //把本地的播放器调用起来
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(videoUrl), "video/*");
            startActivity(intent);
            //Toast.makeText(JsCallJavaVideoActivity.this, "我被js调用了", Toast.LENGTH_SHORT).show();
        }
    }
}
