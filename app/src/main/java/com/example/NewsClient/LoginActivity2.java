package com.example.NewsClient;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity2 extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button login;
    private Button zc;
    private MyHelper helper;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        zc = findViewById(R.id.zc);
        helper=new MyHelper(LoginActivity2.this,"dictionary.db",null,1);
        String un= username.getText().toString().trim();
        String psd = password.getText().toString().trim();

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sqLiteDatabase=helper.getReadableDatabase();
                Cursor cursor=sqLiteDatabase.query("dict",null,"username=?",new String[]{un},null,null,null);
                String ss = null;
                while(cursor.moveToNext()){
                    ss=cursor.getString(2);
                }
                if(psd.equals(ss)){
                    Toast.makeText(LoginActivity2.this,"登陆成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity2.this,"输入的账号或密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(un.equals(null)||psd.equals(null)){
                    Toast.makeText(LoginActivity2.this,"输入的账号或密码为空",Toast.LENGTH_SHORT).show();
                }else {
                    sqLiteDatabase=helper.getReadableDatabase();
                    ContentValues contentValues=new ContentValues();

                    Cursor cursor=sqLiteDatabase.query("dict",null,"username=?",new String[]{un},null,null,null);
                    String ss = null;
                    while(cursor.moveToNext()){
                        ss=cursor.getString(2);
                    }
                    if(ss.equals(null)){
                        contentValues.put("username",un);
                        contentValues.put("password",psd);
                        sqLiteDatabase.insert("dict",null,contentValues);
                        Toast.makeText(LoginActivity2.this,"注册成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity2.this,"账号已注册",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}