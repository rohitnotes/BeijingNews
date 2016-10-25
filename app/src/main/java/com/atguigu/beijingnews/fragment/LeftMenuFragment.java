package com.atguigu.beijingnews.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.beijingnews.MainActivity;
import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.base.BaseFragment;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.atguigu.beijingnews.pager.NewsCenterPager;
import com.atguigu.beijingnews.utils.DensityUtil;
import com.atguigu.beijingnews.utils.LogUtil;

import java.util.List;

/**
 * Created by xinpengfei on 2016/10/16.
 * 微信:18091383534
 * Function :左侧侧滑菜单的Fragment
 */

public class LeftMenuFragment extends BaseFragment {

    /**
     * 用于存放不同详情的ListView
     */
    private ListView listView;

    /**
     * 用于装左侧菜单里详情的适配器
     */
    private LeftMenuFragmentAdapter adapter;

    /**
     * 被点击过的位置
     */
    private int selectPosition;

    /**
     * 左侧菜单对应的集合数据
     */
//    private List<NewsCenterPagerBean.DataBean> leftdata;
    private List<NewsCenterPagerBean2.NewsCenterPagerData> leftdata;

    @Override
    public View initView() {
        LogUtil.e("左侧菜单的视图被初始化了...");

        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context, 40), 0, 0);
        listView.setDividerHeight(0);
        //按下某个设置为没有效果
        listView.setSelector(android.R.color.transparent);
        //屏蔽ListView在低版本的手机上，点击某个会变灰
        listView.setCacheColorHint(Color.TRANSPARENT);

        //设置点击某一条
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //1.点击的时候，设置被点击的高亮
                selectPosition = position;
                adapter.notifyDataSetChanged();//getCount()-->getView();

                //2.左侧菜单的收起
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//开<--->关

                //3.点击的时候
                switchPager(selectPosition);

            }
        });
        return listView;
    }

    /**
     * 根据位置切换到对应的详情页面
     *
     * @param selectPosition
     */
    private void switchPager(int selectPosition) {
        MainActivity mainActivity = (MainActivity) context;
        //3.切换不同的页面
        ContentFragment contentFragment = mainActivity.getContentFragment();
        //得到新闻中心
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        //调用新闻中心的方法切换到对应的页面
        newsCenterPager.switchPager(selectPosition);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("左侧菜单的数据被初始化了...");
    }

    //    public void setData(List<NewsCenterPagerBean.DataBean> leftdata) {
    public void setData(List<NewsCenterPagerBean2.NewsCenterPagerData> leftdata) {

        this.leftdata = leftdata;
        adapter = new LeftMenuFragmentAdapter();
        //设置适配器
        listView.setAdapter(adapter);

        switchPager(selectPosition);
    }


    class LeftMenuFragmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return leftdata.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenufragment, null);
            textView.setText(leftdata.get(position).getTitle());

            if (position == selectPosition) {
                textView.setEnabled(false);
            } else {
                textView.setEnabled(false);
            }
            textView.setEnabled(position == selectPosition);

            return textView;
        }
    }
}
