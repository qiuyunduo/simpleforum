package com.qyd.service.comment.service.impl;

import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.comment.CommentSaveReq;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.notify.NotifyMsgEvent;
import com.qyd.core.util.NumUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.comment.converter.CommentConverter;
import com.qyd.service.comment.repository.dao.CommentDao;
import com.qyd.service.comment.repository.entity.CommentDO;
import com.qyd.service.comment.service.CommentWriteService;
import com.qyd.service.user.service.UserFootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 评论Service
 *
 * @author 邱运铎
 * @date 2024-05-09 15:03
 */
@Service
public class CommentWriteServiceImpl implements CommentWriteService {
    @Resource
    private CommentDao commentDao;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private UserFootService userFootService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveComment(CommentSaveReq commentSaveReq) {
        // 保存评论
        CommentDO commentDO;
        if (NumUtil.nullOrZero(commentSaveReq.getCommentId())) {
            commentDO = addComment(commentSaveReq);
        } else {
            commentDO = updateComment(commentSaveReq);
        }
        return commentDO.getId();
    }

    private CommentDO addComment(CommentSaveReq commentSaveReq) {
        // 0. 获取父评论信息，校验是否存在
        Long parentCommentUser = getParentCommentUser(commentSaveReq.getParentCommentId());

        // 1. 保存评论内容
        CommentDO commentDO = CommentConverter.toDO(commentSaveReq);
        Date now = new Date();
        commentDO.setCreateTime(now);
        commentDO.setUpdateTime(now);
        commentDao.save(commentDO);

        // 2. 保存足迹信息： 文章的已评信息 + 评论的已评信息
        ArticleDO article = articleReadService.queryBasicArticle(commentSaveReq.getArticleId());
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, commentSaveReq.getArticleId());
        }
        userFootService.saveCommentFoot(commentDO, article.getUserId(), parentCommentUser);

        // 3. 发布添加/回复评论的事件
        SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.COMMENT, commentDO));
        if (NumUtil.upZero(parentCommentUser)) {
            // 评论回复事件
            SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.REPLY, commentDO));
        }
        return commentDO;
    }

    private CommentDO updateComment(CommentSaveReq commentSaveReq) {
        // 更新评论
        CommentDO commentDO = commentDao.getById(commentSaveReq.getCommentId());
        if (commentDO == null) {
            throw ExceptionUtil.of(StatusEnum.COMMENT_NOT_EXISTS, commentSaveReq.getCommentId());
        }
        commentDO.setContent(commentSaveReq.getCommentContent());
        commentDao.updateById(commentDO);
        return commentDO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        CommentDO commentDO = commentDao.getById(commentId);
        // 1. 校验评论，是否越权，文章是否存在
        if (commentDO == null) {
            throw ExceptionUtil.of(StatusEnum.COMMENT_NOT_EXISTS, "评论id = " + commentId);
        }
        if (Objects.equals(commentDO.getUserId(), userId)) {
            throw ExceptionUtil.of(StatusEnum.FORBID_ERROR_MIXED, "无权删除评论");
        }
        // 获取文章信息
        ArticleDO article = articleReadService.queryBasicArticle(commentDO.getArticleId());
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, "文章id = " + commentDO.getArticleId());
        }

        // 2. 删除评论，足迹
        commentDO.setDeleted(YesOrNoEnum.YES.getCode());
        commentDao.updateById(commentDO);
        userFootService.removeCommentFoot(commentDO, article.getUserId(), getParentCommentUser(commentDO.getParentCommentId()));

        // 3. 发布评论删除事件
        SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.DELETE_COMMENT, commentDO));
        if (NumUtil.upZero(commentDO.getParentCommentId())) {
            SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.DELETE_REPLY, commentDO));
        }
    }

    private Long getParentCommentUser(Long parentCommendId) {
        if (NumUtil.nullOrZero(parentCommendId)) {
            return null;
        }
        CommentDO parent = commentDao.getById(parentCommendId);
        if (parent == null) {
            throw ExceptionUtil.of(StatusEnum.COMMENT_NOT_EXISTS, "父评论id = " + parentCommendId);
        }
        return parent.getUserId();
    }
}
