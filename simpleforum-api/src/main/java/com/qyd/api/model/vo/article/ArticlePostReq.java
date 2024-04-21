package com.qyd.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import com.qyd.api.model.enums.ArticleTypeEnum;
import com.qyd.api.model.enums.SourceTypeEnum;
import com.qyd.api.model.enums.PushStatusEnum;

/**
 * 发布文章时的请求参数
 *
 * @author 邱运铎
 * @date 2024-04-11 0:10
 */
@Data
public class ArticlePostReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID, 当存在是，表示更新文章
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
     * 分类
     */
    private Long categoryId;

    /**
     * 文章所有标签
     */
    private Set<Long> tagIds;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 封面
     */
    private String cover;

    /**
     * 文章类型
     *
     * @see ArticleTypeEnum
     */
    private String articleType;

    /**
     * 来源： 1-转载，2-原创，3-翻译
     *
     * @see SourceTypeEnum
     */
    private Integer source;

    /**
     * 状态： 0-未发布，1-已发布
     *
     * @see PushStatusEnum
     */
    private Integer status;

    /**
     * 原文地址
     */
    private String sourceUrl;

    /**
     * POST 发表 SAVE 暂存 DELETE 删除
     */
    private String actionType;

    /**
     * 专栏序号
     */
    private Long columnId;

    public PushStatusEnum pushStatus() {
        if ("post".equalsIgnoreCase(actionType)) {
            return PushStatusEnum.ONLINE;
        } else {
            return PushStatusEnum.OFFLINE;
        }
    }

    public boolean deleted() {
        return "delete".equalsIgnoreCase(actionType);
    }
}
