package com.example.NewsClient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    private Button zc;
    private Register helper;
    private SQLiteDatabase sqLiteDatabase;
    String un = "";
    String psd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        zc = findViewById(R.id.zc);
        helper=new Register(LoginActivity.this,"dictionary.db",null,1);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                un= username.getText().toString().trim();
                psd = password.getText().toString().trim();
                sqLiteDatabase=helper.getReadableDatabase();
                Cursor cursor=sqLiteDatabase.query("dict",null,"username=?",new String[]{un},null,null,null);
                String ss = null;
                while(cursor.moveToNext()){
                    ss=cursor.getString(2);
                }
                if(psd.equals(ss)){
                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    ToIntent();
                }else{
                    Toast.makeText(LoginActivity.this,"输入的账号或密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                un= username.getText().toString().trim();
                psd = password.getText().toString().trim();
                if(un.equals("")||psd.equals("")){
                    Toast.makeText(LoginActivity.this,"输入的账号或密码为空",Toast.LENGTH_SHORT).show();
                }else {
                    sqLiteDatabase=helper.getReadableDatabase();
                    ContentValues contentValues=new ContentValues();
                    Cursor cursor=sqLiteDatabase.query("dict",null,"username=?",new String[]{un},null,null,null);
                    String ss = "";
                    while(cursor.moveToNext()){
                        ss=cursor.getString(2);
                    }
                    if(ss.equals("")){
                        contentValues.put("username",un);
                        contentValues.put("password",psd);
                        sqLiteDatabase.insert("dict",null,contentValues);
                        Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this,"该账号已注册",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void ToIntent(){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        //LoginActivity.this.finish();
    }
}