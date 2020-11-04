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
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.getConnection;

public class UserActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "daydreamer";
    String suid,siid,sname,snote;
    ImageView iid,loading;
    ImageButton re;
    PreparedStatement ps = null;
    ResultSet rSet=null;
    TextView name,uid,note,nicheng,zhanghao,jizhu,jianjie;
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        re = findViewById(R.id.re);
        iid = findViewById(R.id.iid);
        name = findViewById(R.id.name);
        uid = findViewById(R.id.uid);
        note = findViewById(R.id.note);
        nicheng = findViewById(R.id.nicheng);
        zhanghao = findViewById(R.id.zhanghao);
        jizhu = findViewById(R.id.jizhu);
        jianjie = findViewById(R.id.jianjie);
        loading = findViewById(R.id.loading);

        //头像展示
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        suid = sharedPreferences.getString("uid","001");
        siid = sharedPreferences.getString("iid","001");
        Log.i(TAG, "xmlreaduser"+siid);
        //base64转化为bitmap显示
        Bitmap b = base64ToPicture(siid);
        iid.setImageBitmap(b);

        //loading动画
        ObjectAnimator rotation = ObjectAnimator.ofFloat(loading, "rotation", 0f, 1400f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(rotation);      //动画控制

        animSet.setDuration(9000);    //动画持续时间
        animSet.start();

        Thread t = new Thread(UserActivity.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {
                    //将动画设为不可见
                    loading.setVisibility(View.GONE);
                    //写入页面
                    Log.i(TAG,"得到！！！！！"+snote);
                    name.setText(sname);
                    uid.setText(suid);
                    note.setText(snote);
                    nicheng.setText("昵称");
                    zhanghao.setText("账号");
                    jizhu.setText("（请记住您的账号)");
                    jianjie.setText("简介");
                }
                super.handleMessage(msg);
            }
        };

        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void run() {
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
            Log.i(TAG, "远程连接成功!user");
        } catch (SQLException e) {
            Log.e(TAG, "远程连接失败!");
        }

        if (conn != null) {
            try {
                //查询语句
                String sql = "select * from users_d where uid = '"+suid+"'";
                // 创建用来执行sql语句的对象
                ps = conn.prepareStatement(sql);
                // 执行sql查询语句并获取查询信息
                rSet = ps.executeQuery();
                Log.i(TAG, "xml创建sqluser");
                //判断结果是否为空（判断是否匹配）
                if(rSet.next()){
                    // 得到对象
                    sname = rSet.getString(2);
                    snote = rSet.getString(5);
                    Log.i(TAG, "xml得到sqluser："+sname);
                    Log.i(TAG, "xml得到sqluser："+snote);

                }
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