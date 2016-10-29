package com.atguigu.swiperefreshlayoutdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 作者：杨光福 on 2016/5/31 10:36
 * 微信：yangguangfu520
 * QQ号：541433511
 */
public class MainActivity extends Activity {
    private Button btn_recyclerview;
    private Button btn_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_recyclerview = (Button) findViewById(R.id.btn_recyclerView);
        btn_listview = (Button) findViewById(R.id.btn_listview);
        btn_recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SwipeRefreshLayoutActivity.class));
            }
        });
        btn_listview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListViewActivity.class));
            }
        });
    }
}
