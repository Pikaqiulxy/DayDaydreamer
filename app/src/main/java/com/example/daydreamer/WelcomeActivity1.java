package com.example.daydreamer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daydreamer.ui.login.LoginActivity;

public class WelcomeActivity1 extends AppCompatActivity {

    private ImageView imageView;           //创建图片对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏以及状态栏  todo   这里一定要注意 这些操作要在setContentView方法前操作
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome1);

        imageView = (ImageView) findViewById(R.id.dear);   //连接图片id

//简单动画操作
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);    //逐渐进入

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(alpha);      //动画控制

        animSet.setDuration(4000);    //动画持续时间
        animSet.start();



//7s之后自动关闭
        handler.sendEmptyMessageDelayed(0, 4500);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    //关闭欢迎页打开主页主页
    public void getHome() {
        Intent intent = new Intent(WelcomeActivity1.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}