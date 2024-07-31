package com.gxy.core.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Classname ArticleEntity
 * @Date 2024/7/25
 * @Created by guoxinyu
 */

@Data
@Table(value = "blog.article",dataSource = "postgresql-datasource")
public class ArticleEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String title;
    private String content;
    private String creatTime;
    private int status;
}
