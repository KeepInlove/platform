package com.gxy.web.api;

import com.alibaba.fastjson.JSON;
import com.gxy.entity.UserEntity;
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
    @RequestMapping("/hello")
    public String hello() {
        UserEntity user = userService.getById(1);
        log.info("user:{}", user);
        return JSON.toJSONString(user);
    }
}
