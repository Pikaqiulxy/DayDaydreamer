package com.example.daydreamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class DetailsActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "daydreamer";

    String suid,siid,stitle,str,spt,part,intro1,link,img1,img2;
    ImageView iid;
    PreparedStatement ps = null;
    ResultSet rSet=null;
    Handler handler;
    TextView title,spt1,intro,link1;
    ImageView image1,image2;
    Button dele;
    ImageButton de;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        iid = findViewById(R.id.iid);
        title = findViewById(R.id.title);
        spt1 = findViewById(R.id.spt);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        intro = findViewById(R.id.intro);
        link1 = findViewById(R.id.link);
        dele = findViewById(R.id.dele);
        de = findViewById(R.id.re);

        //头像展示
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        suid = sharedPreferences.getString("uid","001");
        siid = sharedPreferences.getString("iid","001");
        Log.i(TAG, "详情xmlreadde"+siid);
        //base64转化为bitmap显示
        Bitmap b = base64ToPicture(siid);
        iid.setImageBitmap(b);
        //bundle传值
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        stitle = bundle.getString("text1", "hello");
        str = bundle.getString("text2", "hello");
        String str1=str.substring(0, str.indexOf("·"));
        spt=str.substring(str1.length()+1, str.length());
        Log.i(TAG, "详情xmlread2"+spt);
        Thread t = new Thread(DetailsActivity.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {

                    Log.i(TAG,"得到！！！！！read");
                    title.setText(stitle);
                    Log.i(TAG,"得到！！！！！read");
                    spt1.setText(str);
                    Log.i(TAG,"得到！！！！！read");
                    //base64转化为bitmap显示
                    Bitmap b2 = base64ToPicture(img1);
                    image1.setImageBitmap(b2);
                    intro.setText(intro1);
                    Log.i(TAG,"得到！！！！！read");
                    //base64转化为bitmap显示
                    Bitmap b1 = base64ToPicture(img2);
                    image2.setImageBitmap(b1);
                    Log.i(TAG,"得到！！！！！read");
                    link1.setText(link);
                }
                super.handleMessage(msg);
            }
        };

        dele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //使用Bundle一体化的传输数据
                Bundle bundle = new Bundle();
                bundle.putString("time", spt);

                Intent intent = new Intent();
                intent.setClass(DetailsActivity.this, DeletActivity.class);
                startActivity(intent);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        iid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(DetailsActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    public void run(){
        //1.判断输入值是否为空
        // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
        String ip = "39.97.166.131";
        int port = 3306;
        String dbName = "daydb";
        String url = "jdbc:mysql://" + ip + ":" + port
                + "/" + dbName+"?useUnicode=true&characterEncoding=UTF-8"; // 构建连接mysql的字符串
        String user = "daydreamer";
        String password = "123456";

        // 3.连接JDBC
        Connection conn = null;
        try {
            conn = getConnection(url, user, password);
            Log.i(TAG, "远程连接成功!de");
        } catch (SQLException e) {
            Log.e(TAG, "远程连接失败!");
        }

        if (conn != null) {
            try {
                //查询语句
                String sql = "select * from works_d where uid = '"+suid+"' and time1='"+spt+"'";
                // 创建用来执行sql语句的对象
                ps = conn.prepareStatement(sql);
                // 执行sql查询语句并获取查询信息
                rSet = ps.executeQuery();
                Log.i(TAG, "xml创建sql");
                //判断结果是否为空（判断是否匹配）
                if(rSet.next()){
                    // 得到对象
                    part = rSet.getString(2);
                    Log.i(TAG,"得到！！！"+part);
                    intro1 = rSet.getString(4);
                    Blob b5 = rSet.getBlob(5);//得到Blob对象(iid)
                    Blob b6 = rSet.getBlob(6);
                    link = rSet.getString(7);
                    //blob转string
                    img1 = new String(b5.getBytes(1, (int) b5.length()),"GBK");//blob 转 String
                    Log.i(TAG,"得到！！！"+img1);
                    img2 = new String(b6.getBytes(1, (int) b6.length()),"GBK");//blob 转 String
                }
            } catch (SQLException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "关闭连接失败");
        }
        Message msg = handler.obtainMessage(7);
        handler.sendMessage(msg);
    }

    //base64转bitmap显示
    public Bitmap base64ToPicture(String imgBase64) {
        byte[] decode = Base64.decode(imgBase64, Base64.DEFAULT);
        Bitmap bitma = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitma;
    }
}