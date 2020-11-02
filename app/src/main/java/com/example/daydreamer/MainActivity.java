package com.example.daydreamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daydreamer.DetailsActivity;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.getConnection;

public class MainActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener{

    private static final String TAG = "MyListActivity";

    static Handler handler;
    List<HashMap<String, String>> listData;
    private ListView listView;
    MyAdapter myAdapter;
    String suid,siid,sname;
    ImageView iid,add,imageView2;
    TextView name;
    PreparedStatement ps = null ,ps1 = null;
    ResultSet rSet=null ,rSet1=null;
    String spt[] = new String[1000];
    String stitle[] = new String[1000];
    String si1[] = new String[1000];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        iid = findViewById(R.id.iid);
        listView = (ListView) findViewById(R.id.listview);
        // = new ArrayList<>();
        add = findViewById(R.id.add);
        imageView2 = findViewById(R.id.imageView2);

        //头像展示
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        suid = sharedPreferences.getString("uid","001");
        siid = sharedPreferences.getString("iid","001");
        Log.i(TAG, "xmlread"+siid);
        //base64转化为bitmap显示
        Bitmap b = base64ToPicture(siid);
        iid.setImageBitmap(b);

        Thread t = new Thread(MainActivity.this);
        t.start();

        //对于此处的方法等待
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {
                    Log.i(TAG, "主：hander启动成功");
                    //base64转化为bitmap显示
                    Log.i(TAG, "主：数"+si1[0]);
                    Bitmap b1 = base64ToPicture(si1[0]);
                    imageView2.setImageBitmap(b1);
                    name.setText(sname);
                    listData = (ArrayList<HashMap<String, String>>) msg.obj;
                    myAdapter = new MyAdapter(MainActivity.this,
                            R.layout.form,
                            (ArrayList<HashMap<String, String>>) listData);
                    listView.setAdapter(myAdapter);
                    listView.setOnItemClickListener(MainActivity.this);
                }
                super.handleMessage(msg);
            }
        };

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AddActivity.class);
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
            Log.i(TAG, "远程连接成功!login");
        } catch (SQLException e) {
            Log.e(TAG, "远程连接失败!");
        }

        if (conn != null) {
            try {
                //查询名字
                String sql = "select name from users_d where uid = '"+suid+"'";
                // 创建用来执行sql语句的对象
                ps = conn.prepareStatement(sql);
                // 执行sql查询语句并获取查询信息
                rSet = ps.executeQuery();
                Log.i(TAG, "xml创建sql");
                if(rSet.next()){
                    // 得到对象
                    sname = rSet.getString(1);
                }

                //查询显示的图片等信息
                String sql1 = "select * from works_d where uid = '"+suid+"'";
                // 创建用来执行sql语句的对象
                ps1 = conn.prepareStatement(sql1);
                // 执行sql查询语句并获取查询信息
                rSet1 = ps1.executeQuery();
                Log.i(TAG, "xml创建sql");
                //判断结果是否为空（判断是否匹配）
                // 迭代打印出查询信息
                while (rSet1.next()) {
                    List<String> list2 = new ArrayList<String>();//创建取结果的列表，之所以使用列表，不用数组，因为现在还不知道结果有多少，不能确定数组长度，所有先用list接收，然后转为数组
                    List<String> list3 = new ArrayList<String>();
                    List<Blob> list5 = new ArrayList<Blob>();
                    List<String> list8 = new ArrayList<String>();
                    list2.add(rSet1.getString(2));
                    list3.add(rSet1.getString(3));
                    list5.add(rSet1.getBlob(5));
                    list8.add(rSet1.getString(8));
                    Log.i(TAG, "主：读取成功");
                    listData = new ArrayList<>();
                    if (list2 != null && list2.size() > 0) {//如果list中存入了数据，转化为数组

                        for (int i = 0; i < list2.size(); i++) {
                            HashMap<String, String> map = new HashMap<>();
                            spt[i] = list2.get(i) +"·"+list8.get(i);//数组赋值了。
                            stitle[i] = list3.get(i);
                            si1[i] = new String((list5.get(i)).getBytes(1, (int) (list5.get(i)).length()),"GBK");//blob 转 String
                            map.put("spt",spt[i]);
                            map.put("stitle",stitle[i]);
                            map.put("si1",si1[i]);
                            Log.i(TAG, "主：数组："+"+"+spt[i]+stitle[i]);
                            listData.add(map);
                        }
                        Log.i(TAG, "主：数"+si1[0]);
                    }
                }
                //base64转化为bitmap显示
                //Bitmap b1 = base64ToPicture(si1[0]);
                //imageView2.setImageBitmap(b1);

                conn.close();

            } catch (SQLException | UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(TAG, "关闭连接失败");
            }
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = listData;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TextView text1 = view.findViewById(R.id.text1);
        TextView text2 = view.findViewById(R.id.text2);
        String titleString = String.valueOf(text1.getText());
        String detailString = String.valueOf(text2.getText());

        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

        //使用Bundle一体化的传输数据
        Bundle bundle = new Bundle();
        bundle.putString("text1", titleString);
        bundle.putString("text2", detailString);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    //base64转bitmap显示
    public Bitmap base64ToPicture(String imgBase64) {
        byte[] decode = Base64.decode(imgBase64, Base64.DEFAULT);
        Bitmap bitma = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitma;
    }
}