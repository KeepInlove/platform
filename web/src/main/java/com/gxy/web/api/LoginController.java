package com.gxy.web.api;

import com.gxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Classname LoginController
 * @Date 2024/10/29
 * @Created by guoxinyu
 */
public class LoginController {

    @Autowired
    private UserService loginServcie;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        return loginServcie.login(user);
    }
}
