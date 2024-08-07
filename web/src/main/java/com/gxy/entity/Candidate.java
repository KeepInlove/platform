package com.gxy.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  实体类。
 *
 * @author Gxy
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "public.candidate",dataSource = "postgresql-datasource")
public class Candidate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自动递增
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 工作经验
     */
    private String workExperience;

    /**
     * 学历
     */
    private String education;

    /**
     * 到岗时间
     */
    private String arrivalTime;

    /**
     * 个人优势描述
     */
    private String personalAdvantages;

    /**
     * 专业技能
     */
    private String professionalSkills;

    /**
     * 学校
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 在校经历
     */
    private String schoolExperience;

    /**
     * 关联 hr 表的 id
     */
    private Integer bossId;

    /**
     * 附件简历
     */
    private String resumeAttachment;

    /**
     * 接触状态 (0,1,2,3,4,5)
     */
    private Integer contactStatus;

    /**
     * 打招呼链接
     */
    private String contactLink;

    /**
     * 同步来自职位 id
     */
    private Integer jobId;

    /**
     * 评分
     */
    private Integer score;

}
