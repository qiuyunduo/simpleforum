package com.qyd.service.article.service.impl;

import com.qyd.api.model.enums.HomeSelectEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.dao.ArticleTagDao;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.ArticleTagDO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 文章查询相关服务类
 *
 * @author 邱运铎
 * @date 2024-04-10 22:50
 */
@Service
public class ArticleReadServiceImpl implements ArticleReadService {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleTagDao articleTagDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ArticleDO queryBasicArticle(Long articleId) {
        return null;
    }

    @Override
    public String generateSummary(String content) {
        return null;
    }

    @Override
    public ArticleDTO queryDetailArticleInfo(Long articleId) {
        return null;
    }

    @Override
    public ArticleDTO queryFullArticleInfo(Long articleId, Long currentUser) {
        return null;
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesByCategory(Long categoryId, PageParam page) {
        return null;
    }

    @Override
    public List<ArticleDTO> queryTopArticlesByCategory(Long categoryId) {
        return null;
    }

    @Override
    public Long queryArticleCountByCategory(Long categoryId) {
        return null;
    }

    @Override
    public Map<Long, Long> queryArticleCountsByCategory() {
        return null;
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesByTag(Long tagId, PageParam param) {
        return null;
    }

    @Override
    public List<SimpleArticleDTO> querySimpleArticleBySearchKey(String key) {
        return null;
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesBySearchKey(String key, PageParam param) {
        return null;
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesByUserAndType(Long userId, PageParam param, HomeSelectEnum select) {
        return null;
    }

    @Override
    public PageListVo<ArticleDTO> buildArticleListVo(List<ArticleDO> records, long pageSize) {
        return null;
    }

    @Override
    public PageListVo<SimpleArticleDTO> queryHotArticlesForRecommend(PageParam param) {
        return null;
    }

    @Override
    public Long getArticleCount() {
        return null;
    }
}
