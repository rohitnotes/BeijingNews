package com.atguigu.beijingnews;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Window;

import com.atguigu.beijingnews.fragment.ContentFragment;
import com.atguigu.beijingnews.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public static final String CONTENT_TAG = "content_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";
    /**
     * 记录屏幕的宽
     */
    private int screenWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getScreen();
        initView();
        initFragment();
    }

    private void initView() {
        //把标题栏隐藏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //1.设置主页面
        setContentView(R.layout.activity_main);
        //2.设置左侧菜单
        setBehindContentView(R.layout.leftmemu);

        SlidingMenu slidingMenu = getSlidingMenu();
        //3.设置右侧菜单
        //slidingMenu.setSecondaryMenu(R.layout.rightmenu);

        //4.设置支持滑动的模式：全屏滑动，边缘滑动，不可以滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        //5.设置页面模式：左侧菜单+主页面; 左侧菜单+主页面+右侧菜单； 主页面+右侧菜单
        slidingMenu.setMode(SlidingMenu.LEFT);

        //6.设置主页面占屏幕的宽度
//        slidingMenu.setBehindOffset(DensityUtil.dip2px(this, 200));
        //根据屏幕的密度计算出它占屏幕宽的0.625倍
        slidingMenu.setBehindOffset((int) (screenWidth * 0.625));
    }

    private void getScreen() {
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenWidth = display.widthPixels;//屏幕的宽
    }

    private void initFragment() {
        //1.获取FragmentManager并开启事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //2.添加LeftmenuFragment页面
        transaction.replace(R.id.fl_leftmenu, new LeftMenuFragment(), LEFTMENU_TAG);

        //3.添加主Fragment页面
        transaction.replace(R.id.fl_main_content, new ContentFragment(), CONTENT_TAG);

        //4.提交事务(千万不要忘了!)
        transaction.commit();
    }

    /**
     * 使用TAG得到左侧菜单页面的Fragment对象
     */
    public LeftMenuFragment getLeftMenuFragment() {
        return (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag(LEFTMENU_TAG);
    }

    /**
     * 使用TAG得到主页面的Fragment
     *
     * @return
     */
    public ContentFragment getContentFragment() {
        return (ContentFragment) getSupportFragmentManager().findFragmentByTag(CONTENT_TAG);
    }
}
