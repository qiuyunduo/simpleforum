package com.qyd.service.article.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleAdminDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.article.dto.YearArticleDTO;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.ReadCountDO;
import com.qyd.service.article.repository.params.SearchArticleParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章 mapper 接口
 *
 * @author 邱运铎
 * @date 2024-04-10 22:53
 */
public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * 通过id遍历文章，用于生成sitemap.xml
     *
     * @param lastId
     * @param size
     * @return
     */
    List<SimpleArticleDTO> listArticlesOrderById(@Param("lastId") Long lastId, @Param("size") int size);

    /**
     * 根据阅读次数获取热门文章
     *
     * @param pageParam
     * @return
     */
    List<SimpleArticleDTO> listArticlesByReadCounts(@Param("pageParam")PageParam pageParam);

    /**
     * 查询作者的热门文章
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<SimpleArticleDTO> listArticlesByUserIdOrderByReadCounts(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);

    /**
     * 根据类目Id + 标签Id 查询文章
     *
     * @param categoryId
     * @param tagIds
     * @param pageParam
     * @return
     */
    List<ReadCountDO> listArticleByCategoryAndTags(@Param("categoryId") Long categoryId,
                                                   @Param("tagsIds") List<Long> tagIds,
                                                   @Param("pageParams") PageParam pageParam);

    /**
     * 根据用户Id获取创作历程
     *
     * @param userId
     * @return
     */
    List<YearArticleDTO> listYearArticleByUserId(@Param("userId") Long userId);

    /**
     * 根据文章查询条件查询文章 --服务于后台管理
     *
     * @param searchArticleParams
     * @param param
     * @return
     */
    List<ArticleAdminDTO> listArticlesByParams(@Param("searchParams")SearchArticleParams searchArticleParams,
                                               @Param("pageParam") PageParam param);

    /**
     * 根据文章查询条件返回文章总数
     *
     * @param searchArticleParams
     * @return
     */
    Long countArticlesByParams(@Param("searchParams") SearchArticleParams searchArticleParams);
}
