package com.atguigu.swiperefreshlayoutdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class SwipeRefreshLayoutActivity extends Activity {

    private RecyclerView recyclerview;
    /**
     * 数据集合
     */
    private ArrayList<String> datas;

    private  MyAdapter myAdapter;

    private Button btn_add;
    private Button btn_remove;
    private Button btn_list;
    private Button btn_grid;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiperefreshlayout);
        initData();
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_remove = (Button) findViewById(R.id.btn_remove);
        btn_list = (Button) findViewById(R.id.btn_list);
        btn_grid = (Button) findViewById(R.id.btn_grid);

        setLitener();

        myAdapter = new MyAdapter(this,datas);
        recyclerview.setAdapter(myAdapter);


        /**
         *  设置布局：
         * 第一个参数：上下文
         * 第二参数：方向
         * 第三个参数：排序低到高还是高到低显示，false是低到高显示
         *
         */
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this, 4,GridLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager staggeredGridLayoutManager =  new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(linearLayoutManager);

        //设置分割线-分割线需要自定义&还可以自定义分割线的样式
        //没有提供默认的分割线
        recyclerview.addItemDecoration(new DividerListItemDecoration(this, DividerListItemDecoration.VERTICAL_LIST));

        //设置点击item的点击事件
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String content) {
                Toast.makeText(SwipeRefreshLayoutActivity.this, "content==" + content + ",--position==" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //设置点击某张图片的点击事件
        myAdapter.setOnImageViewClickListener(new MyAdapter.OnImageViewClickListener() {
            @Override
            public void onImageViewClick(View view, int position) {
                Toast.makeText(SwipeRefreshLayoutActivity.this, "position==" + position + ",view==" + view.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        //设置动画
        recyclerview.setItemAnimator(new DefaultItemAnimator());




        //可以设置多个颜色也可以设置一种颜色；如果多个颜色将会来回切换不同颜色
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);

        //设置下拉多少距离才触发下拉刷新动作
        swipeRefreshLayout.setDistanceToTriggerSync(100);
        /**
         * 设置下拉刷新控件的背景颜色
         */
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.white));

        //设置下拉刷新控件的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

        //设置swipeRefreshLayout 的下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new MyOnRefreshListener());
    }

    class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            Toast.makeText(SwipeRefreshLayoutActivity.this, "下拉刷新了", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addData();
                    //设置刷新状态还原-或者说结束
                    swipeRefreshLayout.setRefreshing(false);
                    myAdapter.notifyItemRangeChanged(0,10);
                    recyclerview.scrollToPosition(0);//定位到某条位置
                }
            },4000);


        }
    }

    private void setLitener() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.addData(0,"Content NetData");
                //定位到第0个位置
                recyclerview.scrollToPosition(0);
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.removeData(0);
            }
        });

        btn_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManager gridLayoutManager =  new GridLayoutManager(SwipeRefreshLayoutActivity.this, 2,GridLayoutManager.VERTICAL, false);
                recyclerview.setLayoutManager(gridLayoutManager);
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(SwipeRefreshLayoutActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerview.setLayoutManager(linearLayoutManager);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add("Content" + i);
        }
    }

    /**
     * 初始化数据
     */
    private void addData() {
        for (int i = 0; i < 10; i++) {
            datas.add(0,"New Content" + i);
        }
    }
}
