package com.atguigu.beijingnews.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.atguigu.beijingnews.base.BasePager;

import java.util.ArrayList;

/**
 * Created by xpf on 2016/10/31 :)
 * Wechat:18091383534
 * Function:ContentFragment里面的viewpager的适配器(最外层)
 */

public class ContentFragmentPagerAdapter extends PagerAdapter {

    private final ArrayList<BasePager> basePagers;

    public ContentFragmentPagerAdapter(ArrayList<BasePager> basePagers) {
        this.basePagers = basePagers;
    }

    @Override
    public int getCount() {
        return basePagers.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePager basePager = basePagers.get(position);//HomePager,NewsCenterPager
        View rootView = basePager.rootView;//各个页面

        //合并一块或者联网请求数据
//        basePager.initData();
        container.addView(rootView);

        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
