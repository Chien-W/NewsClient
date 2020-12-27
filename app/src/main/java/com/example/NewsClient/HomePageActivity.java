package com.example.NewsClient;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.NewsClient.adapter.MyAdapter;
import com.example.NewsClient.adapter.NewsAdapter;
import com.example.NewsClient.gson.Data;
import com.example.NewsClient.gson.News;
import com.example.NewsClient.picture.Photo;
import com.example.NewsClient.picture.Photograph;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePageActivity extends Activity implements AdapterView.OnItemClickListener {
    private DrawerLayout drawerlayout;
    private ListView myListView;
    private ListView lvNews;
    private ImageButton imagebutton;
    private NewsAdapter newsAdapter;
    private MyAdapter myAdapter;
    private List<Data> dataList;
    private ImageView imageView;
    private SwipeRefreshLayout swipe;
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        myListView = findViewById(R.id.myListView);
        lvNews = findViewById(R.id.lvNews);
        imagebutton = findViewById(R.id.btntop);
        drawerlayout = findViewById(R.id.drawer_layout);
        imageView = findViewById(R.id.icon_image1);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {// 刷新
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        init(page);
                        swipe.setRefreshing(false);
                    }
                },3000);
            }
        });

        init("shehui");
        page = "shehui";

        myAdapter = new MyAdapter(HomePageActivity.this);
        myListView.setAdapter(myAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        init("shehui");
                        page = "shehui";
                        break;
                    case 1:
                        init("guonei");
                        page = "guonei";
                        break;
                    case 2:
                        init("guoji");
                        page = "guoji";
                        break;
                    case 3:
                        init("yule");
                        page = "yule";
                        break;
                    case 4:
                        init("tiyu");
                        page = "tiyu";
                        break;
                    case 5:
                        init("keji");
                        page = "keji";
                        break;
                    case 6:
                        init("junshi");
                        page = "junshi";
                        break;
                    case 7:
                        init("caijing");
                        page = "caijing";
                        break;
                    case 8:
                        init("shishang");
                        page = "shishang";
                        break;
                }
                showDrawerLayout();
            }
        });

        //监听侧边栏按钮
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(Gravity.LEFT);
            }
        });

        //监听头像按钮
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.closeDrawer(Gravity.LEFT);
                showBottomDialog();
                Log.d("点击成功：", "点击成功");
            }
        });

        //从Photo中获取头像url并对头像进行替换
        String imageurl = null;
        Intent intent = getIntent();
        int type = 0;
        String Type = null;
        Type = intent.getStringExtra("type");
        if(Type!=null)
            type = Integer.parseInt(Type);
        if(type == 1){
            Log.i("选择拍照更换头像","选择拍照更换头像");
            Uri imageUri = getIntent().getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(type == 2){
            Log.i("选择文件更换头像","选择文件更换头像");
            imageurl = intent.getStringExtra("imageurl");
            if(imageurl!=null && !imageurl.equals("")){
                Bitmap bitmap = BitmapFactory.decodeFile(imageurl);
                imageView.setImageBitmap(bitmap);
                //存储上次选择的图片路径，用以再次打开app设置图片
                SharedPreferences sp = getSharedPreferences("sp_img",MODE_PRIVATE);  // 创建xml文件存储数据，name:创建的xml文件名
                SharedPreferences.Editor editor = sp.edit(); //获取edit()
                editor.putString("imgPath",imageurl);
                editor.apply();
            }
        }

    }


    private void init(String s) {
        dataList = new ArrayList<>();
        newsAdapter = new NewsAdapter(HomePageActivity.this, dataList);
        lvNews.setAdapter(newsAdapter);
        lvNews.setOnItemClickListener(this);
        sendRequestWithOKHttp(s);
    }
//    private void initd(String s) {
//        List<Data> list = new ArrayList<>();
//        newsAdapter.setList(list);
//        lvNews.setOnItemClickListener(this);
//        sendRequestWithOKHttp(s);
//    }

    private void showDrawerLayout() {// 点击后关闭侧边栏
        if (!drawerlayout.isDrawerOpen(Gravity.LEFT)) {
            drawerlayout.openDrawer(Gravity.LEFT);
        } else {
            drawerlayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void sendRequestWithOKHttp(String part) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://v.juhe.cn/toutiao/index?type=" + part + "&key=a022eb1dafc57af1e06a19a29a61456a")
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

    private void parseJsonWithGson(String jsonData) {// 获取
        Gson gson = new Gson();
        News news = gson.fromJson(jsonData, News.class);
        List<Data> list = news.getResult().getData();
        for (int i = 0; i < list.size(); i++) {
            String uniquekey = list.get(i).getUniqueKey();
            String title = list.get(i).getTitle();
            String date = list.get(i).getDate();
            String category = list.get(i).getCategory();
            String author_name = list.get(i).getAuthorName();
            String content_url = list.get(i).getUrl();
            String pic_url = list.get(i).getThumbnail_pic_s();
            dataList.add(new Data(uniquekey, title, date, category, author_name, content_url, pic_url));
        }
        runOnUiThread(new Runnable() {// 更新Adapter(务必在主线程中更新UI!!!)
            @Override
            public void run() {
                Log.d("test：", "刷新页面");
                newsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {// 点击查看新闻
        Data data = dataList.get(position);
        Intent intent = new Intent(this, BrowseNewsActivity.class);
        intent.putExtra("content_url", data.getUrl());
        System.out.println(data.getUrl());
        startActivity(intent);
    }


    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog_custom_layout, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ToPhogograph();
                drawerlayout.openDrawer(Gravity.LEFT);
            }
        });

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ToPhoto();
                drawerlayout.openDrawer(Gravity.LEFT);
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void ToPhoto() {
        Intent tophoto = new Intent(this,Photo.class);
        startActivity(tophoto);
    }

    private void ToPhogograph() {
        Intent tophotograph = new Intent(this, Photograph.class);
        startActivity(tophotograph);
    }
}
