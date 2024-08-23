package com.gxy.service.impl;

import com.gxy.entity.ModelRequestsRespEntity;
import com.gxy.mapper.mysql.ModelRequestsRespMapper;
import com.gxy.service.ModelRequestsRespService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用于存储模型请求和响应结果的表 服务层实现。
 *
 * @author Gxy
 * @since 1.0.0
 */
@Service
public class ModelRequestsRespServiceImpl extends ServiceImpl<ModelRequestsRespMapper, ModelRequestsRespEntity> implements ModelRequestsRespService {

}
