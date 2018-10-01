package com.atguigu.beijingnews.utils;

import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpf on 2016/10/31 :)
 * Wechat:18091383534
 * Function:手动解析json数据
 */

public class Parser {

    public static NewsCenterPagerBean2 parseJson3(String json) {

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

}
