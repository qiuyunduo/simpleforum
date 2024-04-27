package com.qyd.service.article.service;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.service.article.repository.entity.ColumnArticleDO;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-27 22:09
 */
public interface ColumnService {

    /**
     * 根据文章id, 构建对应的专栏详情地址
     *
     * @param articleId 文章主键
     * @return   专栏详情页
     */
    ColumnArticleDO getColumnArticleRelation(Long articleId);

    /**
     * 专栏列表
     *
     * @param pageParam
     * @return
     */
    PageListVo<ColumnDTO> listColumn(PageParam pageParam);

    /**
     * 获取专栏中的第N篇文章
     *
     * @param columnId
     * @param order
     * @return
     */
    ColumnArticleDO queryColumnArticle(long columnId, Integer order);

    /**
     * 只查询基本的专栏信息，不需要统计，作者等信息
     *
     * @param columnId
     * @return
     */
    ColumnDTO queryBasicColumnInfo(Long columnId);

    /**
     * 专栏详情
     *
     * @param columnId
     * @return
     */
    ColumnDTO queryColumInfo(Long columnId);

    /**
     * 专栏下的文章列表
     *
     * @param columnId
     * @return
     */
    List<SimpleArticleDTO> queryColumnArticles(Long columnId);

    /**
     * 返回教程数量
     *
     * @return
     */
    Long getTutorialCount();
}
