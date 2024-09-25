package com.gxy.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Classname CountDownLatchExample
 * @Date 2024/9/4
 * @Created by guoxinyu
 */
public class CountDownLatchExample {

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws InterruptedException {
        // 创建 CountDownLatch，计数器初始化为 3
        CountDownLatch latch = new CountDownLatch(3);

        // 创建并启动三个任务线程
//        for (int i = 1; i <= 3; i++) {
//            executorService.execute(new Task)
//        }

        System.out.println("主线程等待子任务完成...");
        // 主线程等待，直到计数器为 0
        latch.await();
        System.out.println("所有子任务完成，主线程继续执行");
    }
}

class Task implements Runnable {
    private CountDownLatch latch;
    private int taskId;

    public Task(CountDownLatch latch, int taskId) {
        this.latch = latch;
        this.taskId = taskId;
    }

    @Override
    public void run() {
        try {
            System.out.println("任务 " + taskId + " 开始执行...");
            Thread.sleep(1000 * taskId); // 模拟任务耗时
            System.out.println("任务 " + taskId + " 执行完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 任务完成后，计数器减 1
            latch.countDown();
        }
    }
}