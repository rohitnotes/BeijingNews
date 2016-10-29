package com.atguigu.swiperefreshlayoutdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 作者：杨光福 on 2016/5/31 10:46
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：在ListView中SwipeRefreshLayout
 */
public class ListViewActivity  extends Activity{

    /**
     * 数据集合
     */
    private ArrayList<String> datas;


    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listview;
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        listview = (ListView) findViewById(R.id.listview);
        initData();
        adapter = new MyAdapter();
        listview.setAdapter(adapter);

        //设置下拉多少距离才触发下拉刷新动作
        swipeRefreshLayout.setDistanceToTriggerSync(100);
        /**
         * 设置下拉刷新控件的背景颜色
         */
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.white));

        //设置下拉刷新控件的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

        //设置swipeRefreshLayout 的下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new MyOnRefreshListener());



    }

    class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            Toast.makeText(ListViewActivity.this, "下拉刷新了", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addData();
                    //设置刷新状态还原-或者说结束
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    listview.setSelection(0);//定位到某条位置
                }
            },4000);


        }
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
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
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(ListViewActivity.this,R.layout.item_main,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //绑定数据
            String content= datas.get(position);
            viewHolder.tv_text.setText(content);

            return convertView;
        }
    }

    static  class ViewHolder{
        ImageView iv_icon;
        TextView tv_text;

    }

    /**
     * 初始化数据
     */
    private void addData() {
        for (int i = 0; i < 10; i++) {
            datas.add(0,"New Content" + i);
        }
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
}
