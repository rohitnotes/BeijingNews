package com.atguigu.beijingnews.pager.detail_pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.activity.NewsDetailActivity;
import com.atguigu.beijingnews.base.MenuDetailBasePager;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.atguigu.beijingnews.domain.TabDetailPagerBean;
import com.atguigu.beijingnews.utils.CacheUtils;
import com.atguigu.beijingnews.utils.Constants;
import com.atguigu.beijingnews.utils.LogUtil;
import com.atguigu.beijingnews.view.HorizontalScrollViewPager;
import com.atguigu.refreshlistview.RefreshListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by xinpengfei on 2016/10/18.
 * 微信:18091383534
 * Function :12个页签页面
 */

public class TabDetailPager extends MenuDetailBasePager {

    private final NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData;
    public static final String READ_ARRAY_ID = "read_array_id";

    @ViewInject(R.id.viewpager)
    private HorizontalScrollViewPager viewPager;

    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    @ViewInject(R.id.ll_point_group)
    private LinearLayout ll_point_group;

    @ViewInject(R.id.listview)
    private RefreshListView listView;

    /**
     * 之前被高亮显示的点
     */
    private int prePosition;
    /**
     * 顶部新闻数据集合
     */
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;

//    private TabDetailPagerBean.DataBean.NewsBean newsBean;

    /**
     * 新闻列表的数据
     */
    private List<TabDetailPagerBean.DataBean.NewsBean> news;

    private TabDetailPagerListAdapter adapter;
    private String url;

    /**
     * 加载更多的url
     */
    private String moreUrl;
    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;

    private ImageOptions imageOptions;

    /**
     * 是否拖拽
     */
    private boolean isDragging = false;

    private InternalHandler handler;

    class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int item = (viewPager.getCurrentItem() + 1) % topnews.size();
            viewPager.setCurrentItem(item);

            handler.postDelayed(new MyRunnable(), 4000);
        }
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    public TabDetailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(80), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();

    }

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        x.view().inject(TabDetailPager.this, view);

        View topnews = View.inflate(context, R.layout.topnews, null);
        x.view().inject(TabDetailPager.this, topnews);

        //以头的方式添加顶部轮播图
