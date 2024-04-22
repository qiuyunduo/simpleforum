package com.qyd.service.rank.service.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 邱运铎
 * @date 2024-04-22 22:09
 */
@Data
@Accessors(chain = true)
public class ActivityScoreBo {
    /**
     * 访问页面增加活跃度，访问的页面
     */
    private String path;

    /**
     * 点赞，评论，收藏的目标文章
     */
    private Long articleId;

    /**
     * 评论增加活跃度
     */
    private Boolean rate;

    /**
     * 点赞增加活跃度
     */
    private Boolean praise;

    /**
     * 收藏文章增加活跃度
     */
    private Boolean collect;

    /**
     * 发布文章增加活跃度
     */
    private Boolean publishArticle;

    /**
     * 被关注的用户
     */
    private Long followedUserId;

    /**
     * 关注其他用户增加活跃度
     */
    private Boolean follow;
}
