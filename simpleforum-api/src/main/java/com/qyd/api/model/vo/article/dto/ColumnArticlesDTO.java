package com.qyd.api.model.vo.article.dto;

import com.qyd.api.model.enums.column.ColumnTypeEnum;
import com.qyd.api.model.vo.comment.dto.TopCommentDTO;
import lombok.Data;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-25 12:12
 */
@Data
public class ColumnArticlesDTO {

    /**
     * 专栏详情
     */
    private Long column;

    /**
     * 当前正在查看的教程文章
     */
    private Integer section;

    /**
     * 教程文章详情
     */
    private ArticleDTO article;

    /**
     * 阅读类型
     * 0 免费阅读
     * 1 登录阅读
     * 2 限时免费， 若当前时间超过限时免费时间，则调整为登录阅读
     * 3 星球用户阅读
     *
     * @see ColumnTypeEnum#getType()
     */
    private Integer readType;

    /**
     * 文章评论
     */
    private List<TopCommentDTO> comments;

    /**
     * 热门评论
     */
    private TopCommentDTO hotComment;

    /**
     * 文章目录列表
     */
    private List<SimpleArticleDTO> articleList;

    /**
     * 翻页，暂未用到
     */
    private ArticleOtherDTO other;
}
