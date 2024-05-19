package com.qyd.service.article.service;

import com.qyd.api.model.enums.OperateArticleEnum;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.ArticlePostReq;
import com.qyd.api.model.vo.article.SearchArticleReq;
import com.qyd.api.model.vo.article.dto.ArticleAdminDTO;

/**
 * 文章后台接口
 *
 * @author 邱运铎
 * @date 2024-05-18 16:17
 */
public interface ArticleSettingService {

    /**
     * 更新文章
     *
     * @param req
     */
    void updateArticle(ArticlePostReq req);

    /**
     * 获取文章列表
     *
     * @param req
     * @return
     */
    PageVo<ArticleAdminDTO> getArticleList(SearchArticleReq req);

    /**
     * 删除文章
     *
     * @param articleId
     */
    void deleteArticle(Long articleId);

    /**
     * 操作文章
     *
     * @param articleId
     * @param operate
     */
    void operateArticle(Long articleId, OperateArticleEnum operate);
}
