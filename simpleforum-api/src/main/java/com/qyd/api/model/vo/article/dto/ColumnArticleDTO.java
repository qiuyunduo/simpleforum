package com.qyd.api.model.vo.article.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 教程内的文章推荐
 *
 * @author 邱运铎
 * @date 2024-04-27 22:54
 */
@Data
@Accessors(chain = true)
public class ColumnArticleDTO implements Serializable {
    private static final long serialVersionUID = 5466004562676306173L;

    /**
     * 唯一ID
     */
    private Long id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章短标题
     */
    private String shortTitle;

    /**
     * 专栏Id
     */
    private Long columnId;

    /**
     * 专栏标题
     */
    private String column;

    /**
     * 专栏封面
     */
    private String columnCover;

    /**
     * 文章排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Timestamp createTime;
}
