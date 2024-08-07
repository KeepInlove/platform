package com.gxy.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Classname UserEntity
 * @Date 2024/7/23
 * @Created by guoxinyu
 */
@Data
@Table(value = "user",dataSource = "mysql-datasource")
public class UserEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String username;

    private String password;

    private int sex;

    private String email;
}
