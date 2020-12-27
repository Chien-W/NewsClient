package com.example.NewsClient.picture;
/**
 * @description: 从相册中选取图片更改头像
 * @author Chien_W
 * @date 2020/12/25 13:14
 * @version 1.0
 */
import android.Manifest;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.NewsClient.HomePageActivity;
import com.example.NewsClient.R;

public class Photo extends HomePageActivity {
    public ImageView imageView;
    public static final int CHOOSE_PHOTO = 2;
    String imagePath = null;
    int flag=0;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        requestPermissino();
    }

    public void requestPermissino() {
            imageView = findViewById(R.id.icon_image1);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }else {
                openAlbum();
            }
        }

    public void openAlbum(){
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent,CHOOSE_PHOTO); //打开相册
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode){
                case 1:
                    if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        openAlbum();
                    }else {
                        Toast.makeText(this,"你拒绝了该权限",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                default:
                    break;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void handleImageOnKitKat(Intent data){
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
        public String getImagePath(Uri uri,String selection){
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

        @Override
        public void onResume() {
            super.onResume();
            //设置再次app时显示的图片
            SharedPreferences sp = getSharedPreferences("sp_img", MODE_PRIVATE);
            //取出上次存储的图片路径设置此次的图片展示
            String beforeImagePath = sp.getString("imgPath", null);
            displayImage(beforeImagePath);
        }
    //展示图片
    protected void displayImage(String imagePath){
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
        flag++;
        if(flag==2)
            passDate();
    }
    private void passDate() {
        Intent intent = new Intent(this,HomePageActivity.class);
        String type = "2";
        intent.putExtra("type",type);
        intent.putExtra("imageurl",imagePath);
        startActivity(intent);
    }
}