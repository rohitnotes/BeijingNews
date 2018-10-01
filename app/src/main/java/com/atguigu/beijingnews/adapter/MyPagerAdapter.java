package com.atguigu.beijingnews.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:引导页面的viewPager的适配器
 */

public class MyPagerAdapter extends PagerAdapter {

    private final ArrayList<ImageView> imageViews;

    public MyPagerAdapter(ArrayList<ImageView> imageViews) {
        this.imageViews = imageViews;
    }

    @Override
    public int getCount() {
        return imageViews.size();
    }

    /**
     * @param container viewPager容器
     * @param position  创建视图对应的位置
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = imageViews.get(position);
        container.addView(imageView);
        return imageView;
    }

    /**
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
