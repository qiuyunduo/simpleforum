package com.qyd.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import com.qyd.api.model.enums.column.ColumnArticleReadEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专栏文章
 *
 * @author 邱运铎
 * @date 2024-04-27 22:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("column_article")
public class ColumnArticleDO extends BaseDO {
    private static final long serialVersionUID = 2232112003340407618L;

    private Long columnId;

    private Long articleId;

    /**
     * 专栏排序字段，越小越靠前
     */
    private Integer section;

    /**
     * 专栏阅读类型
     *
     * @see ColumnArticleReadEnum#getRead()
     */
    private Integer readType;
}
