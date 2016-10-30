package com.atguigu.beijingnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.domain.ShoppingCart;
import com.atguigu.beijingnews.domain.ShoppingPagerBean;
import com.atguigu.beijingnews.utils.CartProvider;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by xpf on 2016/10/26 :)
 * Wechat:18091383534
 * Function:商品详情的适配器
 */

public class ShoppingPagerAdpater extends RecyclerView.Adapter<ShoppingPagerAdpater.ViewHolder> {

    private final Context context;
    private final List<ShoppingPagerBean.Wares> datas;
    private CartProvider cartProvider;

    public ShoppingPagerAdpater(Context context, List<ShoppingPagerBean.Wares> datas) {
        this.context = context;
        this.datas = datas;
        cartProvider = new CartProvider(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_shopping_pager, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到对应的数据
        ShoppingPagerBean.Wares listBean = datas.get(position);
        Glide
                .with(context)
                .load(listBean.getImgUrl())
                .centerCrop()
                .placeholder(R.drawable.pic_item_list_default)
                .crossFade()
                .into(holder.iv_icon);

        holder.tv_name.setText(listBean.getName());
        holder.tv_price.setText("￥" + listBean.getPrice());

        //2.绑定数据
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 清楚数据
     */
    public void clearData() {
        datas.clear();
        notifyItemRangeRemoved(0, datas.size());
    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addData(int count, List<ShoppingPagerBean.Wares> list) {

        datas.addAll(count, list);
        notifyItemRangeChanged(count, datas.size());
    }

    /**
     * 得到多少数据
     */
    public int getCount() {
        return datas.size();
    }

    public void addData(List<ShoppingPagerBean.Wares> list) {
        addData(0, list);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private Button btn_buy;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            btn_buy = (Button) itemView.findViewById(R.id.btn_buy);

            btn_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "已成功放入您的购物车0.0", Toast.LENGTH_SHORT).show();
                    ShoppingPagerBean.Wares wares = datas.get(getLayoutPosition());
                    ShoppingCart cart = cartProvider.convertion(wares);
                    cartProvider.addData(cart);
                }
            });
        }
    }
}
