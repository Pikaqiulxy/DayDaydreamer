package com.example.daydreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "daydreamer";
    ImageView iid;
    String suid,siid;
    private ImageView add;
    private List<Map<String, Object>> lists;
    private SimpleAdapter adapter;
    private ListView listView;

    private String[] theme = {"张明", "李明", "李明", "李明", "李明"};
    private String[] content = {"600 602 501", "666 620 502", "666 620 503", "666 620 503", "666 620 503"};
    private int imageViews = R.mipmap.small;  //用到的图片是mipmap中的ic_launcher

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = (ImageView)findViewById(R.id.add);
        iid = findViewById(R.id.iid);

        //头像展示
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        suid = sharedPreferences.getString("uid","001");
        //siid = sharedPreferences.getString("iid","001");
        siid="content://media/external/images/media/1916917";
        Log.i(TAG, "xmlread"+siid);
        //string转uri并展示
        Uri uri1 = Uri.parse(siid);
        iid.setImageURI(uri1);

        //需要把图片和文字(一个单元中的东西)用Map对应起来，必须这样做，这是下面要用到的适配器的一个参数
        lists = new ArrayList<>();
        for (int i = 0; i < theme.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", imageViews);
            map.put("theme", theme[i]);
            map.put("content", content[i]);
            lists.add(map);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, AddActivity.class);
                    startActivity(intent);
                }
            });
        }

        //适配器指定应用自己定义的xml格式
        adapter = new SimpleAdapter(MainActivity.this, lists, R.layout.form, new String[]{"image", "theme", "content"}, new int[]{R.id.image1, R.id.text1, R.id.text2});
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }
}