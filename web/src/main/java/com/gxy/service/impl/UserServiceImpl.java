package com.gxy.service.impl;

import com.gxy.entity.UserEntity;
import com.gxy.mapper.mysql.UserMapper;
import com.gxy.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Classname UserServiceImpl
 * @Date 2024/7/23
 * @Created by guoxinyu
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
