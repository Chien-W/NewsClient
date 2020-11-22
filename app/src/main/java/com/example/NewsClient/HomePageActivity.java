package com.example.NewsClient;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.example.NewsClient.gson.Data;
import com.example.NewsClient.gson.News;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
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
    private ImageView imageView;
    public static final int CHOOSE_PHOTO = 1;
    public static final int TAKE_PHOTO = 2;//声明一个请求码，用于识别返回的结果
    private ImageView picture;
    private Uri imageUri;
    private final String filePath = Environment.getExternalStorageDirectory() + File.separator + "output_image.jpg";
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
        imageView = findViewById(R.id.icon_image1);
        picture = findViewById(R.id.icon_image1);

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

        //监听侧边栏按钮
        imagebutton.setOnClickListener(new View.OnClickListener() {//////////////////////////////////////点击后打开侧边栏
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(Gravity.LEFT);
            }
        });

        //监听头像按钮
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  requestPermissino();
                drawerlayout.closeDrawer(Gravity.LEFT);
                showBottomDialog();
                Log.d("点击成功：", "点击成功");
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
                            .url("https://www.oyyandwjw.xyz/xw.json")
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

    //动态请求权限
    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        } else {
            //调用
            requestCamera();
        }
    }

    private void requestPermissino() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            openAlbum();
        }

    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO); //打开相册
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults != null && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 1:
                    if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        openAlbum();
                    }else {
                        Toast.makeText(this,"你拒绝了该权限",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2: {
                    requestCamera();
                }
                break;
                default:
                    break;
            }
        }
    }

    private void requestCamera() {
        File outputImage = new File(filePath);
                /*
                创建一个File文件对象，用于存放摄像头拍下的图片，我们把这个图片命名为output_image.jpg
                并把它存放在应用关联缓存目录下，调用getExternalCacheDir()可以得到这个目录，为什么要
                用关联缓存目录呢？由于android6.0开始，读写sd卡列为了危险权限，使用的时候必须要有权限，
                应用关联目录则可以跳过这一步
                 */
        try//判断图片是否存在，存在则删除在创建，不存在则直接创建
        {
            if (!outputImage.getParentFile().exists()) {
                outputImage.getParentFile().mkdirs();
            }
            if (outputImage.exists()) {
                outputImage.delete();
            }

            outputImage.createNewFile();

            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.mydemo.fileprovider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
            }
            //使用隐示的Intent，系统会找到与它对应的活动，即调用摄像头，并把它存储
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
            //调用会返回结果的开启方式，返回成功的话，则把它显示出来
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT>=19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                        //将图片解析成Bitmap对象，并把它显现出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); //根据图片路径显示图片
    }

    //将选择的图片Uri转换为路径
    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!= null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //展示图片
    private void displayImage(String imagePath){
        if(imagePath!=null && !imagePath.equals("")){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
            //存储上次选择的图片路径，用以再次打开app设置图片
            SharedPreferences sp = getSharedPreferences("sp_img",MODE_PRIVATE);  //创建xml文件存储数据，name:创建的xml文件名
            SharedPreferences.Editor editor = sp.edit(); //获取edit()
            editor.putString("imgPath",imagePath);
            editor.apply();
        }else {
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置再次app时显示的图片
        SharedPreferences sp = getSharedPreferences("sp_img", MODE_PRIVATE);
        //取出上次存储的图片路径设置此次的图片展示
        String beforeImagePath = sp.getString("imgPath", null);
        displayImage(beforeImagePath);
    }

    private void showBottomDialog(){
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this,R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this,R.layout.dialog_custom_layout,null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                requestPermission();
                drawerlayout.openDrawer(Gravity.LEFT);
            }
        });

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                requestPermissino();
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

    //设置保存拍照图片——>再次关闭app重新打开显示为上次拍照照片
    private void setDefualtImage() {
        File outputImage = new File(filePath);
        if (!outputImage.exists()) {
            return;
        }
        picture.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }

}