package com.gxy.queue;

import com.gxy.config.ThreadPoolConfig;
import com.gxy.service.RedisService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Classname DynamicMessageConsumer
 * @Date 2024/9/18
 * @Created by guoxinyu
 */
@Service
@Slf4j
public class DynamicMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DynamicMessageConsumer.class);

    @Resource
    private RedisService redisService;

    @Resource
    private QueueManager queueManager;

    @Resource(name = "callRecordTagThreadPool")
    private ExecutorService executor;  // 从 ThreadPoolConfig 注入线程池

    @Resource
    private ThreadPoolConfig threadPoolConfig;  // 注入线程池配置类


    private volatile boolean running = true; // 添加一个标志位来控制线程的运行状态
    @PostConstruct
    public void init() {
        // 消费监听
        consumeNMessages();
    }
    @PreDestroy
    public void shutdown() {
        running = false; // 通知所有消费者线程停止
        threadPoolConfig.shutdownThreadPool(executor);
    }

    // 提交消费任务到线程池
    public void consumeNMessages() {
        List<QueueInfoDTO> nCardQueues = queueManager.getNQueues();
        List<QueueInfoDTO> aCardQueues = queueManager.getAQueues();
        Map<String, QueueInfoDTO> cache = new HashMap<>();
        nCardQueues.addAll(aCardQueues);
        for (QueueInfoDTO tagService : nCardQueues) {
            cache.put(tagService.getIdentifier(), tagService);
        }
        for (Map.Entry<String, QueueInfoDTO> entry: cache.entrySet()) {
            QueueInfoDTO tagService = entry.getValue();
            executor.submit(() -> {
                Thread.currentThread().setName("NCard-Thread-" + tagService.getIdentifier());
                consumeMessagesFromQueue(tagService);
            });
        }
    }


    // 消费单个队列的消息
    private void consumeMessagesFromQueue(QueueInfoDTO tagService) {
        while (running) { // 使用标志位控制线程是否继续运行
            try {
                // 1：目标队列
                String redisKey = tagService.getRedisKey();
                // 2：pending 队列
                String pendingKey = redisKey+"Pending";
                // 3：从目标队列阻塞推入到 pending 队列
                String message = redisService.blmove(redisKey, pendingKey, 5, TimeUnit.SECONDS);  // 从队列头部弹出消息
                if (message != null) {
                    //消费消息
                    try {
                        handleMessage(tagService, message);
                        logger.info("[消费消息]-消费队列:{},消息内容:{}", tagService.getIdentifier() , message);
                    }catch (Exception e){
                        logger.error("消费消息失败，消息内容:{}", message, e);
                        continue;
                    }
                } else {
                    logger.info("队列 {} 中无可用消息。", redisKey);
                    continue;
                }
                // 3：消费成功，从 pending 队列删除记录，相当于确认消费
                redisService.removeQueue(pendingKey, -1, message);
                logger.info("[消费消息]-从 pending 队列删除消息:{}", message);
            } catch (RedisConnectionFailureException | RedisSystemException e) {
                logger.error("Redis 命令失败或超时，正在重试...", e);
            }
        }
    }

    // 处理消息
    private void handleMessage(QueueInfoDTO tagService, String message) {
        try {
            logger.info("[开始消费消息]-消费队列:{},消息内容:{}", tagService.getIdentifier() , message);
        } catch (Exception e) {
            logger.error("[消费消息失败]-消费队列:{},消息内容:{}", tagService.getIdentifier() , message, e);
        }
    }
}
