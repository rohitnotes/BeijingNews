package com.atguigu.beijingnews.pager.detail_pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.beijingnews.base.MenuDetailBasePager;
import com.atguigu.beijingnews.utils.LogUtil;

/**
 * Created by xinpengfei on 2016/10/20.
 * 微信:18091383534
 * Function :
 */

public class VoteDeatailPager extends MenuDetailBasePager {

    private TextView textView;

    public VoteDeatailPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        textView = new TextView(context);
        textView.setText("投票详情页面内容");
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("投票详情页面创建了...");
    }
}
