package com.example.daydreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "daydreamer";
    ImageView iid;
    EditText name;
    EditText pw;
    EditText pw2;
    EditText note;
    Button rbutton;
    String ssiid,sname,suid,spw,spw2,snote;
    int iuid,j=0;
    Bitmap bitmap;
    PreparedStatement pstmst = null;
    ByteArrayInputStream stream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏以及状态栏  todo   这里一定要注意 这些操作要在setContentView方法前操作
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register);

        iid = (ImageView)findViewById(R.id.iid);
        name = findViewById(R.id.name);
        pw = findViewById(R.id.pw);
        pw2 = findViewById(R.id.pw2);
        note = findViewById(R.id.note);
        rbutton = (Button)findViewById(R.id.rbutton);

        final TextView gologin = findViewById(R.id.gologin);

        final Thread thread = new Thread(new Runnable() {  //连接数据库
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
                    conn = DriverManager.getConnection(url, user, password);
                    Log.i(TAG, "远程连接成功!");
                } catch (SQLException e) {
                    Log.e(TAG, "远程连接失败!");
                }

                if (conn != null) {
                    try {
                        // 创建用来执行sql语句的对象
                        java.sql.Statement statement = conn.createStatement();
                        //生成不重复的 min<=uid<max
                        while (j==0){
                            int min = 1;
                            int max = 10000000;
                            iuid = min + ((int) (new Random().nextFloat() * (max - min)));
                            suid = Integer.toString(iuid);
                            //查询语句
                            String sql = "select uid from users_d where uid = '"+suid+"'";
                            // 执行sql查询语句并获取查询信息
                            ResultSet rSet = statement.executeQuery(sql);
                            //判断结果集是否为空
                            if(rSet.next()){
                                j=0;
                            }
                            else j=1;
                        }

                        //输入语句
                        String sql1 = "insert into users_d values(?,?,?,?,?)";//数据库语句
                        //string转blob存储
                        //String sql2 = "insert into t values(?,1)";//?位置是blob类型的
                        Log.i(TAG,"xmlstart");
                        pstmst =conn.prepareStatement(sql1);
                        stream = new ByteArrayInputStream(ssiid.getBytes());
                        pstmst.setBinaryStream(1,stream,stream.available());
                        pstmst.setString(2,sname);
                        pstmst.setString(3,suid);
                        pstmst.setString(4,spw);
                        pstmst.setString(5,snote);
                        pstmst.executeUpdate();//执行sqlINSERT、UPDATE 或 DELETE 语句
                        Log.i(TAG,"xmlstartok");
                        //将id存到xml文件里面
                        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                        sharedPreferences.getString("uid", null);
                        sharedPreferences.getString("iid", null);
                        Log.i(TAG, "xml创建");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("uid",suid);
                        editor.putString("iid",ssiid);
                        editor.commit();
                        Log.i(TAG, "xml数据已保存到sharedPreferences");
                        //页面跳转
                        Intent intent = new Intent();
                        intent.setClass(RegisterActivity.this, UserActivity.class);
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

        rbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sname = name.getText().toString();
                spw = pw.getText().toString();
                spw2 = pw2.getText().toString();
                snote = note.getText().toString();

                if(!sname.equals("")){
                    if(!snote.equals("")){
                        if(!spw.equals("")){
                            if(spw.equals(spw2)){
                                thread.start();
                            }else Toast.makeText(RegisterActivity.this,"The password input is inconsistent",Toast.LENGTH_LONG).show();
                        }else Toast.makeText(RegisterActivity.this,"pleaseSetYourPassword",Toast.LENGTH_LONG).show();
                    }else Toast.makeText(RegisterActivity.this,"PleaseSetYourIntroduction",Toast.LENGTH_LONG).show();
                }else Toast.makeText(RegisterActivity.this,"PleaseSetYourName",Toast.LENGTH_LONG).show();
            }
        });

        gologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        iid.setOnClickListener(new View.OnClickListener() {
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
        if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    //img.setImageBitmap(bitmap);
                    //bitmap转base64存储
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] appicon = baos.toByteArray();// 转为byte数组
                    ssiid = Base64.encodeToString(appicon, Base64.DEFAULT);
                    Log.i(TAG,"xmlstring:"+ssiid+"\n");
                    //base64转化为bitmap显示
                    Bitmap b = base64ToPicture(ssiid);
                    iid.setImageBitmap(b);

                } catch (IOException e) {
                    e.printStackTrace();
                }
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