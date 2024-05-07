package com.qyd.service.article.service;

import com.qyd.api.model.vo.article.ArticlePostReq;

/**
 * @author 邱运铎
 * @date 2024-05-05 17:21
 */
public interface ArticleWriteService {

    /**
     * 保存or更新文章
     *
     * @param req       上传的文章
     * @param author    作者
     * @return          返回文章主键
     */
    Long saveArticle(ArticlePostReq req, Long author);

    /**
     * 删除文章
     *
     * @param articleId     文章id
     * @param loginUserId   执行操作的用户
     */
    void deleteArticle(Long articleId, Long loginUserId);
}
