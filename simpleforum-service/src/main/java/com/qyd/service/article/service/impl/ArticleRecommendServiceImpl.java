package com.qyd.service.article.service.impl;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.dao.ArticleTagDao;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.ArticleTagDO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.ArticleRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 邱运铎
 * @date 2024-05-08 10:32
 */
@Service
public class ArticleRecommendServiceImpl implements ArticleRecommendService {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleTagDao articleTagDao;

    @Autowired
    private ArticleReadService articleReadService;

    /**
     * 查询文章关联推荐列表
     *
     * @param articleId
     * @param pageParam
     * @return
     */
    @Override
    public PageListVo<ArticleDTO> relatedRecommend(Long articleId, PageParam pageParam) {
        ArticleDO article = articleDao.getById(articleId);
        if (article == null) {
            return PageListVo.emptyVo();
        }
        List<Long> tagIds = articleTagDao.listArticleTags(articleId).stream()
                .map(ArticleTagDO::getTagId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagIds)) {
            return PageListVo.emptyVo();
        }

        List<ArticleDO> recommendArticles = articleDao.listRelatedArticlesOrderByReadCount(article.getCategoryId(), tagIds, pageParam);
        if (recommendArticles.removeIf(s -> s.getId().equals(articleId))) {
            // 移除推荐列表中的当前文章 个人认为这里要么在数据库处理掉这个当前文章不在结果集中，或者业务逻辑就认为当前文章出现在相关文章中也没有问题
            pageParam.setPageSize(pageParam.getPageSize() - 1);
        }
        return articleReadService.buildArticleListVo(recommendArticles, pageParam.getPageSize());
    }
}
