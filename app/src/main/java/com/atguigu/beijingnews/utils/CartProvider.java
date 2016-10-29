package com.atguigu.beijingnews.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.atguigu.beijingnews.domain.ShoppingCart;
import com.atguigu.beijingnews.domain.ShoppingPagerBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpf on 2016/10/26 :)
 * Wechat:18091383534
 * Function:购物车存储器类，存取购物车的内容；取内容json数据-->List; 存List-->json数据
 */

public class CartProvider {

    public static final String JSON_CART = "json_cart";
    private final Context context;

    /**
     * SparseArray 替代HashMap,性能优于HashMap
     */
    private SparseArray<ShoppingCart> sparseArray;

    public CartProvider(Context context) {
        this.context = context;
        sparseArray = new SparseArray<>(10);
        listToSparse();
    }

    /**
     * 把列表中的数据转换成sparseArray
     */
    private void listToSparse() {

        //得到集合
        List<ShoppingCart> shoppingCarts = getAllData();
        if (shoppingCarts != null && shoppingCarts.size() > 0) {

            for (int i = 0; i < shoppingCarts.size(); i++) {
                ShoppingCart shoppingCart = shoppingCarts.get(i);
                sparseArray.put(shoppingCart.getId(), shoppingCart);
            }
        }
    }

    /**
     * 得到所有数据
     *
     * @return
     */
    public List<ShoppingCart> getAllData() {
        return getLocalData();
    }

    /**
     * 得到缓存数据
     *
     * @return
     */
    private List<ShoppingCart> getLocalData() {

        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        //从本地保存中获取数据
        String saveJson = CacheUtils.getString(context, JSON_CART);//json字符串
        if (!TextUtils.isEmpty(saveJson)) {
            //直接把json数据解析成集合
            shoppingCarts = new Gson().fromJson(saveJson, new TypeToken<List<ShoppingCart>>() {
            }.getType());
        }

        return shoppingCarts;
    }

    public void addData(ShoppingCart cart) {

        //1.增加数据
        ShoppingCart tempCart = sparseArray.get(cart.getId());//判断是否在列表中存在
        if (tempCart != null) {
            //在列表中存在
            tempCart.setCount(tempCart.getCount() + 1);
            tempCart.setName(cart.getName());//内存中的name肯定也会有值了
        } else {
            tempCart = cart;
            tempCart.setCount(1);
        }

        //保存到sparseArray
        sparseArray.put(tempCart.getId(), tempCart);

        //2.保存到本地
        commit();
    }

    /**
     * 把List列表数据-->json文本数据-->保持到本地
     */
    private void commit() {

        List<ShoppingCart> shoppingCarts = sparseToList();
        String json = new Gson().toJson(shoppingCarts);
        CacheUtils.putString(context, JSON_CART, json);

    }

    private List<ShoppingCart> sparseToList() {

        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        for (int i = 0; i < sparseArray.size(); i++) {
            ShoppingCart shoppingCart = sparseArray.valueAt(i);
            shoppingCarts.add(shoppingCart);
        }

        return shoppingCarts;
    }

    /**
     * 删除数据
     *
     * @param cart
     */
    public void deleteData(ShoppingCart cart) {
        //1.删除数据
        sparseArray.delete(cart.getId());

        //2.保存到本地
        commit();
    }

    /**
     * 修改数据
     *
     * @param cart
     */
    public void update(ShoppingCart cart) {
        //1.修改数据
        sparseArray.put(cart.getId(), cart);

        //2.保存到本地
        commit();
    }

    /**
     * 把Wares转换成ShoppingCart
     *
     * @param wares
     * @return
     */
    public ShoppingCart convertion(ShoppingPagerBean.Wares wares) {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(wares.getId());
        shoppingCart.setCount(1);
        shoppingCart.setCheck(true);
        shoppingCart.setDescription(wares.getDescription());
        shoppingCart.setImgUrl(wares.getImgUrl());
        shoppingCart.setPrice(wares.getPrice());
        shoppingCart.setSale(wares.getSale());
        shoppingCart.setName(wares.getName());

        return shoppingCart;
    }
}
