package com.example.daydreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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

        //需要把图片和文字(一个单元中的东西)用Map对应起来，必须这样做，这是下面要用到的适配器的一个参数
        lists = new ArrayList<>();
        for (int i = 0; i < theme.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", imageViews);
            map.put("theme", theme[i]);
            map.put("content", content[i]);
            lists.add(map);
        }

        //适配器指定应用自己定义的xml格式
        adapter = new SimpleAdapter(MainActivity.this, lists, R.layout.form, new String[]{"image", "theme", "content"}, new int[]{R.id.image1, R.id.text1, R.id.text2});
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }
}