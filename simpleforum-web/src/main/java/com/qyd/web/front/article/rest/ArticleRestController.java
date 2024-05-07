package com.qyd.web.front.article.rest;

import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.ArticleWriteService;
import com.qyd.service.article.service.CategoryService;
import com.qyd.service.article.service.TagService;
import com.qyd.service.user.service.UserFootService;
import com.qyd.web.component.TemplateEngineHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 返回json的文章相关数据
 *
 * @author 邱运铎
 * @date 2024-05-04 19:46
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "article/api")
public class ArticleRestController {
    private final UserFootService userFootService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final ArticleReadService articleReadService;
    private final ArticleWriteService articleWriteService;
    private final TemplateEngineHelper templateEngineHelper;

}
