package com.gxy.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Classname MyEvent
 * @Date 2024/9/18
 * @Created by guoxinyu
 */
public class MyEvent extends ApplicationEvent {
    private String message;

    public MyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;

    }
}