//        listView.addHeaderView(topnews);
        listView.addTopNewsView(topnews);

        //设置刷新的监听
        listView.setOnRefreshListener(new MyOnRefreshListener());

        //设置item的点击事件
        listView.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int realPosition = position - 1;
            TabDetailPagerBean.DataBean.NewsBean newsBean = news.get(realPosition);
            LogUtil.e(newsBean.toString());

            //先把保存的取出来，如果没有保存过就保存
            String read_array_id = CacheUtils.getString(context, READ_ARRAY_ID);//"" //111
            if (!read_array_id.contains(newsBean.getId() + "")) {//222
                //保存数据
                String value = read_array_id + newsBean.getId() + ",";

                CacheUtils.putString(context, READ_ARRAY_ID, value);

                //刷新适配器
                adapter.notifyDataSetChanged();
            }

            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.setData(Uri.parse(Constants.BASE_URL + newsBean.getUrl()));
            context.startActivity(intent);
        }
    }

    class MyOnRefreshListener implements RefreshListView.OnRefreshListener {

        @Override
        public void onPullDownRefresh() {
            //请求网络
            getDataFromNet(url);
        }

        @Override
        public void onLoadeMore() {

            if (TextUtils.isEmpty(moreUrl)) {
                //没有更多
                Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                listView.onFinishRefresh(false);
            } else {
                //加载更多
                getMoreDataFromNet();
            }

        }
    }

    private void getMoreDataFromNet() {

        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("TabDetailPager加载更多联网请求成功==" + result);
                isLoadMore = true;
                processData(result);
                listView.onFinishRefresh(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                LogUtil.e("TabDetailPager加载更多联网请求失败==" + ex.getMessage());
                listView.onFinishRefresh(false);
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

    @Override
    public void initData() {
        super.initData();

        //Constants.BASE_URL
        url = Constants.BASE_URL + childrenData.getUrl();
        LogUtil.e(TabDetailPager.this + ":" + url);

        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        //请求网络数据
        getDataFromNet(url);
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
                listView.onFinishRefresh(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("TabDetailPager联网请求失败==" + ex.getMessage());
                listView.onFinishRefresh(false);
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

    private void processData(String json) {

        TabDetailPagerBean pagerBean = parseJson(json);

        moreUrl = pagerBean.getData().getMore();//""

        if (TextUtils.isEmpty(moreUrl)) {
            moreUrl = "";
        } else {
            //Constants.BASE_URL +
            moreUrl = Constants.BASE_URL + pagerBean.getData().getMore();
        }
        if (!isLoadMore) {
            //顶部新闻数据集合
            topnews = pagerBean.getData().getTopnews();

            //原来的请求
            if (topnews != null && topnews.size() > 0) {
                viewPager.setAdapter(new TabDetailPagerAdapter());

                //监听页面的变化
                viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

                tv_title.setText(topnews.get(prePosition).getTitle());

                //把之前的红点移除
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

            }

            //设置listview的适配器
            news = pagerBean.getData().getNews();
            if (news != null && news.size() > 0) {
                adapter = new TabDetailPagerListAdapter();
                listView.setAdapter(adapter);
            }

//            LogUtil.e(pagerBean.getData().getTopnews().get(2).getTitle());
        } else {
            //加载更多
            isLoadMore = false;
            //把得到的更多数据加载到原来的集合中
            news.addAll(pagerBean.getData().getNews());
            adapter.notifyDataSetChanged();
        }

        //创建Handler开始发消息
        if (handler == null) {
            handler = new InternalHandler();
        }
        handler.removeCallbacksAndMessages(null);//把消息队列中所有的消息和回调移除
        handler.postDelayed(new MyRunnable(), 4000);

    }

    /**
     * 当页面改变时的监听
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

            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                isDragging = true;
                if (handler != null) {

                    handler.removeCallbacksAndMessages(null);
                }

            } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                isDragging = false;
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new MyRunnable(), 4000);
                }

            } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                isDragging = false;
                if(handler != null) {

                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new MyRunnable(), 4000);
                }
            }
        }
    }

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
            TabDetailPagerBean.DataBean.NewsBean newsBean = news.get(position);
            viewHolder.tv_title.setText(newsBean.getTitle());
            viewHolder.tv_time.setText(newsBean.getPubdate());

            String saveReadArrayId = CacheUtils.getString(context, READ_ARRAY_ID);
            if (saveReadArrayId.contains(newsBean.getId() + "")) {
                //设置灰色
                viewHolder.tv_title.setTextColor(Color.GRAY);
            } else {
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }

            //请求图片
            x.image().bind(viewHolder.iv_icon, Constants.BASE_URL + newsBean.getListimage(), imageOptions);
            Log.e("TAGA", "bind===" + Constants.BASE_URL + newsBean.getListimage());
            return convertView;
        }

    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

    class TabDetailPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.pic_item_list_default);

            x.image().bind(imageView, Constants.BASE_URL + topnews.get(position).getTopimage());
            container.addView(imageView);
            Log.e("TAGA", "path===" + Constants.BASE_URL + topnews.get(position).getTopimage());

            //设置触摸事件
            imageView.setOnTouchListener(new MyOnTouchListener());

            imageView.setTag(position);
            //设置点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    TabDetailPagerBean.DataBean.TopnewsBean topnewsBean = topnews.get(position);

                    Intent intent = new Intent(context, NewsDetailActivity.class);
                    intent.setData(Uri.parse(Constants.BASE_URL + topnewsBean.getUrl()));
                    context.startActivity(intent);

                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    class MyOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    LogUtil.e("按下ACTION_DOWN");
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    break;

                case MotionEvent.ACTION_UP:
                    LogUtil.e("离开ACTION_UP");
                    //创建Handler开始发消息
                    if (handler == null) {
                        handler = new InternalHandler();
                    }
                    handler.removeCallbacksAndMessages(null);//把消息队列中所有的消息和回调移除
                    handler.postDelayed(new MyRunnable(), 4000);
                    break;
            }

            return false;
        }
    }

    /**
     * 解析json数据
     *
     * @param json
     * @return
     */
    private TabDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
