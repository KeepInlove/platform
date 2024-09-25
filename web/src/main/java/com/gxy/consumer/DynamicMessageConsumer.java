package com.gxy.consumer;

import com.gxy.entity.GpuEntity;
import com.gxy.message.QueueManager;
import com.gxy.service.RedisService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * @Classname DynamicMessageConsumer
 * @Date 2024/9/18
 * @Created by guoxinyu
 */
@Service
@Slf4j
public class DynamicMessageConsumer {

    @Autowired
    private RedisService redisService;

    @Resource
    private QueueManager queueManager;

    private ThreadPoolExecutor executor;

    private volatile boolean running = true; // 添加一个标志位来控制线程的运行状态
    @PostConstruct
    public void init() {
        // 初始化线程池
        executor = new ThreadPoolExecutor(
                10,         // 核心线程数
                50,         // 最大线程数
                60,         // 线程空闲保持时间（秒）
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),  // 队列容量
                new CustomThreadFactory(),  // 自定义线程工厂，设置线程名
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：调用线程执行任务
        );
//        consumeNCardMessages(); // 启动 N 卡消息消费
//        consumeNACardMessages(); // 启动A 卡消息消费
    }
    @PreDestroy
    public void shutdown() {
        log.info("开始优雅关闭线程池...");
        running = false; // 通知所有消费者线程停止
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        log.info("线程池已关闭");
    }

    // 提交消费任务到线程池
    public void consumeNCardMessages() {
        List<GpuEntity> nCardQueues = queueManager.getNQueues();
        for (GpuEntity queue : nCardQueues) {
            executor.submit(() -> {
                Thread.currentThread().setName("NCard-Thread-" + queue.getIdentifier());
                consumeMessagesFromQueue(queue.getRedisKey());
            });
        }
    }

    public void consumeNACardMessages() {
        List<GpuEntity> nACardQueues = queueManager.getAQueues();
        for (GpuEntity queue : nACardQueues) {
            executor.submit(() -> {
                Thread.currentThread().setName("ACard-Thread-" + queue.getIdentifier());
                consumeMessagesFromQueue(queue.getRedisKey());
            });
        }
    }
    // 消费单个队列的消息
    private void consumeMessagesFromQueue(String queue) {
        while (running) { // 使用标志位控制线程是否继续运行
            try {
                Long queueSize = redisService.getListSize(queue);  // 获取队列大小
                if (queueSize != null && queueSize > 0) {
                    String message = redisService.rightPop(queue, 5, TimeUnit.SECONDS);  // 从队列尾部弹出消息
                    if (message != null) {
                        //消费消息
                        handleMessage(queue, message);
                    } else {
                        log.info("队列 {} 中无可用消息。", queue);
                    }
                } else {
                    log.info("队列 {} 中消息为空，等待中...", queue);
                    Thread.sleep(5000); // 如果队列为空，休眠一段时间再检查
                }
            } catch (RedisConnectionFailureException | RedisSystemException e) {
                log.error("Redis 命令失败或超时，正在重试...", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // 处理消息
    private void handleMessage(String queue, String message) {
       log.info("[开始消费消息]-消费队列:{},消息内容:{}", queue , message);
        // 处理消息的业务逻辑
    }

    // 自定义线程工厂
    private static class CustomThreadFactory implements ThreadFactory {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName("Gpu-Consumer-Thread-" + thread.getId()); // 默认命名
            return thread;
        }
    }
}
