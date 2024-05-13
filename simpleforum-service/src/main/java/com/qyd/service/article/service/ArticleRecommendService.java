package com.qyd.service.article.service;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleDTO;

/**
 * @author 邱运铎
 * @date 2024-05-08 10:31
 */
public interface ArticleRecommendService {

    /**
     * 文章关联推荐
     *
     * @param article
     * @param pageParam
     * @return
     */
    PageListVo<ArticleDTO> relatedRecommend(Long article, PageParam pageParam);
}
