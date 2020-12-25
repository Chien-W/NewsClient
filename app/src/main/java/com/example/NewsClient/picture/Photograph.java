package com.example.NewsClient.picture;
/**
 * @description: 使用相机拍照更改头像
 * @author Chien_W
 * @date 2020/12/25 14:52
 * @version 1.0
 */
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.example.NewsClient.HomePageActivity;
import com.example.NewsClient.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Photograph extends AppCompatActivity {
        public static final int TAKE_PHOTO = 1;//声明一个请求码，用于识别返回的结果
        private ImageView imageView;
        private Uri imageUri;
        private final String filePath = Environment.getExternalStorageDirectory() + File.separator + "output_image.jpg";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_page);
            requestPermission();  //在其中若用户给予权限则请求相机拍照
        }

        //动态请求权限
        private void requestPermission() {
            imageView = findViewById(R.id.icon_image1);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            } else {
                //调用
                requestCamera();
            }
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults != null && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {
                    case 1: {
                        requestCamera();
                    }
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

        //处理返回结果的函数，下面是隐示Intent的返回结果的处理方式，具体见以前我所发的intent讲解
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            imageView.setImageBitmap(bitmap);
                            //将图片解析成Bitmap对象，并把它显现出来
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
            passDate();
        }
    private void passDate() {
        Intent intent = new Intent(this, HomePageActivity.class);
        String type = "1";
        intent.putExtra("type",type);
        intent.setData(imageUri);
        startActivity(intent);
    }
}