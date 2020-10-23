package com.example.daydreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daydreamer.ui.login.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    ImageView iid;
    TextView textView;
    EditText name;
    EditText uid;
    EditText pw;
    EditText pw2;
    EditText note;
    Button rbutton;
    String sname,suid,spw,spw2,snote;
    int iii=0 ,jjj=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏以及状态栏  todo   这里一定要注意 这些操作要在setContentView方法前操作
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register);

        iid = findViewById(R.id.iid);
        textView = findViewById(R.id.textView);
        name = findViewById(R.id.name);
        uid = findViewById(R.id.uid);
        pw = findViewById(R.id.pw);
        pw2 = findViewById(R.id.pw2);
        note = findViewById(R.id.note);
        rbutton = findViewById(R.id.rbutton);

        sname = name.getText().toString();
        suid = uid.getText().toString();
        spw = pw.getText().toString();
        spw2 = pw2.getText().toString();
        snote = note.getText().toString();


        final TextView gologin = findViewById(R.id.gologin);

        rbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sname != null){
                    if(suid != null){
                        if(snote != null){
                            if(spw !=null){
                                if(spw.equals(spw2)){
                                    iii = 1;
                                }else textView.setText("The password input is inconsistent");
                            }else textView.setText("pleaseSetYourPassword");
                        }else textView.setText("PleaseSetYourIntroduction");
                    }else textView.setText("PleaseSetYourOnlyId");
                }else textView.setText("PleaseSetYourName");


                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

            final Thread thread = new Thread(new Runnable() {  //连接数据库
                @Override
                public void run() {
                    if(iii == 1){
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
                        conn = DriverManager.getConnection(url, user, password);
                        Log.i(TAG, "远程连接成功!");
                    } catch (SQLException e) {
                        Log.e(TAG, "远程连接失败!");
                    }

                    if (conn != null) {
                        //查询语句
                        String sql = "select uid from users_d";

                        try {
                            // 创建用来执行sql语句的对象
                            java.sql.Statement statement = conn.createStatement();
                            //执行sqlINSERT、UPDATE 或 DELETE 语句
                            //statement.executeUpdate(sql1);
                            // 执行sql查询语句并获取查询信息
                            ResultSet rSet = statement.executeQuery(sql);
                            // 迭代打印出查询信息

                            while (rSet.next()) {
                                //Log.i(TAG, rSet.getString("iid") + "输出" + rSet.getString("name") + "\t");
                                List<String> list = new ArrayList<String>();//创建取结果的列表，之所以使用列表，不用数组，因为现在还不知道结果有多少，不能确定数组长度，所有先用list接收，然后转为数组
                                list.add(rSet.getString(1));
                                if (list != null && list.size() > 0) {//如果list中存入了数据，转化为数组
                                    String[] arr = new String[list.size()];//创建一个和list长度一样的数组
                                    for (int i = 0; i < list.size(); i++) {
                                        arr[i] = list.get(i);//数组赋值了。
                                    }
                                    //遍历比较uid是否唯一
                                    for (int i = 0; i < arr.length; i++) {
                                        if (arr[i].equals(spw)) {
                                            textView.setText("This ID is already in use");
                                            jjj = 1;
                                            break;
                                        }
                                    }

                                }
                            }
                            if(jjj == 0){
                                //输入语句
                                String sql1 = "insert into users_d(iid,name,uid,pw,note) values(NULL,'"+sname+"','"+suid+"','"+spw+"','"+snote+"')";//数据库语句
                            }
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.e(TAG, "关闭连接失败");
                        }
                    }
                    }
                }
            });
            thread.start();


        gologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}