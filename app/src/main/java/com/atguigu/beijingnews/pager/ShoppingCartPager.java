package com.atguigu.beijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.atguigu.beijingnews.base.BasePager;
import com.atguigu.beijingnews.utils.LogUtil;

/**
 * Created by xinpengfei on 2016/10/17.
 * 微信:18091383534
 * Function :购物车页面
 */

public class ShoppingCartPager extends BasePager {

    public ShoppingCartPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("购物车面数据加载了....");
        //设置标题
        tv_title.setText("购物车");

        //创建子页面的视图
        TextView textView = new TextView(context);
        textView.setText("购物车页面");
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);

        //子页面的视图和FrameLayout结合在一起，形成一个新的页面
        fl_base_content.addView(textView);

    }
}
