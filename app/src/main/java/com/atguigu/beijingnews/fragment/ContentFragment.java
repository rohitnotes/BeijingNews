package com.atguigu.beijingnews.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.atguigu.beijingnews.MainActivity;
import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.base.BaseFragment;
import com.atguigu.beijingnews.base.BasePager;
import com.atguigu.beijingnews.pager.HomePager;
import com.atguigu.beijingnews.pager.NewsCenterPager;
import com.atguigu.beijingnews.pager.SettingPager;
import com.atguigu.beijingnews.pager.ShoppingCartPager;
import com.atguigu.beijingnews.pager.ShoppingPager;
import com.atguigu.beijingnews.utils.LogUtil;
import com.atguigu.beijingnews.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by xinpengfei on 2016/10/16.
 * 微信:18091383534
 * Function :主页面的Fragment
 */

public class ContentFragment extends BaseFragment {

    /**
     * 使用注解的方式初始化
     */
    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;

//    @ViewInject(R.id.viewpager)
//    private ViewPager viewPager;

    @ViewInject(R.id.viewpager)
    private NoScrollViewPager viewPager;

    /**
     * 各个页面的实例
     */
    private ArrayList<BasePager> basePagers;

    @Override
    public View initView() {
        LogUtil.e("主页面的视图被初始化了...");
        View view = View.inflate(context, R.layout.fragment_content, null);

        //把view注入到xUtils中
        x.view().inject(ContentFragment.this, view);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("主页面的数据被初始化了...");
        rg_main.check(R.id.rb_home);

        //准备集合数据
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(context));//添加首页页面
        basePagers.add(new NewsCenterPager(context));//添加新闻页面
        basePagers.add(new ShoppingPager(context));//添加商城页面
        basePagers.add(new ShoppingCartPager(context));//添加购物车页面
        basePagers.add(new SettingPager(context));//添加设置页面


        //设置viewpager的适配器
        viewPager.setAdapter(new MyPagerAdapter());

        //监听RadioGroup状态的变化
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //设置监听页面被选中
//        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        basePagers.get(0).initData();

        //默认不可以滑动
        setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    /**
     * 得到新闻中心
     *
     * @return
     */
    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) basePagers.get(1);
    }


    /**
     * 设置触摸事件的模式
     *
     * @param touchmodeNone
     */
    private void setTouchModeAbove(int touchmodeNone) {

        MainActivity mainActivity = (MainActivity) context;
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        slidingMenu.setTouchModeAbove(touchmodeNone);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            //当选中某个页面的时候，才调用initData
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            //设置默认不可以滑动
            setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            switch (checkedId) {
                case R.id.rb_home://主页面
                    //不可以侧滑
                    viewPager.setCurrentItem(0, false);
                    break;
                case R.id.rb_news://新闻中心
                    //可以侧滑
                    setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    viewPager.setCurrentItem(1, false);
                    break;
                case R.id.rb_shopping://商城
                    viewPager.setCurrentItem(2, false);
                    break;
                case R.id.rb_shopping_cart://购物车
                    viewPager.setCurrentItem(3, false);
                    break;
                case R.id.rb_setting://设置
                    viewPager.setCurrentItem(4, false);
                    break;
            }
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return basePagers.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager basePager = basePagers.get(position);//HomePager,NewsCenterPager
            View rootView = basePager.rootView;//各个页面

            //合并一块或者联网请求数据
//            basePager.initData();
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
}
