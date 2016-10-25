package com.atguigu.beijingnews.pager;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atguigu.beijingnews.MainActivity;
import com.atguigu.beijingnews.base.BasePager;
import com.atguigu.beijingnews.base.MenuDetailBasePager;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.atguigu.beijingnews.fragment.LeftMenuFragment;
import com.atguigu.beijingnews.pager.detail_pager.InteractDeatailPager;
import com.atguigu.beijingnews.pager.detail_pager.NewsDeatailPager;
import com.atguigu.beijingnews.pager.detail_pager.PhotosDeatailPager;
import com.atguigu.beijingnews.pager.detail_pager.TopicDeatailPager;
import com.atguigu.beijingnews.pager.detail_pager.VoteDeatailPager;
import com.atguigu.beijingnews.utils.CacheUtils;
import com.atguigu.beijingnews.utils.Constants;
import com.atguigu.beijingnews.utils.LogUtil;
import com.atguigu.beijingnews.volley.VolleyManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinpengfei on 2016/10/17.
 * 微信:18091383534
 * Function :新闻页面
 */

public class NewsCenterPager extends BasePager {

    /**
     * 左侧菜单对应的数据
     */
//    private List<NewsCenterPagerBean.DataBean> leftdata;
    private List<NewsCenterPagerBean2.NewsCenterPagerData> leftdata;

    /**
     * 左侧菜单对应的详情页面集合
     */
    private ArrayList<MenuDetailBasePager> detailBasePagers;

    //开始时间
    private long startTime;

    public NewsCenterPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();

        //显示菜单按钮
        ib_menu.setVisibility(View.VISIBLE);

        LogUtil.e("新闻页面数据加载了....");
        //设置标题
        tv_title.setText("新闻");

//        //创建子页面的视图
//        TextView textView = new TextView(context);
//        textView.setTextSize(30);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.RED);
//        textView.setText("新闻页面");

        //子页面的视图和FrameLayout结合在一起，形成一个新的页面
//        fl_base_content.addView(textView);

        //Constants.BASE_URL +
        String saveJson = CacheUtils.getString(context, Constants.NEWS_CENTER_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }

        startTime = SystemClock.uptimeMillis();
        //联网请求
//        getDataFromNet();
        getDataFromNetByVolley();

    }

    /**
     * 使用Volley请求网络数据
     */
    private void getDataFromNetByVolley() {

        //请求队列
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.NEWS_CENTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                long endTime = SystemClock.uptimeMillis();

                long passTime = endTime - startTime;
                Log.e("TAG", "Volley -passTime==" + passTime);
                LogUtil.e("Volley联网成功==" + result);
                //数据保存起来
                CacheUtils.putString(context, Constants.NEWS_CENTER_URL, result);
                processData(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("Volley联网失败==" + volleyError.getMessage());
            }
        }) {//重新配置编码

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException var4) {
                    parsed = new String(response.data);
                }

                return super.parseNetworkResponse(response);
            }
        };

        //把StringRequest添加到队列中
