package com.example.daydreamer;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.*;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "daydreamer";
    EditText pw;
    EditText uid;
    Button login;
    String suid,spw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏以及状态栏  todo   这里一定要注意 这些操作要在setContentView方法前操作
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        pw = findViewById(R.id.pw);
        uid = findViewById(R.id.uid);
        login = (Button)findViewById(R.id.login);

        final TextView goregister = findViewById(R.id.goregister);

        final Thread thread = new Thread(new Runnable() {  //连接数据库
            @Override
            public void run() {
                //1.判断输入值是否为空
                // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                String ip = "39.97.166.131";
                int port = 3306;
                String dbName = "daydb";
                String url = "jdbc:mysql://" + ip + ":" + port
                        + "/" + dbName; // 构建连接mysql的字符串
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
                        // 创建用来执行sql语句的对象
                        java.sql.Statement statement = conn.createStatement();
                        //查询语句
                        String sql = "select pw from users_d where uid = '"+suid+"'";
                        // 执行sql查询语句并获取查询信息
                        ResultSet rSet = statement.executeQuery(sql);
                        //判断结果是否为空（判断是否匹配）
                        if(rSet!=null){
                            // 迭代打印出查询信息
                            while (rSet.next()) {
                                List<String> list = new ArrayList<String>();//创建取结果的列表，之所以使用列表，不用数组，因为现在还不知道结果有多少，不能确定数组长度，所有先用list接收，然后转为数组
                                list.add(rSet.getString("pw"));
                                if (list != null && list.size() > 0) {//如果list中存入了数据，转化为数组
                                    String[] arr = new String[list.size()];//创建一个和list长度一样的数组
                                    for (int i = 0; i < list.size(); i++) {
                                        arr[i] = list.get(i);//数组赋值了。
                                        if(arr[i].equals(spw)){
                                            //将id存到xml文件里面
                                            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                                            sharedPreferences.getString("uid", null);
                                            Log.i(TAG, "xml创建");
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("uid",suid);
                                            editor.commit();
                                            Log.i(TAG, "xml数据已保存到sharedPreferences");
                                            //页面跳转
                                            Intent intent = new Intent();
                                            intent.setClass(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }else Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
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
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spw = pw.getText().toString();
                suid = uid.getText().toString();

                if(!suid.equals("")){
                    if(!spw.equals("")){
                        thread.start();
                    }else Toast.makeText(LoginActivity.this,"Login Failed Cause PW",Toast.LENGTH_LONG).show();
                }else Toast.makeText(LoginActivity.this,"Login Failed Cause ID",Toast.LENGTH_LONG).show();
            }
        });

        goregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}