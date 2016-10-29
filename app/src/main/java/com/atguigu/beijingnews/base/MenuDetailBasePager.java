package com.atguigu.beijingnews.base;

import android.content.Context;
import android.view.View;

/**
 * Created by xinpengfei on 2016/10/17.
 * 微信:18091383534
 * Function :各个详情页面的公共类
 * 标题栏和内容部分:新闻，专题，组图，互动，投票详情页面等都继承MenuDetailBasePager
 */

public abstract class MenuDetailBasePager {

    public Context context;

    /**
     * 代表每个页面的视图
     */
    public View rootView;

    public MenuDetailBasePager(Context context) {
        this.context = context;//接受上下文要放在第一行代码里
        rootView = initView();
    }

    /**
     * 强制子类实现该方法，实现自己特有的ui效果
     *
     * @return
     */
    public abstract View initView();

    /**
     * 由子类重写该方法，子视图和FrameLayout结合成一个页面；绑定数据
     */
    public void initData() {

    }

}
