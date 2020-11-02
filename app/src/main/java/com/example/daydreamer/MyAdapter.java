package com.example.daydreamer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends ArrayAdapter {
    /*
     * 自定义的MyAdapter接受ArrayList<HashMap<String, String>>作为参数
     * 与list_item.xml中的资源相关联,用于列表视图的显示
     * */

    private static final String TAG = "MyAdapter";

    public MyAdapter(Context context,
                     int resource,
                     ArrayList<HashMap<String, String>> list) {
        super(context, resource, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.form,
                    parent,
                    false);
        }
        HashMap<String, String> map = (HashMap<String, String>) getItem(position);

        ImageView image1 = itemView.findViewById(R.id.image1);
        TextView theme = itemView.findViewById(R.id.text1);
        TextView content = itemView.findViewById(R.id.text2);
        Log.i(TAG, "主：显示开始");
        image1.setImageBitmap(base64ToPicture(map.get("si1").toString()));
        theme.setText(map.get("stitle").toString());
        content.setText(map.get("spt").toString());
        Log.i(TAG, "主：显示成功");
        return itemView;
    }

    //base64转bitmap显示
    public Bitmap base64ToPicture(String imgBase64) {
        byte[] decode = Base64.decode(imgBase64, Base64.DEFAULT);
        Bitmap bitma = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitma;
    }
}