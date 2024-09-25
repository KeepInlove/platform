package com.gxy.producer;

import com.gxy.entity.GpuEntity;
import com.gxy.message.QueueManager;
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
    // 动态选择队列并生产消息（支持优先处理特殊数据）
    public void produceMessage(String message,Boolean isSpecial) {
        // 获取所有队列（N 卡和 N + A 卡共享队列）
        List<GpuEntity> allQueues = queueManager.getAllQueues();
        // 动态选择负载最轻的队列插入消息
        String targetQueue = getLightestQueueBasedOnWeight(allQueues);
        if (targetQueue != null) {
            if (isSpecial != null && isSpecial) {
                lock.lock();
                try {
                    pushSpecialMessageHead(message);
                } finally {
                    lock.unlock();
                }
            } else {
                lock.lock();
                try {
                    redisService.rightPushToList(targetQueue, message);  // 使用 RedisServiceImpl 插入消息
                   // log.info("普通消息添加到 {} 队列-消息内容: {}", targetQueue, message);
                } finally {
                    lock.unlock();
                }
            }
        } else {
            log.warn("没有可用的队列来处理消息：{}", message);
        }
    }

    // 将特殊数据插入到 N 卡队列的头部
    private void pushSpecialMessageHead(String message) {
        // 获取 N 卡队列
        List<GpuEntity> nCardQueues = queueManager.getNQueues();

        String nCardQueue = getLightestQueueBasedOnWeight(nCardQueues);
        // 使用 RedisServiceImpl 将消息插入到队列头部
        redisService.leftPushToList(nCardQueue, message);
        //log.info("特殊消息添加到{}队列-消息内容: {}", nCardQueue, message);
    }


    // 负载均衡
    private String getLightestQueueBasedOnWeight(List<GpuEntity> cards) {
        GpuEntity lightestCard = null;
        long minWeightedSize = Long.MAX_VALUE;

        for (GpuEntity card : cards) {
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
