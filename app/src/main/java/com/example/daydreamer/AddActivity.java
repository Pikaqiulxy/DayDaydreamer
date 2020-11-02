package com.example.daydreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "daydreamer";
    ImageButton re;
    ImageView iid;
    EditText cla;
    EditText title;
    EditText intro;
    Button add1,add2;
    EditText link;
    Button submit;
    String suid,siid,scla,stitle,sintro,slink,sadd1,sadd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        re = findViewById(R.id.re);
        iid = (ImageView)findViewById(R.id.iid);
        cla = findViewById(R.id.cla);
        title = findViewById(R.id.title);
        intro = findViewById(R.id.intro);
        add1 = findViewById(R.id.add1);
        add2 = findViewById(R.id.add2);
        link = findViewById(R.id.link);
        submit = findViewById(R.id.submit);

        //头像展示
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        suid = sharedPreferences.getString("uid","001");
        siid = sharedPreferences.getString("iid","001");
        Log.i(TAG, "xmlread"+siid);
        //base64转化为bitmap显示
        Bitmap b = base64ToPicture(siid);
        iid.setImageBitmap(b);


        final Thread thread = new Thread(new Runnable() {  //连接数据库
            @Override
            public void run() {
                //1.获取当前时间
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                String da = simpleDateFormat.format(date);
                // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                String ip = "39.97.166.131";
                int port = 3306;
                String dbName = "daydb";
                String url = "jdbc:mysql://" + ip + ":" + port
                        + "/" + dbName+"?useUnicode=true&characterEncoding=utf8"; // 构建连接mysql的字符串
                String user = "daydreamer";
                String password = "123456";

                // 3.连接JDBC
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(url, user, password);
                    Log.i(TAG, "远程连接成功!");
                } catch (SQLException e) {
                    Log.e(TAG, "远程连接失败!");
                }

                if (conn != null) {
                    try {
                        // 创建用来执行sql语句的对象
                        java.sql.Statement statement = conn.createStatement();
                        //输入语句
                        String sql1 = "insert into works_d(uid,part,title,intro,img_1,img_2,link,time1) " +
                                "values('"+suid+"','"+scla+"','"+stitle+"','"+sintro+"','"+sadd1+"','"+sadd2+"','"+slink+"','"+da+"')";//数据库语句
                        //执行sqlINSERT、UPDATE 或 DELETE 语句
                        statement.executeUpdate(sql1);
                        //页面跳转
                        Intent intent = new Intent();
                        intent.setClass(AddActivity.this, MainActivity.class);
                        startActivity(intent);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "关闭连接失败");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scla = cla.getText().toString();
                stitle = title.getText().toString();
                sintro = intro.getText().toString();
                slink = link.getText().toString();
                thread.start();
            }
        });

        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });

        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                //iid.setImageURI(uri);
                //将uri转string格式，方便存入数据库
                sadd1 = uri.toString();
                //Uri uri1 = Uri.parse((String) siid);
                //iid.setImageURI(uri1);
            }
        }
        if (requestCode == 2) {
            if (data != null) {
                Uri uri = data.getData();
                sadd2 = uri.toString();
            }
        }
    }

    //base64转bitmap显示
    public Bitmap base64ToPicture(String imgBase64) {
        byte[] decode = Base64.decode(imgBase64, Base64.DEFAULT);
        Bitmap bitma = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitma;
    }
}