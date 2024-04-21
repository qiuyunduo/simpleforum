package com.qyd.service.user.service.userfoot;

import com.qyd.api.model.enums.DocumentTypeEnum;
import com.qyd.api.model.enums.OperateTypeEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.qyd.api.model.vo.user.dto.UserFootStatisticDTO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.comment.repository.entity.CommentDO;
import com.qyd.service.comment.service.CommentReadService;
import com.qyd.service.user.repository.dao.UserFootDao;
import com.qyd.service.user.repository.entity.UserFootDO;
import com.qyd.service.user.service.UserFootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 用户足迹Service
 *
 * @author 邱运铎
 * @date 2024-04-16 0:52
 */
@Service
public class UserFootServiceImpl implements UserFootService {
    private final UserFootDao userFootDao;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private CommentReadService commentReadService;

    public UserFootServiceImpl(UserFootDao userFootDao) {
        this.userFootDao = userFootDao;
    }

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
    @Override
    public UserFootDO saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum) {
        // 查询是否有该足迹， 有则更新，无则插入
        UserFootDO userFootDO = userFootDao.getByDocumentAndUserId(documentId, documentType.getCode(), userId);
        if (userFootDO == null) {
            userFootDO = new UserFootDO();
            userFootDO.setUserId(userId);
            userFootDO.setDocumentId(documentId);
            userFootDO.setDocumentType(documentType.getCode());
            userFootDO.setDocumentUserId(authorId);
            setUserFootStat(userFootDO, operateTypeEnum);
            userFootDao.save(userFootDO);
        } else {
            userFootDO.setUpdateTime(new Date());
            userFootDao.updateById(userFootDO);
        }
        return userFootDO;
    }

    private boolean setUserFootStat(UserFootDO userFootDO, OperateTypeEnum operate) {
        switch (operate) {
            case READ:
                // 设置为已读
                userFootDO.setReadStat(1);
                // 需要更新时间，用于浏览记录
                return false;
            case PRAISE:
            case CANCEL_PRAISE:
                return compareAndUpdate(userFootDO::getPraiseStat, userFootDO::setPraiseStat, operate.getDbStatCode());
            case COLLECTION:
            case CANCEL_COLLECTION:
                return compareAndUpdate(userFootDO::getCollectionStat, userFootDO::setCollectionStat, operate.getDbStatCode());
            case COMMENT:
            case DELETE_COMMENT:
                return compareAndUpdate(userFootDO::getCommentStat, userFootDO::setCommentStat, operate.getDbStatCode());
            default:
                return false;
        }
    }

    /**
     * 相同则直接返回false不用更新， 不同则更新，返回true
     *
     * @param supplier
     * @param consumer
     * @param input
     * @return
     */
    private <T> boolean compareAndUpdate(Supplier<T> supplier, Consumer<T> consumer, T input) {
        if (Objects.equals(supplier.get(), input)) {
            return false;
        }
        consumer.accept(input);
        return true;
    }

    @Override
    public void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
        // 保存文章对应的评论足迹
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        // 如果是子评论，则需要找到父评论记录，然后设置为已经被回复
        if (comment.getParentCommentId() != null && comment.getParentCommentId() != 0) {
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        }
    }

    @Override
    public void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        if (comment.getParentCommentId() != null) {
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        }
    }

    @Override
    public List<Long> queryUserReadArticleList(Long userId, PageParam pageParam) {
        return userFootDao.listReadArticleByUserId(userId, pageParam);
    }

    @Override
    public List<Long> queryUserCollectionArticleList(Long userId, PageParam pageParam) {
        return userFootDao.listCollectedArticlesByUserId(userId, pageParam);
    }

    @Override
    public List<SimpleUserInfoDTO> queryArticlePraisedUsers(Long articleId) {
        return userFootDao.listDocumentPraisedUsers(articleId, DocumentTypeEnum.ARTICLE.getCode(), 10);
    }

    @Override
    public UserFootDO queryUserFoot(Long documentId, Integer type, Long userId) {
        return userFootDao.getByDocumentAndUserId(documentId, type, userId);
    }

    @Override
    public UserFootStatisticDTO getFootCount() {
        return userFootDao.getFootCount();
    }
}
