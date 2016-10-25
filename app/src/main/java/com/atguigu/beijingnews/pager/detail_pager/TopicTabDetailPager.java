package com.atguigu.beijingnews.pager.detail_pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.base.MenuDetailBasePager;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.atguigu.beijingnews.domain.TabDetailPagerBean;
import com.atguigu.beijingnews.utils.CacheUtils;
import com.atguigu.beijingnews.utils.Constants;
import com.atguigu.beijingnews.utils.LogUtil;
import com.atguigu.beijingnews.view.HorizontalScrollViewPager;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by xinpengfei on 2016/10/20.
 * 微信:18091383534
 * Function :专题的12个页签页面
 */

public class TopicTabDetailPager extends MenuDetailBasePager {

    private final NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData;

    @ViewInject(R.id.viewpager)
    private HorizontalScrollViewPager viewpager;

    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    @ViewInject(R.id.ll_point_group)
    private LinearLayout ll_point_group;

    @ViewInject(R.id.pull_refresh_list)
    private PullToRefreshListView pull_refresh_list;

    private ListView listview;

    /**
     * 之前被高亮显示的点
     */
    private int prePosition;
    /**
     * 顶部新闻viewPager的数据集合
     */
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;
    /**
     * 新闻列表ListView的数据
     */
    private List<TabDetailPagerBean.DataBean.NewsBean> news;

    private TabDetailPagerListAdapter adapter;
    private TabDetailPagerBean.DataBean.NewsBean newsBean;
    private String url;
    /**
     * 加载更多的url
     */
    private String moreUrl;
    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;

    public TopicTabDetailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.topic_tabdetail_pager, null);
        x.view().inject(TopicTabDetailPager.this, view);

        View topnews = View.inflate(context, R.layout.topnews, null);
        x.view().inject(TopicTabDetailPager.this, topnews);

        listview = pull_refresh_list.getRefreshableView();//listView
        //以头的方式添加顶部轮播图
        listview.addHeaderView(topnews);

        pull_refresh_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //请求网络
                getDataFromNet(url);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (TextUtils.isEmpty(moreUrl)) {
                    //没有更多
                    Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                    pull_refresh_list.onRefreshComplete();
                } else {
                    //加载更多
                    getMoreDataFromNet();

                }
            }
        });

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(context);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        pull_refresh_list.setOnPullEventListener(soundListener);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        LogUtil.e(TopicTabDetailPager.this + ":" + url);

        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        //请求网络
        getDataFromNet(url);
    }

    /**
     * 获取更多数据
     *
     * @param json
     */
    private void processData(String json) {

        TabDetailPagerBean pagerBean = parseJson(json);
        //顶部新闻数据集合
        topnews = pagerBean.getData().getTopnews();

        moreUrl = pagerBean.getData().getMore();//""

        if (TextUtils.isEmpty(moreUrl)) {
            moreUrl = "";
        } else {
            moreUrl = Constants.BASE_URL + pagerBean.getData().getMore();
        }

        if (!isLoadMore) {
            //原来的请求
            if (topnews != null && topnews.size() > 0) {

                viewpager.setAdapter(new TabDetailPagerAdapter());

                //监听页面的变化
                viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

                tv_title.setText(topnews.get(prePosition).getTitle());

                //把之前的空点全部移除
                ll_point_group.removeAllViews();
                for (int i = 0; i < topnews.size(); i++) {

                    ImageView imageView = new ImageView(context);
                    imageView.setBackgroundResource(R.drawable.point_selector);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(5), DensityUtil.dip2px(5));
                    if (i != 0) {
                        imageView.setEnabled(false);
                        params.leftMargin = DensityUtil.dip2px(5);
                    } else {
                        imageView.setEnabled(true);
                    }
                    imageView.setLayoutParams(params);

                    //把点添加到线性布局
                    ll_point_group.addView(imageView);
                }

//                ll_point_group.getChildAt(prePosition).setEnabled(true);
            }

            //设置listView的适配器
            news = pagerBean.getData().getNews();
            if (news != null && news.size() > 0) {
                adapter = new TabDetailPagerListAdapter();
                listview.setAdapter(adapter);
            }

        } else {

            //加载更多
            isLoadMore = false;
            //把得到的更多数据加载到原来的集合中
            news.addAll(pagerBean.getData().getNews());
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 当viewPager页面滚动时的监听
     */
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            //把之前高亮的点设置为默认
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            //把当前的位置对应的点设置高亮
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition = position;
        }

        @Override
        public void onPageSelected(int position) {
            tv_title.setText(topnews.get(position).getTitle());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 用于装ListView中数据的适配器
     */
    class TabDetailPagerListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
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

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_tabdetail_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到对应的数据
            newsBean = news.get(position);
            viewHolder.tv_title.setText(newsBean.getTitle());
            viewHolder.tv_time.setText(newsBean.getPubdate());

            //Constants.BASE_URL +.replace("zhbj", "bjxw")
            x.image().bind(viewHolder.iv_icon, Constants.BASE_URL + newsBean.getListimage());

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

    /**
     * 新闻详情页面的适配器(顶部viewPager的适配器)
     */
    class TabDetailPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imgaeView = new ImageView(context);
            imgaeView.setBackgroundResource(R.drawable.pic_item_list_default);

            //Constants.BASE_URL +.replace("zhbj","bjxw")
            x.image().bind(imgaeView, Constants.BASE_URL + topnews.get(position).getTopimage());
            container.addView(imgaeView);

            return imgaeView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //移除容器里面的视图对象
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /**
     * 解析Json数据
     *
     * @param json
     * @return
     */
    private TabDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }

    /**
     * 联网加载更多数据
     */
    private void getMoreDataFromNet() {

        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("TabDetailPager加载更多联网请求成功==" + result);
                isLoadMore = true;
                processData(result);
                pull_refresh_list.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("TabDetailPager加载更多联网请求失败==" + ex.getMessage());
                pull_refresh_list.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });

    }

    /**
     * 联网获取数据
     *
     * @param url
     */
    private void getDataFromNet(final String url) {

        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("TabDetailPager联网请求成功==" + result);
                CacheUtils.putString(context, url, result);
                processData(result);
                pull_refresh_list.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("TabDetailPager联网请求失败==" + ex.getMessage());
                pull_refresh_list.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

}
