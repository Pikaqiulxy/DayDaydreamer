package com.example.daydreamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class DeletActivity extends AppCompatActivity{

    private static final String TAG = "daydreamer";
    String tti,suid;
    PreparedStatement ps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delet);

        //bundle传值
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        tti = bundle.getString("time", "hello");

        //头像展示
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        suid = sharedPreferences.getString("uid","001");

        final Thread thread = new Thread(new Runnable() {  //连接数据库
            @Override
            public void run() {
                //1.判断输入值是否为空
                // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                String ip = "39.97.166.131";
                int port = 3306;
                String dbName = "daydb";
                String url = "jdbc:mysql://" + ip + ":" + port
                        + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8"; // 构建连接mysql的字符串
                String user = "daydreamer";
                String password = "123456";

                // 3.连接JDBC
                Connection conn = null;
                try {
                    conn = getConnection(url, user, password);
                    Log.i(TAG, "远程连接成功!login");
                } catch (SQLException e) {
                    Log.e(TAG, "远程连接失败!");
                }

                if (conn != null) {
                    try {

                        //查询语句
                        String sql = "delete from works_d where time1='"+tti+"' and uid = '"+suid+"'";
                        // 创建用来执行sql语句的对象
                        ps = conn.prepareStatement(sql);
                        // 执行sql查询语句并获取查询信息
                        ps.executeUpdate();

                        conn.close();

                        //页面跳转
                        Intent intent = new Intent();
                        intent.setClass(DeletActivity.this, MainActivity.class);
                        startActivity(intent);

                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.e(TAG, "关闭连接失败");
                    }
                }
            }
        });
        thread.start();
        }
}