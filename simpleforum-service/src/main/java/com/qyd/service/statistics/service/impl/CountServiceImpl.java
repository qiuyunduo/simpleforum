package com.qyd.service.statistics.service.impl;

import com.qyd.api.model.vo.user.dto.ArticleFootCountDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.qyd.core.cache.RedisClient;
import com.qyd.core.util.MapUtils;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.comment.service.CommentReadService;
import com.qyd.service.statistics.constants.CountConstants;
import com.qyd.service.statistics.service.CountService;
import com.qyd.service.user.repository.dao.UserDao;
import com.qyd.service.user.repository.dao.UserFootDao;
import com.qyd.service.user.repository.dao.UserRelationDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 计数服务，后续计数相关的可以考虑基于redis来做
 *
 * @author 邱运铎
 * @date 2024-04-20 20:31
 */
@Slf4j
@Service
public class CountServiceImpl implements CountService {
    private final UserFootDao userFootDao;

    @Resource
    private UserRelationDao userRelationDao;

    @Resource
    private ArticleDao articleDao;

    @Resource
    private CommentReadService commentReadService;

    @Resource
    private UserDao userDao;

    public CountServiceImpl(UserFootDao userFootDao) {
        this.userFootDao = userFootDao;
    }
    @Override
    public ArticleFootCountDTO queryArticleCountInfoByArticleId(Long articleId) {
        ArticleFootCountDTO res = userFootDao.countArticleByArticleId(articleId);
        if (res == null) {
            res = new ArticleFootCountDTO();
        } else {
            res.setCommentCount(commentReadService.queryCommentCount(articleId));
        }
        return res;
    }

    @Override
    public ArticleFootCountDTO queryArticleCountInfoByUserId(Long userId) {
        return userFootDao.countArticleByUserId(userId);
    }

    /**
     * 查询评论的点赞数
     *
     * @param commentId
     * @return
     */
    @Override
    public Long queryCommentPraiseCount(Long commentId) {
        return userFootDao.countCommentPraise(commentId);
    }

    @Override
    public UserStatisticInfoDTO queryUserStatisticInfo(Long userId) {
        Map<String, Integer> ans = RedisClient.hGetAll(CountConstants.USER_STATISTIC_INFO + userId, Integer.class);
        UserStatisticInfoDTO info = new UserStatisticInfoDTO();
        info.setFollowCount(ans.getOrDefault(CountConstants.FOLLOW_COUNT, 0));
        info.setArticleCount(ans.getOrDefault(CountConstants.ARTICLE_COUNT, 0));
        info.setPraiseCount(ans.getOrDefault(CountConstants.PRAISE_COUNT, 0));
        info.setCollectionCount(ans.getOrDefault(CountConstants.COLLECTION_COUNT, 0));
        info.setReadCount(ans.getOrDefault(CountConstants.READ_COUNT, 0));
        info.setFansCount(ans.getOrDefault(CountConstants.FANS_COUNT, 0));
        return info;
    }

    @Override
    public ArticleFootCountDTO queryArticleStatisticInfo(Long articleId) {
        Map<String, Integer> ans = RedisClient.hGetAll(CountConstants.ARTICLE_STATISTIC_INFO + articleId, Integer.class);
        ArticleFootCountDTO info = new ArticleFootCountDTO();
        info.setPraiseCount(ans.getOrDefault(CountConstants.PRAISE_COUNT, 0));
        info.setCollectionCount(ans.getOrDefault(CountConstants.COLLECTION_COUNT, 0));
        info.setCommentCount(ans.getOrDefault(CountConstants.COMMENT_COUNT, 0));
        info.setReadCount(ans.getOrDefault(CountConstants.READ_COUNT, 0));
        return info;
    }

    @Override
    public void incrArticleReadCount(Long authorUserId, Long articleId) {
        // db层的文章阅读计数 + 1
        articleDao.incrReadCount(articleId);
        // redis计数器 + 1
        RedisClient.pipelineAction()
                .add(CountConstants.ARTICLE_STATISTIC_INFO + articleId, CountConstants.READ_COUNT,
                        ((redisConnection, key, value) -> redisConnection.hIncrBy(key, value, 1)))
                .execute();
    }

    /**
     * 每天4:15分执行定时任务，全量刷新用户统计信息
     */
    @Scheduled(cron = "0 15 4 * * ?")
    public void autoRefreshAllUserStatisticInfo() {
        Long now = System.currentTimeMillis();
        log.info("开始自动刷新用户统计信息");
        Long userId= 0L;
        int batchSize = 20;
        while (true) {
            List<Long> userIds = userDao.scanUserId(userId, batchSize);
            userIds.forEach(this::refreshUserStatisticInfo);
            if (userIds.size() < batchSize) {
                userId = userIds.get(userIds.size() - 1);
                break;
            } else {
                userId = userIds.get(batchSize - 1);
            }
        }
        log.info("结束自动刷新用户统计信息，共耗时: {}ms, maxUserId: {}", System.currentTimeMillis() - now, userId);
    }

    /**
     * 更新用户的统计信息
     *
     * @param userId
     */
    @Override
    public void refreshUserStatisticInfo(Long userId) {
        // 用户的文章点赞数，收藏数，阅读计数
        ArticleFootCountDTO count = userFootDao.countArticleByUserId(userId);
        if (count == null) {
            count = new ArticleFootCountDTO();
        }

        Long followCount = userRelationDao.queryUserFollowCount(userId);
        Long fansCount = userRelationDao.queryUserFansCount(userId);
        // 查询用户发布的文章数
        Integer articleNum = articleDao.countArticleByUser(userId);
        String key = CountConstants.USER_STATISTIC_INFO + userId;
        /**
         * todo 这个项目对于每次的数据更新，只要是在redis有缓存的，都需要同步更新缓存
         *  关于redis缓存或者做其他功能，后面有深的体会再回头来看。
         */
        RedisClient.hMSet(key, MapUtils.create(CountConstants.PRAISE_COUNT, count.getPraiseCount(),
                CountConstants.COLLECTION_COUNT, count.getCollectionCount(),
                CountConstants.READ_COUNT, count.getReadCount(),
                CountConstants.FANS_COUNT, fansCount,
                CountConstants.FOLLOW_COUNT, followCount,
                CountConstants.ARTICLE_COUNT, articleNum));
    }

    @Override
    public void refreshArticleStatisticInfo(Long articleId) {
        ArticleFootCountDTO res = userFootDao.countArticleByArticleId(articleId);
        if (res == null) {
            res = new ArticleFootCountDTO();
        } else {
            res.setCommentCount(commentReadService.queryCommentCount(articleId));
        }

        RedisClient.hMSet(CountConstants.ARTICLE_STATISTIC_INFO + articleId,
                MapUtils.create(CountConstants.COLLECTION_COUNT, res.getCollectionCount(),
                        CountConstants.PRAISE_COUNT, res.getPraiseCount(),
                        CountConstants.READ_COUNT, res.getReadCount(),
                        CountConstants.COMMENT_COUNT, res.getCommentCount()));
    }
}
