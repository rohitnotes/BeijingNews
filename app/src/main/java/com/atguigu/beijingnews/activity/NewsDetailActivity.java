package com.atguigu.beijingnews.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.utils.LogUtil;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsDetailActivity extends Activity implements View.OnClickListener {

    private WebView webview;
    private ProgressBar progressbar;
    private ImageButton ibMenu;
    private TextView tvTitle;
    private ImageButton ibBack;
    private ImageButton iconShare;
    private ImageButton icon_textsize;

    private int tempSize = 2;//正常
    private int realSize = tempSize;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViews();

        getData();
    }

    private void getData() {
        Uri uri = getIntent().getData();
        LogUtil.e("uri==" + uri);
        webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//设置支持JavaScript
        //设置
        webSettings.setBuiltInZoomControls(true);//设置缩放按钮
        webSettings.setUseWideViewPort(true);//设置支持双击页面变大变小，页面要支持

        //这个监听有一个作用，点击页面的连接不会打开系统的浏览器页面
        webview.setWebViewClient(new WebViewClient() {
            //当页面加载完成的时候回调
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressbar.setVisibility(View.GONE);
            }
        });

        webview.loadUrl(uri.toString());
//        webview.loadUrl("http://atguigu.com/teacher.shtml");
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-10-21 13:09:32 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {

        setContentView(R.layout.activity_news_detail);

        webview = (WebView) findViewById(R.id.webview);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        ibMenu = (ImageButton) findViewById(R.id.ib_menu);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        iconShare = (ImageButton) findViewById(R.id.icon_share);
        icon_textsize = (ImageButton) findViewById(R.id.icon_textsize);

        tvTitle.setVisibility(View.GONE);
        ibBack.setVisibility(View.VISIBLE);
        icon_textsize.setVisibility(View.VISIBLE);
        iconShare.setVisibility(View.VISIBLE);

        ibMenu.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        iconShare.setOnClickListener(this);
        icon_textsize.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-10-21 13:09:32 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == icon_textsize) {
            // Handle clicks for ibMenu
            Toast.makeText(NewsDetailActivity.this, "设置文字大小", Toast.LENGTH_SHORT).show();
            showChangeTextSizeDialog();

        } else if (v == ibBack) {
            // Handle clicks for ibBack
            finish();

        } else if (v == iconShare) {
            // Handle clicks for iconShare
            showShare();
//            Toast.makeText(NewsDetailActivity.this, "分享", Toast.LENGTH_SHORT).show();
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");

        // 启动分享GUI
        oks.show(this);
    }

    private void showChangeTextSizeDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("设置文字大小");
        String[] items = {"超大字体", "大字体", "正常字体", "小字体", "超小字体"};
        dialog.setSingleChoiceItems(items, realSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempSize = which;
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realSize = tempSize;
                changeTextSize(realSize);
            }
        });
        dialog.show();
    }

    /**
     * 0~4
     *
     * @param realSize
     */
    private void changeTextSize(int realSize) {
        switch (realSize) {
            case 0://超大字体
//                webSettings.setTextSize(WebSettings.TextSize.LARGEST);
                webSettings.setTextZoom(200);
                break;
            case 1://大字号
                //webSettings.setTextSize(WebSettings.TextSize.LARGER);
                webSettings.setTextZoom(150);
                break;
            case 2://正常
                //webSettings.setTextSize(WebSettings.TextSize.NORMAL);
                webSettings.setTextZoom(100);
                break;
            case 3://小字号
                //webSettings.setTextSize(WebSettings.TextSize.SMALLER);
                webSettings.setTextZoom(75);
                break;
            case 4://超小字体
                //webSettings.setTextSize(WebSettings.TextSize.SMALLEST);
                webSettings.setTextZoom(50);
                break;
        }
    }


}
