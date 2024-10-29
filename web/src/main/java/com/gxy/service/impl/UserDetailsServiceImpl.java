package com.gxy.service.impl;

import com.gxy.entity.UserEntity;
import com.gxy.mapper.mysql.UserMapper;
import com.gxy.req.LoginUser;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Classname UserDetailsServiceImpl
 * @Date 2024/10/29
 * @Created by guoxinyu
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper wrapper = QueryWrapper.create()
                .select()
                .from(UserEntity.class)
                .where(UserEntity::getUsername).eq(username);
        UserEntity user = userMapper.selectOneByQuery(wrapper);
        //如果查询不到数据就通过抛出异常来给出提示
        if(Objects.isNull(user)){
            throw new RuntimeException("用户名或密码错误");
        }
        //TODO 根据用户查询权限信息 添加到LoginUser中

        //封装成UserDetails对象返回
        return new LoginUser(user);
    }
}
