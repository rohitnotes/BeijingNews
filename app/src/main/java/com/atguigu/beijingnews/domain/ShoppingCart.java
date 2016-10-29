package com.atguigu.beijingnews.domain;

/**
 * Created by xpf on 2016/10/26 :)
 * Wechat:18091383534
 * Function:购物车类，显示购物车的状态，继承商品类；例如，数量，和是否选中
 */

public class ShoppingCart extends ShoppingPagerBean.Wares {

    /**
     * 商品的个数
     */
    private int count = 1;

    /**
     * 商品是否被选中
     */
    private boolean isCheck = true;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
