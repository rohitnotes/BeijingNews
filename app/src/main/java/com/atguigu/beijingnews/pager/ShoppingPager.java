package com.atguigu.beijingnews.pager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.adapter.ShoppingPagerAdpater;
import com.atguigu.beijingnews.base.BasePager;
import com.atguigu.beijingnews.domain.ShoppingPagerBean;
import com.atguigu.beijingnews.utils.CacheUtils;
import com.atguigu.beijingnews.utils.Constants;
import com.atguigu.beijingnews.utils.LogUtil;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

import static com.atguigu.beijingnews.R.id.recyclerview;

/**
 * Created by xinpengfei on 2016/10/17.
 * 微信:18091383534
 * Function :商城页面
 */

public class ShoppingPager extends BasePager {

    private MaterialRefreshLayout refresh;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private String url;

    private ShoppingPagerAdpater adpater;
    /**
     * 每页要求10个数据
     */
    private int pageSize = 10;
    /**
     * 第几页
     */
    private int curPage = 1;

    /**
     * 总的多少页
     */
    private int totalPager;
    /**
     * 商城热卖的数据集合
     */
    private List<ShoppingPagerBean.Wares> list;

    /**
     * 默认状态
     */
    private static final int STATE_NORMAL = 1;

    /**
     * 下拉刷新
     */
    private static final int STATE_REFRESH = 2;

    /**
     * 上拉刷新
     */
    private static final int STATE_LOADMORE = 3;
    /**
     * 当前状态
     */
    private int currentState = STATE_NORMAL;

    public ShoppingPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("商城面数据加载了....");
        //设置标题
        tv_title.setText("商城");

        //创建子页面的视图
        View view = View.inflate(context, R.layout.shopping_pager, null);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        recyclerView = (RecyclerView) view.findViewById(recyclerview);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        fl_base_content.removeAllViews();//加载新视图之前移除所有视图，否则会出现页面重叠

        //子页面的视图和FrameLayout结合在一起，形成一个新的页面
        fl_base_content.addView(view);

        //设置下拉刷新和上拉刷新的监听
        initRefreshLayout();

        getDataFromNet();//默认请求
    }

    private void initRefreshLayout() {
        refresh.setMaterialRefreshListener(new MyMaterialRefreshListener());
    }

    class MyMaterialRefreshListener extends MaterialRefreshListener {

        /**
         * 下拉刷新
         *
         * @param materialRefreshLayout
         */
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            refreshData();
        }

        /**
         * 加载更多
         *
         * @param materialRefreshLayout
         */
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            loadMoreData();
        }
    }

    private void loadMoreData() {

        currentState = STATE_LOADMORE;
        if (curPage < totalPager) {
            curPage += 1;
            //联网请求
            url = Constants.WARES_HOT_URL + "pageSize=" + pageSize + "&curPage=" + curPage;
            OkHttpUtils
                    .get()
                    .url(url)
                    .id(100)
                    .build()
                    .execute(new MyStringCallback());
        } else {
            //没有更多数据的页面
            refresh.finishRefreshLoadMore();
            Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();

        }
    }

    private void refreshData() {

        curPage = 1;//下拉刷新数据默认请求第一页
        currentState = STATE_REFRESH;
        url = Constants.WARES_HOT_URL + "pageSize=" + pageSize + "&curPage=" + curPage;

        /**
         * 使用OkHttpUtils联网请求数据
         */
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }

    private void getDataFromNet() {
        currentState = STATE_NORMAL;
        curPage = 1;
        url = Constants.WARES_HOT_URL + "pageSize=" + pageSize + "&curPage=" + curPage;
        String saveJson = CacheUtils.getString(context, Constants.WARES_HOT_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }

        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }

    /**
     * @param json : 解析和显示Json数据
     */
    private void processData(String json) {

        /**
         * 使用Gson将Json对象解析成ShoppingPagerBean对象
         */
        ShoppingPagerBean bean = new Gson().fromJson(json, ShoppingPagerBean.class);

        curPage = bean.getCurrentPage();
        pageSize = bean.getPageSize();
        totalPager = bean.getTotalCount();
        list = bean.getList();

        if (list != null && list.size() > 0) {
            LogUtil.e("curPage==" + curPage + ",pagerSize==" + pageSize + ",totalPage==" + totalPager + ",name==" + bean.getList().get(0).getName());
            showData();
        }

        progressBar.setVisibility(View.GONE);
    }

    /**
     * 根据状态显示数据
     */
    private void showData() {

        switch (currentState) {

            case STATE_NORMAL://有数据
                adpater = new ShoppingPagerAdpater(context, list);
                //设置适配器
                recyclerView.setAdapter(adpater);
                //设置显示为ListView的样式
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                break;

            case STATE_REFRESH:
                //清除数据
                adpater.clearData();
                adpater.addData(list);
                refresh.finishRefresh();//下拉刷新状态还原
                break;

            case STATE_LOADMORE:
                adpater.addData(adpater.getCount(), list);
                refresh.finishRefreshLoadMore();//加载更多状态还原
                break;

        }
    }

    /**
     * OkHttpUtils联网请求状态的回调
     */
    private class MyStringCallback extends StringCallback {

        /**
         * 在联网请求之前的回调
         *
         * @param request
         * @param id
         */
        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
        }

        /**
         * 在联网请求之后的回调
         *
         * @param id
         */
        @Override
        public void onAfter(int id) {
            super.onAfter(id);
        }

        /**
         * 在联网请求失败时的回调
         *
         * @param call
         * @param e
         * @param id
         */
        @Override
        public void onError(Call call, Exception e, int id) {
            LogUtil.e("okhttpUtils互动请求数据失败==" + e.getMessage());
            e.printStackTrace();
        }

        /**
         * 在联网请求成功时的回调
         *
         * @param s
         * @param id
         */
        @Override
        public void onResponse(String s, int id) {
            LogUtil.e("okhttpUtils互动请求数据成功==" + s);
            //缓存到本地存储中
            CacheUtils.putString(context, Constants.WARES_HOT_URL, s);
            //解析json数据
            processData(s);

            switch (id) {
                case 100:
                    Toast.makeText(context, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(context, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        /**
         * 进度更新的回调
         *
         * @param progress
         * @param total
         * @param id
         */
        @Override
        public void inProgress(float progress, long total, int id) {
            super.inProgress(progress, total, id);
        }
    }
}
