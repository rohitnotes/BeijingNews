package com.atguigu.beijingnews.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;

import java.util.List;

/**
 * Created by xpf on 2016/10/31 :)
 * Wechat:18091383534
 * Function:左侧菜单ListView的适配器
 */

public class LeftMenuFragmentAdapter extends BaseAdapter {

    private Context context;
    private List<NewsCenterPagerBean2.NewsCenterPagerData> leftdata;

    private int selectPosition; // 记录被点击过的位置

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public LeftMenuFragmentAdapter(Context context, List<NewsCenterPagerBean2.NewsCenterPagerData> leftdata) {
        this.context = context;
        this.leftdata = leftdata;
    }

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

        //方法一：
        if (position == selectPosition) {
            textView.setEnabled(true);
        } else {
            textView.setEnabled(false);
        }

        // 方法二：当被点击的位置和当前的位置相等的时候就高亮显示(enable = true)
//        textView.setEnabled(position == selectPosition);
        return textView;
    }
}
