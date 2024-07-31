package com.gxy.core.service.impl;

import com.gxy.core.entity.ArticleEntity;
import com.gxy.core.mapper.postgresql.ArticleMapper;
import com.gxy.core.service.ArticleService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Classname ArticleServiceImpl
 * @Date 2024/7/25
 * @Created by guoxinyu
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity> implements ArticleService {
}
