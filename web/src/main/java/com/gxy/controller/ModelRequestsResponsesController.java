package com.gxy.controller;

import com.gxy.entity.ModelRequestsRespEntity;
import com.gxy.service.ModelRequestsRespService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用于存储模型请求和响应结果的表 控制层。
 *
 * @author Gxy
 * @since 1.0.0
 */
@RestController
@RequestMapping("/modelRequestsResponses")
public class ModelRequestsResponsesController {

    @Autowired
    private ModelRequestsRespService modelRequestsResponsesService;

    /**
     * 添加用于存储模型请求和响应结果的表。
     *
     * @param modelRequestsRespEntity 用于存储模型请求和响应结果的表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ModelRequestsRespEntity modelRequestsRespEntity) {
        return modelRequestsResponsesService.save(modelRequestsRespEntity);
    }

    /**
     * 根据主键删除用于存储模型请求和响应结果的表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return modelRequestsResponsesService.removeById(id);
    }

    /**
     * 根据主键更新用于存储模型请求和响应结果的表。
     *
     * @param modelRequestsRespEntity 用于存储模型请求和响应结果的表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ModelRequestsRespEntity modelRequestsRespEntity) {
        return modelRequestsResponsesService.updateById(modelRequestsRespEntity);
    }

    /**
     * 查询所有用于存储模型请求和响应结果的表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ModelRequestsRespEntity> list() {
        return modelRequestsResponsesService.list();
    }

    /**
     * 根据用于存储模型请求和响应结果的表主键获取详细信息。
     *
     * @param id 用于存储模型请求和响应结果的表主键
     * @return 用于存储模型请求和响应结果的表详情
     */
    @GetMapping("getInfo/{id}")
    public ModelRequestsRespEntity getInfo(@PathVariable Serializable id) {
        return modelRequestsResponsesService.getById(id);
    }

    /**
     * 分页查询用于存储模型请求和响应结果的表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ModelRequestsRespEntity> page(Page<ModelRequestsRespEntity> page) {
        return modelRequestsResponsesService.page(page);
    }

}
