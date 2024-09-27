package com.gxy.service;

import org.springframework.data.redis.connection.RedisListCommands.Direction;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Classname RedisService
 * @Date 2024/9/19
 * @Created by guoxinyu
 */
public interface RedisService {

    String get(String key);

    /**
     * 设置缓存对
     * @param key
     * @param value
     * @param expire	超时时间, 单位是秒
     */
    boolean set(String key, String value, int expire);

    /**
     * 设置缓存对
     *
     * @param key
     * @param value
     * @param timeout 超时时间
     * @param timeUnit 单位
     */
    boolean set(String key, String value, long timeout, TimeUnit timeUnit);

    /**
     * 删除单个key
     * @param key
     */
    boolean remove(String key);

    /**
     * 获取匹配的key集合
     * @param pattern
     * @return
     */
    Set<String> keys(String pattern);


    /**
     * 添加一个值到set集合中
     * @param key		set的key
     * @param value		set的某个值
     */
    boolean addValueToSet(String key, String value);

    /**
     * 从set集合中移除元素值
     * @param key		set的key
     * @param value		被移除的值
     */
    boolean removeValueFromSet(String key, String value);

    /**
     * 获取一个set集合的所有值
     * @param key	set的key
     */
    Set<String> getValueSet(String key);

//-------------

    /**
     * 在 Redis 的哈希表中设置字段和值
     * @param key Redis 键
     * @param field 哈希字段
     * @param value 哈希字段对应的值
     */
    boolean setHashField(String key, String field, String value);


    /**
     * 获取哈希表的所有条目
     * @param key Redis 哈希表的键
     * @return 哈希表的所有字段和值
     */
    Map<String, String> getHashEntries(String key);
    /**
     * 获取哈希表中某个字段的值
     * @param key Redis 键
     * @param field 哈希字段
     * @return 哈希字段对应的值
     */
    String getHashField(String key, String field);

    /**
     * 删除哈希表中的某个字段
     * @param key Redis 键
     * @param field 哈希字段
     */
    boolean deleteHashField(String key, String field);
    /**
     * 获取 Redis 列表的大小
     * @param key Redis 列表的键
     * @return 列表大小
     */
    Long getListSize(String key);

    /**
     * 将消息插入到 Redis 列表的尾部
     * @param key Redis 列表的键
     * @param value 插入的值
     * @return 是否插入成功
     */
    boolean rightPushToList(String key, String value);

    boolean rightPushToList(String key, List<String> value);

    /**
     * 将消息插入到 Redis 列表的头部
     * @param key Redis 列表的键
     * @param value 插入的值
     * @return 是否插入成功
     */
    boolean leftPushToList(String key, String value);


    boolean leftPushToList(String key, List<String> value);

    /**
     * 从队列的尾部弹出消息
     * @param key Redis 列表的键
     * @param timeout 超时时间
     * @param timeUnit 超时的时间单位
     * @return 弹出的消息
     */
    String rightPop(String key, long timeout, TimeUnit timeUnit);

    /**
     * 阻塞式移动元素
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param timeUnit
     * @return
     */

    String blmove(String sourceKey, String destinationKey, long timeout, TimeUnit timeUnit);

    /**
     *  移除队列中的元素
     * @param key
     * @param count
     * @param value
     */

    void removeQueue(String key, int count, String value);


}