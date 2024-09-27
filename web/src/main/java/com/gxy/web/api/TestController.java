package com.gxy.web.api;

import com.alibaba.fastjson.JSON;
import com.gxy.queue.DynamicMessageConsumer;
import com.gxy.entity.UserEntity;
import com.gxy.queue.DynamicMessageProducer;
import com.gxy.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname TestController
 * @Date 2024/7/23
 * @Created by guoxinyu
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource
    private UserService userService;

    @Resource
    private DynamicMessageProducer dynamicMessageProducer;
    @Resource
    private DynamicMessageConsumer dynamicMessageConsumer;
    @RequestMapping("/hello")
    public String hello() {
        UserEntity user = userService.getById(1);
        log.info("user:{}", user);
        return JSON.toJSONString(user);
    }
    @RequestMapping("/producerMessage")
    public String producerMessage() {
        // 生产20条普通消息
        for (int i = 1; i <= 100; i++) {
            String normalMessage = "普通消息 " + i;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            dynamicMessageProducer.produceMessage(normalMessage, false); // false表示非特殊消息
        }

        // 生产5条特殊消息
        for (int i = 1; i <= 20; i++) {
            String specialMessage = "特殊消息 " + i;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            dynamicMessageProducer.produceMessage(specialMessage, true); // true表示特殊消息
        }
        return "20 条普通消息和 5 条特殊消息已生产！";
    }
}
