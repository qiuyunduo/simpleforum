package com.qyd.web.front.home.helper;

import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.CategoryService;
import com.qyd.service.config.service.ConfigService;
import com.qyd.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 首页推荐相关
 *
 * @author 邱运铎
 * @date 2024-04-08 17:28
 */
@Component
public class IndexRecommendHelper {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigService configService;







}
