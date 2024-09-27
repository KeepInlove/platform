package com.gxy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.util.concurrent.*;

/** 线程配置
 * @Classname ThreadPoolConfig
 * @Date 2024/9/20
 * @Created by guoxinyu
 */
@Configuration
public class ThreadPoolConfig {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);

    // 线程池
    @Bean(name = "callRecordTagThreadPool")
    public ExecutorService callRecordTagThreadPool() {
        return new ThreadPoolExecutor(
                10,         // 核心线程数
                50,         // 最大线程数
                60L,        // 线程空闲保持时间（秒）
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),  // 队列容量
                new CustomThreadFactory("callRecordTagThread"), // 自定义线程工厂，设置线程名称
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略：调用线程执行任务
        );
    }

    // 异步任务线程池
    @Bean(name = "asyncThreadPool")
    public ExecutorService asyncThreadPool() {
        return Executors.newCachedThreadPool(new CustomThreadFactory("Async-Thread"));
    }

    // 优雅关闭线程池的方法
    public void shutdownThreadPool(ExecutorService executorService) {
        logger.info("开始关闭线程池...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        logger.info("线程池已关闭");
    }

    // 自定义线程工厂，允许为不同线程池设置不同的线程名前缀
    private static class CustomThreadFactory implements ThreadFactory {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final String threadNamePrefix;

        public CustomThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public Thread newThread(@Nullable Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName(threadNamePrefix + "-" + thread.getId());
            return thread;
        }
    }
}
