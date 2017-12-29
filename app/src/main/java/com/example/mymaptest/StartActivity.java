package com.example.mymaptest;

/**
 * Created by 逸风 on 2017/11/30.
 * 程序启动界面动画
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity
{
    private ImageView welcomeImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**全屏设置，隐藏窗口所有装饰*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        welcomeImg = (ImageView) this.findViewById(R.id.welcome_img);
        //设置动画透明度（0—1）
        AlphaAnimation anima = new AlphaAnimation(1.0f, 1.0f);
        // 设置动画显示时间2秒
        anima.setDuration(2000);
        welcomeImg.startAnimation(anima);
        anima.setAnimationListener(new AnimationImpl());

    }

    private class AnimationImpl implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            welcomeImg.setBackgroundResource(R.mipmap.start_bg);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // 动画结束后跳转到主页面
            skip();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    private void skip() {
        //跳转页面后将此activity干掉
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
