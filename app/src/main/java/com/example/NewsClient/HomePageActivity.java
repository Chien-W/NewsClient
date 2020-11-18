package com.example.NewsClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HomePageActivity extends Activity {
    private DrawerLayout drawerlayout;
    private ListView listView;
    private ImageButton imagebutton;
    private String[] name={"社会新闻","国内新闻","国际新闻","娱乐新闻","体育新闻","科技新闻","军事新闻"};
    private int[] images={R.drawable.sh,R.drawable.gn,R.drawable.gj,R.drawable.yl,R.drawable.ty,R.drawable.kj,R.drawable.js};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        listView = findViewById(R.id.myListView);
        imagebutton = findViewById(R.id.btntop);
        drawerlayout = findViewById(R.id.drawer_layout);
        MyAdapter myAdapter=new MyAdapter();
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch(position){
                    case 0:
                }
            }
        });
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(Gravity.LEFT);
            }
        }
        );

    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return name.length;
        }

        public MyAdapter() {
            super();
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
            View view=View.inflate(HomePageActivity.this,R.layout.fg_content,null);
            ImageView image=view.findViewById(R.id.item_image);
            TextView text=view.findViewById(R.id.item_text);
            image.setBackgroundResource(images[position]);
            text.setText(name[position]);
            return view;
        }
    }

}