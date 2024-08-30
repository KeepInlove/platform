package com.gxy.modelTool;

/**
 * @Classname GetWhetherTool
 * @Date 2024/8/29
 * @Created by guoxinyu
 */
public class GetWhetherTool {

    private String location;

    public GetWhetherTool(String location) {
        this.location = location;
    }

    public String call() {
        return location + "今天是晴天";
    }
}
