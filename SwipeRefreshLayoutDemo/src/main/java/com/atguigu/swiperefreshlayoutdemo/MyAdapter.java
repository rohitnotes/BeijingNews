package com.atguigu.swiperefreshlayoutdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 作者：杨光福 on 2016/5/26 15:37
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：xxxx
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<String> datas;


    //设置点击某个item的监听
    public interface OnItemClickListener{

         void onItemClick(View view,int position,String content);
    }

    private OnItemClickListener onItemClickListener;
    /**
     * 设置某条的监听
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //设置点击图片

    //设置点击某个item的监听
    public interface OnImageViewClickListener{

        void onImageViewClick(View view,int position);
    }

    private OnImageViewClickListener onImageViewClickListener;
    /**
     * 设置监听图片
     * @param onImageViewClickListener
     */
    public void setOnImageViewClickListener(OnImageViewClickListener onImageViewClickListener) {
        this.onImageViewClickListener = onImageViewClickListener;
    }




    public MyAdapter(Context context,ArrayList<String> datas){
        this.context = context;
        this.datas = datas;
    }
    /**
     * 相当于ListView适配器中的getView的创建holder布局
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_main, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_text.setText(datas.get(position));
        holder.iv_icon.setImageResource(R.mipmap.ic_launcher);

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_text;
        private ImageView iv_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);

            //设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(v,getLayoutPosition(),datas.get(getLayoutPosition()));
                    }
                }
            });

            //设置监听
            iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onImageViewClickListener != null){
                        onImageViewClickListener.onImageViewClick(v,getLayoutPosition());
                    }
                }
            });


        }
    }

    /**
     * 添加数据
     * @param position
     * @param content
     */
    public void addData(int position,String content){
        datas.add(position,content);
        notifyItemInserted(position);


    }

    /**
     * 移除数据
     * @param position
     */
    public void removeData(int position){
        datas.remove(position);
        notifyItemRemoved(position);
    }


}
