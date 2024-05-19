package com.qyd.service.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qyd.api.model.enums.ArticleEventEnum;
import com.qyd.api.model.enums.OperateArticleEnum;
import com.qyd.api.model.enums.PushStatusEnum;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.event.ArticleMsgEvent;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.ArticlePostReq;
import com.qyd.api.model.vo.article.SearchArticleReq;
import com.qyd.api.model.vo.article.dto.ArticleAdminDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.article.conveter.ArticleConverter;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.dao.ColumnArticleDao;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.repository.params.SearchArticleParams;
import com.qyd.service.article.service.ArticleSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 文章操作管理后台
 *
 * @author 邱运铎
 * @date 2024-05-18 16:37
 */
@Service
public class ArticleSettingServiceImpl implements ArticleSettingService {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ColumnArticleDao columnArticleDao;

    /**
     * CacheEvict 清除缓存，清除指定缓存
     *
     * @param req
     */
    @Override
    @CacheEvict(key = "'sideBar_' + #req.articleId", cacheNames = "article", cacheManager = "caffeineCacheManager")
    public void updateArticle(ArticlePostReq req) {
        if (!PushStatusEnum.isPushStatusCode(req.getStatus())) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "发布状态不合法");
        }
        ArticleDO article = articleDao.getById(req.getArticleId());
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.RECORDS_NOT_EXISTS, "文章不存在");
        }

        if (StringUtils.isNotBlank(req.getTitle())) {
            article.setTitle(req.getTitle());
        }
        if (StringUtils.isNotBlank(req.getShortTitle())) {
            article.setShortTitle(req.getShortTitle());
        }
        article.setStatus(req.getStatus());
        ArticleEventEnum operateEvent = ArticleEventEnum.typeOf(PushStatusEnum.fromCode(req.getStatus()).toString());
        articleDao.updateById(article);
        if (operateEvent != null) {
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, operateEvent, article));
        }

    }

    @Override
    public PageVo<ArticleAdminDTO> getArticleList(SearchArticleReq req) {
        // 转换参数，从前端获取的参数转换为数据库查询的参数
        SearchArticleParams searchArticleParams = ArticleConverter.toSearchParams(req);

        // 查询文章列表，分页
        List<ArticleAdminDTO> articleDTOS = articleDao.listArticlesByParams(searchArticleParams);

        // 查询文章总数
        Long totalCount = articleDao.countArticleByParams(searchArticleParams);
        return PageVo.build(articleDTOS, req.getPageSize(), req.getPageNumber(), totalCount);
    }

    @Override
    public void deleteArticle(Long articleId) {
        ArticleDO dto = articleDao.getById(articleId);
        if (dto != null && dto.getDeleted() != YesOrNoEnum.YES.getCode()) {
            // 查询改文章是否关联了教程，如果已经关联了教程，则不能删除
            long count = columnArticleDao.count(Wrappers
                    .<ColumnArticleDO>lambdaQuery()
                    .eq(ColumnArticleDO::getArticleId, articleId));

            if (count > 0) {
                throw ExceptionUtil.of(StatusEnum.ARTICLE_RELATION_TUTORIAL, articleId, "请先解除文章与教程的关联关系");
            }

            dto.setDeleted(YesOrNoEnum.YES.getCode());
            articleDao.updateById(dto);

            // 发布文章删除事件
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.DELETE, dto));
        } else {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }
    }

    @Override
    public void operateArticle(Long articleId, OperateArticleEnum operate) {
        ArticleDO articleDO = articleDao.getById(articleId);
        if (articleDO == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }
        setArticleStat(articleDO, operate);
        articleDao.updateById(articleDO);
    }

    public void setArticleStat(ArticleDO articleDO, OperateArticleEnum operate) {
        switch (operate) {
            case OFFICIAL:
            case CANCEL_OFFICIAL:
                compareAndUpdate(articleDO::getOfficialStat, articleDO::setOfficialStat, operate.getDbStatCode());
                return;
            case TOPPING:
            case CANCEL_TOPPING:
                compareAndUpdate(articleDO::getToppingStat, articleDO::setToppingStat, operate.getDbStatCode());
                return;
            case CREAM:
            case CANCEL_CREAM:
                compareAndUpdate(articleDO::getCreamStat, articleDO::setCreamStat, operate.getDbStatCode());
                return;
            default:
        }
    }

    /**
     * 相同者直接返回false不用更新，不同则更新，返回true
     *
     * @param supplier
     * @param consumer
     * @param input
     * @param <T>
     */
    private <T> void compareAndUpdate(Supplier<T> supplier, Consumer<T> consumer, T input) {
        if (Objects.equals(supplier.get(), input)) {
            return;
        }
        consumer.accept(input);
    }
}
