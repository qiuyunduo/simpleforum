package com.qyd.api.model.vo.user.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 用户主页信息 - 文章相关统计信息
 *
 * @author 邱运铎
 * @date 2024-04-16 0:47
 */
@Data
@ToString(callSuper = true)
public class UserFootStatisticDTO {

    /**
     * 文章被点赞数量
     */
    private Long praiseCount;

    /**
     * 文章被阅读数
     */
    private Long readCount;

    /**
     * 文章被收藏数
     */
    private Long collectionCount;

    /**
     * 文章被评论数
     */
    private Long commentCount;

    public UserFootStatisticDTO() {
        praiseCount = 0L;
        readCount = 0L;
        collectionCount = 0L;
        commentCount = 0L;
    }
}
