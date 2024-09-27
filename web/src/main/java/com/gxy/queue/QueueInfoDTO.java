package com.gxy.queue;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Classname TagServiceDTO
 * @Date 2024/9/19
 * @Created by guoxinyu
 */
@Getter
@Setter
public class QueueInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String identifier; // 卡的标识，例如 N 卡或 A 卡
    private String group;      // 卡的分组，例如 "N 卡" 或 "A 卡"
    private int weight;        // 卡的权重，N 卡的权重可以设置为 1，A 卡的权重为 3
    private String url;

    // 获取 Redis 中的键名，格式为: group:identifier
    public String getRedisKey() {
        return group + ":" + identifier;
    }


    public QueueInfoDTO() {
    }

    public QueueInfoDTO(String identifier, String group, int weight) {
        this.identifier = identifier;
        this.group = group;
        this.weight = weight;
    }

    public QueueInfoDTO(String identifier, String group, int weight, String url) {
        this.identifier = identifier;
        this.group = group;
        this.weight = weight;
        this.url = url;
    }
}
