package com.zzy.eventbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一步，我先网络加载数据，异步加载
                //第二步 调用MainActivity的a方式时，是在子线程，如果更新UI，会出错
                //我们希望注解中可以看到线程的模式

                EventBus.getInstance().post(new EventBean("zhaozhenye",28));
            }
        });
    }
}
