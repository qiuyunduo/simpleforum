package com.qyd.service.article.service.impl;

import com.qyd.api.model.vo.article.ArticlePostReq;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.dao.ArticleTagDao;
import com.qyd.service.article.service.ArticleWriteService;
import com.qyd.service.article.service.ColumnSettingService;
import com.qyd.service.user.service.UserFootService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 文章操作相关服务
 *
 * @author 邱运铎
 * @date 2024-05-05 17:23
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleWriteServiceImpl implements ArticleWriteService {
    private ArticleDao articleDao;
    private ArticleTagDao articleTagDao;
    private ColumnSettingService columnSettingService;
    private UserFootService userFootService;



    @Override
    public Long saveArticle(ArticlePostReq req, Long author) {
        return null;
    }

    @Override
    public void deleteArticle(Long articleId, Long loginUserId) {

    }
}
