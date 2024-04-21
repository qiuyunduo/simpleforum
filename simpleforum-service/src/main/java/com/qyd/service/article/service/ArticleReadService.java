package com.qyd.service.article.service;

import com.qyd.api.model.enums.HomeSelectEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.qyd.service.article.repository.entity.ArticleDO;

import java.util.List;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-04-10 21:16
 */
public interface ArticleReadService {

    /**
     * 查询基础的文章信息
     *
     * @param articleId
     * @return
     */
    ArticleDO queryBasicArticle(Long articleId);

    /**
     * 提取文章摘要
     *
     * @param content
     * @return
     */
    String generateSummary(String content);

    /**
     * 查询文章详情，包括正文内容，分类，标签等信息
     *
     * @param articleId
     * @return
     */
    ArticleDTO queryDetailArticleInfo(Long articleId);

    /**
     * 查询文章所有的关联信息, 正文，分类，标签，阅读计数+1，
     * 当前登录用户是否点赞、评论过
     *
     * @param articleId 文章id
     * @param currentUser 当前查看的用户id
     * @return
     */
    ArticleDTO queryFullArticleInfo(Long articleId, Long currentUser);

    /**
     * 查询某个分类下的文章，支持翻页
     *
     * @param categoryId
     * @param page
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesByCategory(Long categoryId, PageParam page);

    /**
     * 获取当前分类下的 Top 文章
     *
     * @param categoryId
     * @return
     */
    List<ArticleDTO> queryTopArticlesByCategory(Long categoryId);

    /**
     * 获取分类文章计数
     *
     * @param categoryId
     * @return
     */
    Long queryArticleCountByCategory(Long categoryId);

    /**
     * 根据分类统计文章计数
     *
     * @return
     */
    Map<Long, Long>  queryArticleCountsByCategory();

    /**
     * 查询某个标签下的文章，支持翻页
     *
     * @param tagId
     * @param param
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesByTag(Long tagId, PageParam param);

    /**
     * 根据关键词匹配标题，查询用于推荐的文章列表，只返回 articleId + title
     *
     * @param key
     * @return
     */
    List<SimpleArticleDTO> querySimpleArticleBySearchKey(String key);

    /**
     * 根据关键词查询文章列表，支持翻页
     *
     * @param key
     * @param param
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesBySearchKey(String key, PageParam param);

    /**
     * 查询用户的文章列表
     *
     * @param userId
     * @param param
     * @param select
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesByUserAndType(Long userId, PageParam param, HomeSelectEnum select);

    /**
     * 文章实体补齐 统计、 作者、分类、标签等信息
     *
     * @param records
     * @param pageSize
     * @return
     */
    PageListVo<ArticleDTO> buildArticleListVo(List<ArticleDO> records, long pageSize);

    /**
     * 查询热门文章 支持翻页
     *
     * @param param
     * @return
     */
    PageListVo<SimpleArticleDTO> queryHotArticlesForRecommend(PageParam param);

    /**
     * 返回总的文章计数
     *
     * @return
     */
    Long getArticleCount();
}
