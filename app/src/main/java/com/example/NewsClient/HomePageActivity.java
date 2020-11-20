package com.example.NewsClient;


import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.NewsClient.gson.Data;
import com.example.NewsClient.gson.News;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePageActivity extends Activity implements AdapterView.OnItemClickListener{
    private DrawerLayout drawerlayout;
    private ListView myListView;
    private ListView lvNews;
    private ImageButton imagebutton;
    private NewsAdapter newsAdapter;
    private List<Data> dataList;
    private String[] name={"社会新闻","国内新闻","国际新闻","娱乐新闻","体育新闻","科技新闻","军事新闻","财经新闻","时尚新闻"};
    private int[] images={R.drawable.sh,R.drawable.gn,R.drawable.gj,R.drawable.yl,R.drawable.ty,R.drawable.kj,R.drawable.js,R.drawable.cj,R.drawable.ss};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        myListView = findViewById(R.id.myListView);
        lvNews = findViewById(R.id.lvNews);
        imagebutton = findViewById(R.id.btntop);
        lvNews = findViewById(R.id.lvNews);
        drawerlayout = findViewById(R.id.drawer_layout);

        init("junshi");

        MyAdapter myAdapter=new MyAdapter();
        myListView.setAdapter(myAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        init("shehui");
                        break;
                    case 1:
                        init("guonei");
                        break;
                    case 2:
                        init("guoji");
                        break;
                    case 3:
                        init("yule");
                        break;
                    case 4:
                        init("tiyu");
                        break;
                    case 5:
                        init("junshi");
                        break;
                    case 6:
                        init("keji");
                        break;
                    case 7:
                        init("caijing");
                        break;
                    case 8:
                        init("shishang");
                        break;
                }
                showDrawerLayout();
            }
        });
        imagebutton.setOnClickListener(new View.OnClickListener() {//////////////////////////////////////点击后打开侧边栏
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void init(String s){
        dataList = new ArrayList<Data>();
        newsAdapter = new NewsAdapter(HomePageActivity.this, dataList);
        lvNews.setAdapter(newsAdapter);
        lvNews.setOnItemClickListener(this);
        sendRequestWithOKHttp(s);
    }


    private void showDrawerLayout() {////////////////////////////////////////////////////////////////////点击后关闭侧边栏
        if (!drawerlayout.isDrawerOpen(Gravity.LEFT)) {
            drawerlayout.openDrawer(Gravity.LEFT);
        } else {
            drawerlayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void sendRequestWithOKHttp(String part){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://v.juhe.cn/toutiao/index?type="+part+"&key=a022eb1dafc57af1e06a19a29a61456a")
                            .build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("测试：", responseData);
                    parseJsonWithGson(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJsonWithGson(String jsonData){//////////////////////////////////////////////////获取
        Gson gson = new Gson();
        News news = gson.fromJson(jsonData, News.class);
        List<Data> list = news.getResult().getData();
        for (int i=0; i<list.size(); i++){
            String uniquekey = list.get(i).getUniqueKey();
            String title = list.get(i).getTitle();
            String date = list.get(i).getDate();
            String category = list.get(i).getCategory();
            String author_name = list.get(i).getAuthorName();
            String content_url = list.get(i).getUrl();
            String pic_url = list.get(i).getThumbnail_pic_s();
            dataList.add(new Data(uniquekey, title, date, category, author_name, content_url, pic_url));
        }
        runOnUiThread(new Runnable() {/////更新Adapter(务必在主线程中更新UI!!!)
            @Override
            public void run() {
                newsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {///////////////点击查看新闻
        Data data = dataList.get(position);
        Intent intent = new Intent(this, BrowseNewsActivity.class);
        intent.putExtra("content_url", data.getUrl());
        startActivity(intent);
    }




    class MyAdapter extends BaseAdapter {////////////////////////////////////////////////////////////配置适配器
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