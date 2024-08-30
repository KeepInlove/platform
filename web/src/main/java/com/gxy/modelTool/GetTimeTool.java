package com.gxy.modelTool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Classname GetTimeTool
 * @Date 2024/8/29
 * @Created by guoxinyu
 */
public class GetTimeTool {

    public GetTimeTool() {
    }
    public String call() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = "当前时间：" + now.format(formatter) + "。";
        return currentTime;
    }
}