//        queue.add(request);
        VolleyManager.getRequestQueue().add(request);
    }

    /**
     * 联网请求获取数据
     */
    private void getDataFromNet() {

        RequestParams params = new RequestParams(Constants.NEWS_CENTER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime;
                Log.e("TAG", "Volley -passTime==" + passTime);

                LogUtil.e("联网成功==" + result);

                //数据保存起来
                CacheUtils.putString(context, Constants.NEWS_CENTER_URL, result);

                //解析和显示数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
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

    /**
     * 解析和显示数据
     *
     * @param json
     */
    private void processData(String json) {

//        NewsCenterPagerBean bean = parseJson(json);
//        NewsCenterPagerBean2 bean2 = parseJson2(json);
        NewsCenterPagerBean2 bean2 = parseJson3(json);
        LogUtil.e("解析成功了==" + bean2.getData().get(0).getChildren().get(1).getTitle());

        //把解析好的数据传递给左侧菜单
        leftdata = bean2.getData();

        MainActivity mainActivity = (MainActivity) context;

        //创建页面集合
        detailBasePagers = new ArrayList<>();
        detailBasePagers.add(new NewsDeatailPager(context, leftdata.get(0)));//新闻详情页面
        detailBasePagers.add(new TopicDeatailPager(context, leftdata.get(0)));//专题详情页面
        detailBasePagers.add(new PhotosDeatailPager(context,leftdata.get(2)));//组图详情页面
        detailBasePagers.add(new InteractDeatailPager(context,leftdata.get(2)));//互动详情页面
        detailBasePagers.add(new VoteDeatailPager(context));//投票详情页面

        //得到左侧菜单的实例
        LeftMenuFragment leftMenuFragment = mainActivity.getLeftMenuFragment();
        //设置数据
        leftMenuFragment.setData(leftdata);

//        switchPager(0);
    }

    /**
     * 手动解析json数据
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean2 parseJson3(String json) {

        NewsCenterPagerBean2 bean2 = new NewsCenterPagerBean2();

        try {
            JSONObject jsonObject = new JSONObject(json);
            int retcode = jsonObject.optInt("retcode");
            bean2.setRetcode(retcode);
            JSONArray jsonArrayData = jsonObject.optJSONArray("data");

            if (jsonArrayData != null) {
                //创建集合数据
                List<NewsCenterPagerBean2.NewsCenterPagerData> data = new ArrayList<>();
                //把集合关联到Bean对象中
                bean2.setData(data);

                for (int i = 0; i < jsonArrayData.length(); i++) {

                    JSONObject itemData = (JSONObject) jsonArrayData.get(i);
                    if (itemData != null) {

                        NewsCenterPagerBean2.NewsCenterPagerData newsCenterPagerData = new NewsCenterPagerBean2.NewsCenterPagerData();
                        int id = itemData.optInt("id");
                        newsCenterPagerData.setId(id);
                        int type = itemData.optInt("type");
                        newsCenterPagerData.setType(type);
                        String title = itemData.optString("title");
                        newsCenterPagerData.setTitle(title);
                        String url = itemData.optString("url");
                        newsCenterPagerData.setUrl(url);
                        String url1 = itemData.optString("url1");
                        newsCenterPagerData.setUrl1(url1);
                        String dayurl = itemData.optString("dayurl");
                        newsCenterPagerData.setDayurl(dayurl);
                        String excurl = itemData.optString("excurl");
                        newsCenterPagerData.setExcurl(excurl);
                        String weekurl = itemData.optString("weekurl");
                        newsCenterPagerData.setWeekurl(weekurl);

                        JSONArray childrenjsonArray = itemData.optJSONArray("children");
                        if (childrenjsonArray != null) {
                            List<NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData> children = new ArrayList<>();
                            //设置children的数据
                            newsCenterPagerData.setChildren(children);

                            for (int j = 0; j < childrenjsonArray.length(); j++) {

                                JSONObject childrenItemData = (JSONObject) childrenjsonArray.get(j);

                                if (childrenItemData != null) {

                                    NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData = new NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData();

                                    //添加到集合中
                                    children.add(childrenData);

                                    //添加数据
                                    childrenData.setId(childrenItemData.optInt("id"));
                                    childrenData.setType(childrenItemData.optInt("type"));
                                    childrenData.setTitle(childrenItemData.optString("title"));
                                    childrenData.setUrl(childrenItemData.optString("url"));
                                }

                            }
                        }

                        //把数据添加到集合中
                        data.add(newsCenterPagerData);

                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bean2;
    }

    /**
     * 使用gson解析json数据(自己手写的实体类)
     *
     * @param json
     * @return
     */
//    private NewsCenterPagerBean2 parseJson2(String json) {
//        return new Gson().fromJson(json, NewsCenterPagerBean2.class);
//    }

    /**
     * 根据位置切换到不同的详情页面
     *
     * @param position
     */
    public void switchPager(int position) {
        //1.改变标题
        tv_title.setText(leftdata.get(position).getTitle());
        //2.改变内容
        MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
        //每个详情页面对应的视图
        View rootView = detailBasePager.rootView;
        detailBasePager.initData();//初始化数据
        //把之前的移除
        fl_base_content.removeAllViews();
        //添加到FrameLayout
        fl_base_content.addView(rootView);

        if (position == 2) {
            ib_swich_list_grid.setVisibility(View.VISIBLE);
            ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PhotosDeatailPager photosDeatailPager = (PhotosDeatailPager) detailBasePagers.get(2);
                    photosDeatailPager.switchListAndGrid(ib_swich_list_grid);

                }
            });
        } else {
            ib_swich_list_grid.setVisibility(View.GONE);
        }
    }

    /**
     * 解析json数据：使用第三方框架（Gson,Fastjson）和使用系统的API解析
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean parseJson(String json) {
        return new Gson().fromJson(json, NewsCenterPagerBean.class);
    }
}
