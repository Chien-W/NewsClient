package com.example.NewsClient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.NewsClient.R;


/**
 * @author yfxie
 * @version 1.0
 * @date 2020/12/27 14:48
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private String[] name = {"社会新闻", "国内新闻", "国际新闻", "娱乐新闻", "体育新闻", "科技新闻", "军事新闻", "财经新闻", "时尚新闻"};
    private int[] images = {R.drawable.sh, R.drawable.gn, R.drawable.gj, R.drawable.yl, R.drawable.ty, R.drawable.kj, R.drawable.js, R.drawable.cj, R.drawable.ss};

    public MyAdapter() {
        super();
    }

    public MyAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return name[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fg_content, null);
        }
        ImageView image = convertView.findViewById(R.id.item_image);
        TextView text = convertView.findViewById(R.id.item_text);
        image.setBackgroundResource(images[position]);
        text.setText(name[position]);
        return convertView;
    }
}
