package com.gxy.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用于存储模型请求和响应结果的表 实体类。
 *
 * @author Gxy
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "model_requests_resp",dataSource = "mysql-datasource")
public class ModelRequestsRespEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增的主键
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 请求的唯一标识符
     */
    private String requestId;

    /**
     * 提问内容
     */
    private String question;

    /**
     * 提问者
     */
    private String roleDesc;

    /**
     * 响应内容
     */
    private String response;

    /**
     * 输入的 tokens 数量
     */
    private Integer inputTokens;

    /**
     * 输出的 tokens 数量
     */
    private Integer outputTokens;

    /**
     * 总的 tokens 数量
     */
    private Integer totalTokens;

    /**
     * 请求时间
     */
    private Timestamp requestTime;

    /**
     * 响应时间
     */
    private Timestamp responseTime;

    /**
     * 记录的创建时间，默认为当前时间
     */
    private Timestamp createdAt;

    /**
     * 记录的最后更新时间，自动更新为当前时间
     */
    private Timestamp updatedAt;

    private String sessionId;

}
