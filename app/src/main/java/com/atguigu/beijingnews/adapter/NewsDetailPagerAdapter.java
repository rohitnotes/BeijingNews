package com.atguigu.beijingnews.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.atguigu.beijingnews.base.MenuDetailBasePager;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpf on 2016/10/31 :)
 * Wechat:18091383534
 * Function:新闻详情页面的适配器
 */

public class NewsDetailPagerAdapter extends PagerAdapter {

    private final List<NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData> childrenData;
    private final ArrayList<MenuDetailBasePager> detailBasePagers;

    public NewsDetailPagerAdapter(List<NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData> childrenData, ArrayList<MenuDetailBasePager> detailBasePagers) {
        this.childrenData = childrenData;
        this.detailBasePagers = detailBasePagers;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return childrenData.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return detailBasePagers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        MenuDetailBasePager tabDetailPager = detailBasePagers.get(position);//TabDetailPager
        View rootView = tabDetailPager.rootView;
        tabDetailPager.initData();//初始化数据
        container.addView(rootView);

        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
