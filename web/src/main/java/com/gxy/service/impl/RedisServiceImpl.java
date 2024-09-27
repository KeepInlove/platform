package com.gxy.service.impl;

import com.gxy.service.RedisService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.RedisListCommands.Direction;
/**
 * @Classname RedisServiceImpl
 * @Date 2024/9/19
 * @Created by guoxinyu
 */
@Service
public class RedisServiceImpl implements RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    private void init() {
        // 设置序列器, 以防止使用默认的 JdkSerializationRedisSerializer 导致乱码
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean set(String key, String value, int expire) {
        try {
            if (expire == -1) {
                // -1 表示不设置到期时间, 常规缓存不建议使用
                redisTemplate.opsForValue().set(key, value);
            } else {
                redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            }
        } catch (Exception ex) {
            logger.error(">>> redis set error. ex=", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean set(String key, String value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception ex) {
            logger.error(">>> redis set error. ex=", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ex) {
            logger.error(">>> redis remove error. ex=", ex);
            return false;
        }
        return true;
    }

    @Override
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public boolean addValueToSet(String key, String value) {
        try {
            redisTemplate.opsForSet().add(key, value);
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
            return true;
        } catch (Exception ex) {
            logger.error(">>> redis addValueToSet error. ex=", ex);
        }
        return false;
    }

    @Override
    public boolean removeValueFromSet(String key, String value) {
        try {
            redisTemplate.opsForSet().remove(key, value);
            return true;
        } catch (Exception ex) {
            logger.error(">>> redis removeValueFromSet error. ex=", ex);
        }
        return false;
    }

    @Override
    public Set<String> getValueSet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception ex) {
            logger.error(">>> redis getValueSet error. ex=", ex);
        }
        return new HashSet<>();
    }
    @Override
    public Map<String, String> getHashEntries(String key) {
        try {
            // 使用 Redis 的 HashOperations 获取哈希表的所有字段和值
            return redisTemplate.<String, String>opsForHash().entries(key);
        } catch (Exception ex) {
            logger.error(">>> Redis 获取哈希表条目失败. key={}, ex={}", key, ex);
            return Collections.emptyMap();
        }
    }
    @Override
    public boolean setHashField(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);  // 使用 JSON 字符串存储值
            return true;
        } catch (Exception ex) {
            logger.error(">>> Redis 设置哈希字段失败. key={}, field={}, ex={}", key, field, ex);
            return false;
        }
    }

    @Override
    public String getHashField(String key, String field) {
        try {
            return (String) redisTemplate.opsForHash().get(key, field);  // 获取 JSON 字符串并返回
        } catch (Exception ex) {
            logger.error(">>> Redis 获取哈希字段失败. key={}, field={}, ex={}", key, field, ex);
            return null;
        }
    }

    @Override
    public boolean deleteHashField(String key, String field) {
        try {
            redisTemplate.opsForHash().delete(key, field);  // 删除哈希字段
            return true;
        } catch (Exception ex) {
            logger.error(">>> Redis 删除哈希字段失败. key={}, field={}, ex={}", key, field, ex);
            return false;
        }
    }

    @Override
    public Long getListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception ex) {
            logger.error(">>> Redis 获取列表大小时出错. key={}, ex={}", key, ex);
            return null;
        }
    }

    @Override
    public boolean rightPushToList(String key, String value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception ex) {
            logger.error(">>> Redis 插入消息到队列尾部失败. key={}, ex={}", key, ex);
            return false;
        }
    }
    @Override
    public boolean rightPushToList(String key, List<String> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception ex) {
            logger.error(">>> Redis 插入消息到队列尾部失败. key={}, ex={}", key, ex);
            return false;
        }
    }

    @Override
    public boolean leftPushToList(String key, String value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception ex) {
            logger.error(">>> Redis 插入消息到队列头部失败. key={}, ex={}", key, ex);
            return false;
        }
    }
    @Override
    public boolean leftPushToList(String key, List<String> value) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            return true;
        } catch (Exception ex) {
            logger.error(">>> Redis 插入消息到队列头部失败. key={}, ex={}", key, ex);
            return false;
        }
    }
    @Override
    public String rightPop(String key, long timeout, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForList().rightPop(key, timeout, timeUnit);  // 从队列的尾部弹出消息
        } catch (Exception ex) {
            logger.error(">>> Redis rightPop 操作失败. key={}, ex={}", key, ex);
            return null;
        }
    }

    @Override
    public String blmove(String sourceKey, String destinationKey, long timeout, TimeUnit timeUnit) {
        try {
            return redisTemplate.execute((RedisCallback<String>) connection -> {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] source = serializer.serialize(sourceKey);
                byte[] destination = serializer.serialize(destinationKey);
                Direction from = Direction.LEFT;   // 从左侧弹出，实现 FIFO
                Direction to = Direction.RIGHT;    // 推入右侧

                // 将超时时间转换为秒
                double timeoutInSeconds = timeUnit.toMillis(timeout) / 1000.0;
                // 调用 bLMove 命令
                assert source != null;
                assert destination != null;
                byte[] result = connection.listCommands().bLMove(
                        source,
                        destination,
                        from,
                        to,
                        timeoutInSeconds
                );
                if (result != null) {
                    return serializer.deserialize(result);
                } else {
                    return null;
                }
            });
        } catch (Exception ex) {
            logger.error(">>> Redis blmove 操作失败. sourceKey={}, ex={}", sourceKey, ex);
            return null;
        }
    }

    @Override
    public void removeQueue(String key, int count, String value) {
        try {
            redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception ex) {
            logger.error(">>> Redis出队失败. key={}, ex={}", key, ex);
        }
    }


}
