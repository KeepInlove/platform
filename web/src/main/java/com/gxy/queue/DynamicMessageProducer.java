package com.gxy.queue;

import com.gxy.service.RedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Classname DynamicMessageProducer
 * @Date 2024/9/18
 * @Created by guoxinyu
 */
@Service
@Slf4j
public class DynamicMessageProducer {

    @Autowired
    private RedisService redisService;  // 使用 RedisServiceImpl 代替 RedisTemplate

    @Resource
    private QueueManager queueManager;

    private final Lock lock = new ReentrantLock();

    /**
     * 单个消息
     * @param messages
     * @param isSpecial
     */
    public void produceMessage(String messages,Boolean isSpecial) {
        if (messages == null || messages.isEmpty()) {
            log.error(">>>消息为空，无法生产消息");
            return;
        }
        lock.lock();
        try {
            QueueInfoDTO targetQueue = getInsertQueue(isSpecial);
            if (targetQueue == null) {
                log.error("队列为空，无法插入消息");
                return;
            }
            if (isSpecial) {
                redisService.leftPushToList(targetQueue.getRedisKey(), messages);
            } else {
                redisService.rightPushToList(targetQueue.getRedisKey(), messages);
            }
        }catch (Exception e){
            log.error("获取插入队列时发生异常", e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 批量消息
     * @param messages
     * @param isSpecial
     */
    public void produceMessage(List<String> messages,Boolean isSpecial) {
        if (messages == null || messages.isEmpty()) {
            log.error(">>>消息为空，无法生产消息");
            return;
        }
        lock.lock();
        try {
            int batchSize = 100;
            int totalBatches = messages.size() / batchSize; // 计算完整批次数
            if (messages.size() % batchSize != 0) {
                totalBatches++; // 如果有余数，增加一个批次
            }
            for (int batch = 0; batch < totalBatches; batch++) {
                // 计算当前批次的开始和结束索引
                int start = batch * batchSize;
                int end = Math.min(start + batchSize, messages.size());
                List<String> messageBatch = messages.subList(start, end); // 取出当前批次的消息
                QueueInfoDTO targetQueue = getInsertQueue(isSpecial); // 获取插入队列
                if (targetQueue == null) {
                    log.error("队列为空，无法插入消息");
                    return;
                }
                String redisKey = targetQueue.getRedisKey();
                // 根据是否是特殊消息插入不同队列
                if (isSpecial) {
                    redisService.leftPushToList(redisKey, messageBatch); // 插入特殊消息
                } else {
                    redisService.rightPushToList(redisKey, messageBatch); // 插入普通消息
                }
                log.info("第 {} 批次消息插入成功，共 {} 条消息", batch + 1, messageBatch.size());
            }
        } catch (Exception e) {
            log.error("获取插入队列时发生异常", e);
        } finally {
            lock.unlock();
        }

    }


    /**
     * 轮询方式选择队列
     * @param isSpecial
     * @return
     */
    private QueueInfoDTO getInsertQueue(boolean isSpecial) {
        String key = isSpecial ? "tag_N" : "tag_A";
        List<QueueInfoDTO> queues = isSpecial ? queueManager.getNQueues() : queueManager.getAQueues();
        // 确保队列不为空
        if (queues.isEmpty()) {
            return null;
        }
        // 从 Redis 中获取当前索引
        String indexStr = redisService.get(key);
        int currentIndex = (indexStr != null) ? Integer.parseInt(indexStr) : 0;
        // 减卡或者超出数组的时候会出现这种情况，此时需要从头开始
        if (currentIndex > queues.size() - 1) {
            currentIndex = 0;
        }
        QueueInfoDTO targetQueue = queues.get(currentIndex);
        // 修改下一个执行的索引
        int newIndex = currentIndex + 1;
        redisService.set(key, String.valueOf(newIndex), -1); // 更新索引到下一个队列
        log.info("使用队列 {} 插入消息，当前索引 {}，下一个执行的索引={}", targetQueue.getIdentifier(), currentIndex, newIndex);
        return targetQueue;
    }

    // 负载均衡,根据队列长度选择队列
    private String getLightestQueueBasedOnWeight(List<QueueInfoDTO> cards) {
        QueueInfoDTO lightestCard = null;
        long minWeightedSize = Long.MAX_VALUE;

        for (QueueInfoDTO card : cards) {
            Long size = redisService.getListSize(card.getRedisKey());

            if (size != null && size == 0) {
                log.info("选择空队列 {} 进行插入", card.getRedisKey());
                return card.getRedisKey();  // 优先选择空队列
            }
            long weightedSize = (size != null ? size : 0) * card.getWeight();
            //log.info("队列 {} 的负载权重: {}", card.getRedisKey(), weightedSize);
            if (weightedSize < minWeightedSize) {
                lightestCard = card;
                minWeightedSize = weightedSize;
            }
        }

        return lightestCard != null ? lightestCard.getRedisKey() : null;
    }

}
