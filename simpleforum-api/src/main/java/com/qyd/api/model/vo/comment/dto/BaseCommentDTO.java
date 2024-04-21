package com.qyd.api.model.vo.comment.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 评论树状结构
 *
 * @author 邱运铎
 * @date 2024-04-18 17:55
 */
@Data
public class BaseCommentDTO implements Comparable<BaseCommentDTO>{

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评论用户姓名
     */
    private String userName;

    /**
     * 评论用户头像
     */
    private String userPhoto;

    /**
     * 评论时间
     */
    private Long commentTime;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 点赞的数量
     */
    private Integer praiseCount;

    /**
     * true 表示已点赞
     */
    private Boolean praised;

    @Override
    public int compareTo(@NotNull BaseCommentDTO o) {
        return Long.compare(o.getCommentTime(), this.commentTime);
    }
}
