package com.atguigu.beijingnews.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by xinpengfei on 2016/10/18.
 * 微信:18091383534
 * Function :水平滑动的viewPager
 */

public class HorizontalScrollViewPager extends ViewPager {

    private float startX;
    private float startY;

    public HorizontalScrollViewPager(Context context) {
        super(context);
    }

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 1.水平方向滑动
     * a,第0个位置，并且是从左到右滑动
     * getParent().requestDisallowInterceptTouchEvent(false);
     * b,滑动到最后一个，并且是从右到左滑动
     * getParent().requestDisallowInterceptTouchEvent(false);
     * c,其他中间部分
     * getParent().requestDisallowInterceptTouchEvent(true);
     * 2.竖直方向滑动
     * getParent().requestDisallowInterceptTouchEvent(false);
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //反拦截-请求父类禁用拦截方法

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //优先要把事件给当前控件
                getParent().requestDisallowInterceptTouchEvent(true);
                //1.记录按下的坐标
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //2.来到新的坐标
                float endX = ev.getX();
                float endY = ev.getY();

                //3.计算距离
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);

                if (distanceX > distanceY) {
                    //水平方向滑动
                    //a,第0个位置，并且是从左到右滑动
                    if (getCurrentItem() == 0 && endX - startX > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);

                        //b,滑动到最后一个，并且是从右到左滑动
                    } else if (getCurrentItem() == getAdapter().getCount() - 1 && endX - startX < 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);

                    } else {//c,其他中间部分
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else {
                    //竖直方向滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
