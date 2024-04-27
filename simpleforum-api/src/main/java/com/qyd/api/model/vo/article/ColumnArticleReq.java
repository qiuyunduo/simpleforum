package com.qyd.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 保存Column 文章请求参数
 *
 * @author 邱运铎
 * @date 2024-04-28 0:15
 */
@Data
public class ColumnArticleReq implements Serializable {
    private static final long serialVersionUID = 5878581287626018289L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 专栏id
     */
    private Long columnId;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章排序字段
     */
    private Integer sort;

    /**
     * 文章短标题
     */
    private String shortTitle;

    /**
     * 专栏类型，免费，登录，付费
     */
    private Integer type;
}
