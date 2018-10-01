package com.atguigu.beijingnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.domain.ShoppingCart;
import com.atguigu.beijingnews.utils.CartProvider;
import com.atguigu.beijingnews.view.NumberAddSubView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by xpf on 2016/10/26 :)
 * Wechat:18091383534
 * Function:购物车页面的适配器
 */

public class ShoppingCartPagerAdpater extends RecyclerView.Adapter<ShoppingCartPagerAdpater.ViewHolder> {

    private final Context context;
    private List<ShoppingCart> datas;

    /**
     * 是否点击全选按钮
     */
    private CheckBox checkbox_all;

    /**
     * 商品总价格
     */
    private TextView tv_total_price;

    /**
     * 商品总数量
     */
    private TextView tv_delete_total_count;

    /**
     * 删除商品的总数量
     */
    private TextView tv_total_count;

    private CartProvider cartProvider;

    public ShoppingCartPagerAdpater(Context context, final List<ShoppingCart> datas, final CheckBox checkbox_all, TextView tv_total_price, TextView tv_total_count, TextView tv_delete_total_count) {
        this.context = context;
        this.datas = datas;
        this.checkbox_all = checkbox_all;
        this.tv_total_price = tv_total_price;
        this.tv_total_count = tv_total_count;
        this.tv_delete_total_count = tv_delete_total_count;
        cartProvider = new CartProvider(context);

        // 显示总价格
        showTotalPrice();
        // 显示总数量
        showTotalCount();

        // 设置item的点击事件
        setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 0.状态要变化
                ShoppingCart cart = datas.get(position);
                cart.setCheck(!cart.isCheck());//状态取反
                notifyItemChanged(position);//刷新状态

                // 1.保存状态
                cartProvider.update(cart);

                // 2.设置全选和反选
                checkAll_none();

                // 3.计算总价格
                showTotalPrice();

                // 4.显示总数量
                showTotalCount();

                // 5.显示删除商品总数量
                showDeleteTotalCount();
            }
        });

        //校验是否全选
        checkAll_none();
        //设置CheckBox的点击事件
        checkbox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1.得到Checkbox的状态和设置全选和非全选
                checkAll_none(checkbox_all.isChecked());

                //2.显示总价格
                showTotalPrice();

                //4.显示总数量
                showTotalCount();

                //5.显示删除商品总数量
                showDeleteTotalCount();
            }
        });

    }

    public void checkAll_none(boolean isCheck) {

        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                ShoppingCart cart = datas.get(i);
                cart.setCheck(isCheck);
                notifyItemChanged(i);//刷新适配器
            }
        }

    }

    public void checkAll_none() {

        if (datas != null && datas.size() > 0) {
            int number = 0;
            for (int i = 0; i < datas.size(); i++) {
                ShoppingCart cart = datas.get(i);
                if (!cart.isCheck()) {//设置非全选
                    checkbox_all.setChecked(false);
                } else {
                    //选中
                    number++;
                }
            }
            if (number == datas.size()) {
                checkbox_all.setChecked(true);
            }
        }
    }

    /**
     * 显示总价格
     */
    public void showTotalPrice() {
        tv_total_price.setText("合计:￥" + getTotalPrice());
    }

    /**
     * 显示商品总数
     */
    public void showTotalCount() {
        tv_total_count.setText("去结算(" + getTotalCount() + ")");
    }

    /**
     * 显示删除商品总数
     */
    public void showDeleteTotalCount() {
        tv_delete_total_count.setText("删除(" + getTotalCount() + ")");
    }

    /**
     * 得到商品的总数量
     *
     * @return
     */
    private int getTotalCount() {

        int count = 0;
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                ShoppingCart cart = datas.get(i);//购物车类：是否被选中和多少个
                //是否被勾选
                if (cart.isCheck()) {
                    //得到数量，并且和之前的相加
                    count += cart.getCount();
                }
            }
        }
        return count;
    }

    /**
     * @return :循环把总价格给计算出来并返回
     */
    public float getTotalPrice() {
        float result = 0;
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                ShoppingCart cart = datas.get(i);
                // 判断是否被勾选
                if (cart.isCheck()) {
                    // 得到数量和商品单价计算出价格,然后和之前的相加
                    result += cart.getCount() * cart.getPrice();
                }
            }
        }
        return result;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_shopping_cart_pager, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到对应的数据
        final ShoppingCart cart = datas.get(position);

        //2.绑定数据
        Glide
                .with(context)
                .load(cart.getImgUrl())
                .centerCrop()
                .placeholder(R.drawable.news_pic_default)
                .crossFade()
                .into(holder.iv_icon);
        holder.tv_name.setText(cart.getName());
        holder.tv_price.setText("￥" + cart.getPrice());
        holder.number_add_sub_view.setValue(cart.getCount());
        // 设置最大的库存
        holder.number_add_sub_view.setMaxValue(10);
        // 设置商品对象是否选中(属性)
        holder.checkbox.setChecked(cart.isCheck());

        // 设置当点击增加或减少商品数量时的监听
        holder.number_add_sub_view.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonSubClick(View v, int value) {

                // 1.把这个值设置一下(不要忘了，否则会出现数字变化，价格不变的情况)
                cart.setCount(value);
                // 2.保存到内存中和本地
                cartProvider.update(cart);
                // 3.显示总价格
                showTotalPrice();
                // 4.显示总数量
                showTotalCount();
                // 5.显示删除商品总数量
                showDeleteTotalCount();
            }

            @Override
            public void onButtonAddClick(View v, int value) {

                // 1.把这个值设置一下
                cart.setCount(value);
                // 2.保持到内存中和本地
                cartProvider.update(cart);
                // 3.显示总价格
                showTotalPrice();
                // 4.显示总数量
                showTotalCount();
                // 5.显示删除商品总数量
                showDeleteTotalCount();
            }
        });
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 清除数据
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
    public void addData(int count, List<ShoppingCart> list) {

        datas.addAll(count, list);
        notifyItemRangeChanged(count, datas.size());
    }

    /**
     * 得到多少数据
     */
    public int getCount() {
        return datas.size();
    }

    public void addData(List<ShoppingCart> list) {
        addData(0, list);
    }

    /**
     * 删除数据
     */
    public void deleteData() {
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {

                ShoppingCart cart = datas.get(i);//删除选中的数据
                if (cart.isCheck()) {
                    datas.remove(cart);
                    cartProvider.deleteData(cart);
                    notifyItemRemoved(i);

                    i--;
                }
            }

//            for (Iterator iterator = datas.iterator(); iterator.hasNext(); ) {
//
//                ShoppingCart cart = (ShoppingCart) iterator.next();
//                if (cart.isCheck()) {
//
//                    //根据对象查找它在列表中的位置
//                    int i = datas.indexOf(cart);
//                    //在内存中移除，注意此时要用iterator去移除！
//                    iterator.remove();
//                    //移除本地和缓存的
//                    cartProvider.deleteData(cart);
//                    //刷新适配器
//                    notifyItemRemoved(i);
//                }
//            }
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkbox;
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private NumberAddSubView number_add_sub_view;

        public ViewHolder(View itemView) {
            super(itemView);

            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            number_add_sub_view = (NumberAddSubView) itemView.findViewById(R.id.number_add_sub_view);

            /**
             * 设置点击某条item的点击事件
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getLayoutPosition());
                    }
                }
            });
        }
    }

    /**
     * 设置当点击某个item时的监听
     */
    public interface OnItemClickListener {

        /**
         * @param view     :被点击的视图
         * @param position :被点击的位置
         */
        void onItemClick(View view, int position);
    }

    private OnItemClickListener itemClickListener;

    /**
     * 设置点击某条的监听
     *
     * @param itemClickListener
     */
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
