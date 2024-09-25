package com.gxy.listener;

import com.gxy.event.MyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Classname MyEventListener
 * @Date 2024/9/18
 * @Created by guoxinyu
 */
// 事件监听器
@Component
public class MyEventListener implements ApplicationListener<MyEvent> {
    @Override
    public void onApplicationEvent(MyEvent event) {
        System.out.println("接收到事件: " + event.getMessage());
        // 模拟事件处理
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
