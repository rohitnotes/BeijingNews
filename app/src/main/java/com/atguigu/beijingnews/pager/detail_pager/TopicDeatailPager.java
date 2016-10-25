package com.atguigu.beijingnews.pager.detail_pager;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.beijingnews.MainActivity;
import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.base.MenuDetailBasePager;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinpengfei on 2016/10/17.
 * 微信:18091383534
 * Function :新闻专题详情页面
 */

public class TopicDeatailPager extends MenuDetailBasePager {

    /**
     * 专题详情页面的数据
     */
    private final List<NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData> childrenDatas;

    /**
     * 专题详情页面的ui集合
     */
    private ArrayList<MenuDetailBasePager> detailBasePagers;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

//    @ViewInject(R.id.indicator)
//    private TabPageIndicator indicator;

    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;

    public TopicDeatailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData newsCenterPagerData) {
        super(context);
        childrenDatas = newsCenterPagerData.getChildren();
    }

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.topic_detail_pager, null);
        x.view().inject(this, view);

        return view;
    }

    @Event(value = R.id.ib_next)
    private void tabNext(View view) {
        //有点击事件
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    @Override
    public void initData() {
        super.initData();

        detailBasePagers = new ArrayList<>();
        for (int i = 0; i < childrenDatas.size(); i++) {
            //12个页签页面
            detailBasePagers.add(new TopicTabDetailPager(context, childrenDatas.get(i)));
        }

        //设置适配器
        viewPager.setAdapter(new NewsDetailPagerAdapter());

        //TabPageIndicator和ViewPager关联，关联要在ViewPager设置适配器之后
//        indicator.setViewPager(viewPager);

        //注意了setupWithViewPager必须在ViewPager.setAdapter()之后调用
        tablayout.setupWithViewPager(viewPager);
        //关联后，监听页面的改变由TabPagerIndicator
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        //设置可以滚动
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);

//        for (int i = 0; i < tablayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tablayout.getTabAt(i);
//        tab.setIcon(R.drawable.dot_focus);
//            tab.setCustomView(getTabView(i));
//        }

    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(childrenDatas.get(position).getTitle());
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.dot_focus);
        return view;
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                //北京--SlidingMenu可以滑动
                setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            } else {
                //其他--SlidingMenu不可以滑动
                setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * @param touchmodeNone
     */

    private void setTouchModeAbove(int touchmodeNone) {
        MainActivity mainActivity = (MainActivity) context;
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        slidingMenu.setTouchModeAbove(touchmodeNone);
    }

    class NewsDetailPagerAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return childrenDatas.get(position).getTitle();
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

            MenuDetailBasePager tabDetailPager = detailBasePagers.get(position);
            View rootView = tabDetailPager.rootView;
            tabDetailPager.initData();
            container.addView(rootView);

            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
