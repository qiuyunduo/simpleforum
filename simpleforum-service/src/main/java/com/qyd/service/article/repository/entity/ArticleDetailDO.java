package com.qyd.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章详情
 *
 * DO 对应数据库实体类
 *
 * @author 邱运铎
 * @date 2024-04-10 23:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_detail")
public class ArticleDetailDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章修改的版本号
     */
    private Long version;

    /**
     * 文章内容正文
     */
    private String content;

    private Integer deleted;
}
