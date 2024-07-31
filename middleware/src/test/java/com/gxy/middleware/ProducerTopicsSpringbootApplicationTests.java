package com.gxy.middleware;

import com.gxy.middleware.config.RabbitmqConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname ProdcerTopicsSpringbootApplicationTests
 * @Date 2024/7/24
 * @Created by guoxinyu
 */

@SpringBootTest
public class ProducerTopicsSpringbootApplicationTests {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    public void Producer_topics_springbootTest() {

        //使用rabbitTemplate发送消息
        String message = "发送一个mq消息内容";
        /**
         * 参数：
         * 1、交换机名称
         * 2、routingKey
         * 3、消息内容
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.email", message);

    }

}
