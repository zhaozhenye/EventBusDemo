package com.zzy.eventbus;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getInstance().register(this);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

    /**
     * 其他页面需要调用到的某个方法
     * MainActivity将 a 方法放入到 eventbus 中去
     * 不是MainActivity中的所有方法都放进去
     * 思考1：使用什么方式告诉eventbus 哪些方式需要添加到eventbus 哪些不需要添加?
     * 答案： 通过注解的方式 。只有带有注解的方法才加入，不带有注解的不加入
     * 思考2：哪些信息要放到eventbus的容器中呢？只需要method吗？
     * 需要method 、threadMode 、方法参数  这三个变量需要进行一次封装
     * 利用单例实现内存共享
     */

    @Subscrible(threadMode = ThreadMode.BACKGROUND)//指定BACKGROUND后，方法a只能在子线程调用
    public void a(EventBean bean) {
        String name = Thread.currentThread().getName();
        Log.d(TAG, "bean: " + bean.toString());
        Log.d(TAG, "thread name: " + name);
    }

    @Subscrible(threadMode = ThreadMode.MAIN)//指定MAIN后，方法a只能在主线程调用
    public void b(EventBean bean) {
        String name = Thread.currentThread().getName();
        Log.d(TAG, "bean: " + bean.toString());
        Log.d(TAG, "thread name: " + name);
    }


}
