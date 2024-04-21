package com.qyd.service.user.service;

import com.qyd.api.model.enums.DocumentTypeEnum;
import com.qyd.api.model.enums.OperateTypeEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.qyd.api.model.vo.user.dto.UserFootStatisticDTO;
import com.qyd.service.comment.repository.entity.CommentDO;
import com.qyd.service.user.repository.entity.UserFootDO;

import java.util.List;

/**
 * 用户足迹Service接口
 *
 * @author 邱运铎
 * @date 2024-04-15 23:24
 */
public interface UserFootService {

    /**
     * 保存或更新状态信息
     *
     * @param documentType      文档类型： 文章 / 评论
     * @param documentId        文档ID
     * @param authorId          作者
     * @param userId            操作人
     * @param operateTypeEnum   操作类型： 点赞 收藏 评论等
     * @return
     */
    UserFootDO saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum);

    /**
     * 保存评论足迹
     * 1. 用户文章记录上，设置为已评论
     * 2. 若该评论为回复别人的评论， 则针对父评论设置已评论
     *
     * @param comment               保存的评论入参
     * @param articleAuthor         文章作者
     * @param parentCommentAuthor   父评论作者
     */
    void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);

    /**
     * 删除评论足迹
     *
     * @param comment               保存的评论入参
     * @param articleAuthor         文章作者
     * @param parentCommentAuthor   父评论作者
     */
    void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);

    /**
     * 查询用户的已读文章列表
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> queryUserReadArticleList(Long userId, PageParam pageParam);

    /**
     * 查询用户的收藏文章列表
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> queryUserCollectionArticleList(Long userId, PageParam pageParam);

    /**
     * 查询文章的点赞用户信息
     *
     * @param articleId
     * @return
     */
    List<SimpleUserInfoDTO> queryArticlePraisedUsers(Long articleId);

    /**
     * 查询用户针对指定文章的操作记录，判断是否对指定文章进行过点赞，评论，收藏操作。
     *
     * @param documentId    文章ID
     * @param type          操作类型 点赞 收藏 评论
     * @param userId        用户ID
     * @return
     */
    UserFootDO queryUserFoot(Long documentId, Integer type, Long userId);

    UserFootStatisticDTO getFootCount();
}
