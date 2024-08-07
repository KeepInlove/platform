package com.gxy.web.api;

import com.gxy.entity.Candidate;
import com.gxy.service.CandidateService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author Gxy
 * @since 1.0.0
 */
@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    /**
     * 添加。
     *
     * @param candidate 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Candidate candidate) {
        return candidateService.save(candidate);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return candidateService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param candidate 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Candidate candidate) {
        return candidateService.updateById(candidate);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Candidate> list() {
        return candidateService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public Candidate getInfo(@PathVariable Serializable id) {
        return candidateService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Candidate> page(Page<Candidate> page) {
        return candidateService.page(page);
    }

}
