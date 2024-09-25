package com.gxy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Classname GpuEntity
 * @Date 2024/9/19
 * @Created by guoxinyu
 */
@Getter
@Setter
@AllArgsConstructor
public class GpuEntity implements Serializable {
    //序列化
    private static final long serialVersionUID = 1L;
    private String identifier; // 卡的标识，例如 N 卡或 A 卡
    private String group;      // 卡的分组，例如 "N 卡" 或 "A 卡"
    private int weight;        // 卡的权重，N 卡的权重可以设置为 1，A 卡的权重为 3

    public GpuEntity() {
    }
    // 获取 Redis 中的键名，格式为: group:identifier
    public String getRedisKey() {
        return group + ":" + identifier;
    }
}
