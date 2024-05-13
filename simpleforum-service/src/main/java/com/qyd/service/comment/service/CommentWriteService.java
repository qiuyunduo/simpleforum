package com.qyd.service.comment.service;

import com.qyd.api.model.vo.comment.CommentSaveReq;

/**
 * 评论service接口
 *
 * @author 邱运铎
 * @date 2024-05-09 15:01
 */
public interface CommentWriteService {

    /**
     * 更新/保存评论
     *
     * @param commentSaveReq
     * @return
     */
    Long saveComment(CommentSaveReq commentSaveReq);

    /**
     * 删除评论
     *
     * @param commentId
     * @param userId
     */
    void deleteComment(Long commentId, Long userId);
}
